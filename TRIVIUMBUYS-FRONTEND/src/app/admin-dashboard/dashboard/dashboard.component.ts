import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, NgIf, NgForOf, NgClass, DatePipe, DecimalPipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';
import Chart from 'chart.js/auto';

import { interval, Subscription, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { UserProfile } from '../../models/user-profile';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, 
    NgIf, 
    NgForOf, 
    NgClass, 
    DatePipe, 
    DecimalPipe, 
    RouterModule,
    FormsModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  // Dashboard data
  dashboardData: any;
  adminName: string = 'Admin';
  adminAvatar: string = 'assets/images/pratham.jpg';// Default avatar path
  currentDate: Date = new Date();
  currentTime: string = '';
  revenuePeriod: string = 'monthly';

  // Chart instances
  revenueChart: any;
  salesPieChart: any;

  // Component state
  loading: boolean = true;
  error: string | null = null;
  
  // Subscriptions
  private clockSubscription: Subscription | null = null;
  
  constructor(
    private adminService: AdminService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
    this.startClock();
    this.loadDashboardData();
    
    // Add animation class to table rows when they come into view
    this.setupScrollAnimations();
  }

  ngOnDestroy(): void {
    // Clean up charts when component is destroyed
    if (this.revenueChart) {
      this.revenueChart.destroy();
    }
    if (this.salesPieChart) {
      this.salesPieChart.destroy();
    }
    
    // Unsubscribe from clock updates
    if (this.clockSubscription) {
      this.clockSubscription.unsubscribe();
    }
  }

  /**
   * Load the user profile to display the admin's name
   */
  loadUserProfile(): void {
    // First try to get from localStorage for immediate display
    const storedUser = this.authService.getStoredUser();
    if (storedUser) {
      this.adminName = storedUser.name || 'Admin';
      this.adminAvatar = storedUser.avatar || 'assets/images/pratham.jpg';
    }
    
    // Then try to get from API (or mock)
    const token = localStorage.getItem('token');
    if (token) {
      this.authService.getUserProfile(token)
        .pipe(
          catchError(err => {
            console.warn('Using fallback profile data');
            // Fallback to default values
            return of({
              id: 1,
              name: 'Pratham',
              email: 'admin@triviumbuys.com',
              role: 'ADMIN',
              avatar: 'assets/images/pratham.jpg'
            } as UserProfile);
          })
        )
        .subscribe(profile => {
          this.adminName = profile.name || 'Admin';
          this.adminAvatar = profile.avatar || 'assets/images/pratham.jpg';
        });
    }
  }

  /**
   * Start a clock that updates every second to show the current time
   */
  startClock(): void {
    // Set initial time
    this.updateCurrentTime();
    
    // Update time every second
    this.clockSubscription = interval(1000).subscribe(() => {
      this.updateCurrentTime();
    });
  }

  /**
   * Update the current time display
   */
  updateCurrentTime(): void {
    const now = new Date();
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    const seconds = now.getSeconds().toString().padStart(2, '0');
    
    this.currentTime = `${hours}:${minutes}:${seconds}`;
  }

  /**
   * Load dashboard data from the API
   */
  loadDashboardData(): void {
    this.loading = true;
    this.error = null;
    
    const token = localStorage.getItem('token');
    if (token) {
      this.adminService.getDashboardOverview(token).subscribe({
        next: (res: any) => {
          this.dashboardData = res;
          this.loading = false;
          
          // Initialize charts after a short delay to ensure DOM is ready
          setTimeout(() => {
            this.createRevenueChart();
            this.createSalesPieChart();
          }, 100);
        },
        error: (err: any) => {
          this.loading = false;
          this.error = 'Failed to load dashboard data. Please try again.';
          console.error('Error loading dashboard:', err);
        }
      });
    } else {
      this.loading = false;
      this.error = 'Authentication token not found. Please log in again.';
    }
  }

  /**
   * Create and render the revenue chart using Chart.js
   */
  createRevenueChart(): void {
    const canvas = document.getElementById('revenueChart') as HTMLCanvasElement;
    if (!canvas) {
      console.error('Revenue chart canvas element not found');
      return;
    }

    // Clean up existing chart if it exists
    if (this.revenueChart) {
      this.revenueChart.destroy();
    }

    // Check if we have monthly revenue data
    const monthlyData = this.dashboardData?.revenuePerMonth || [];
    if (monthlyData.length === 0) {
      console.warn('No monthly revenue data available');
    }

    const months = [
      'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
      'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
    ];

    const gradientFill = canvas.getContext('2d')?.createLinearGradient(0, 0, 0, 400);
    if (gradientFill) {
      gradientFill.addColorStop(0, 'rgba(54, 162, 235, 0.5)');
      gradientFill.addColorStop(1, 'rgba(54, 162, 235, 0.0)');
    }

    this.revenueChart = new Chart(canvas, {
      type: 'line',
      data: {
        labels: months,
        datasets: [{
          label: 'Revenue (₹)',
          data: monthlyData,
          backgroundColor: gradientFill || 'rgba(54, 162, 235, 0.2)',
          borderColor: 'rgb(54, 162, 235)',
          borderWidth: 3,
          tension: 0.4,
          pointBackgroundColor: 'rgb(54, 162, 235)',
          pointBorderColor: '#fff',
          pointHoverBackgroundColor: '#fff',
          pointHoverBorderColor: 'rgb(54, 162, 235)',
          pointRadius: 5,
          pointHoverRadius: 8,
          fill: true,
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: {
          intersect: false,
          mode: 'index'
        },
        animation: {
          duration: 2000,
          easing: 'easeOutQuart'
        },
        plugins: {
          legend: {
            display: true,
            position: 'top',
            labels: {
              font: {
                family: "'Poppins', sans-serif",
                size: 13
              }
            }
          },
          tooltip: {
            enabled: true,
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            titleFont: {
              family: "'Poppins', sans-serif",
              size: 14
            },
            bodyFont: {
              family: "'Poppins', sans-serif",
              size: 13
            },
            padding: 12,
            borderWidth: 1,
            borderColor: 'rgba(255, 255, 255, 0.2)',
            callbacks: {
              label: function(context) {
                let label = context.dataset.label || '';
                if (label) {
                  label += ': ';
                }
                if (context.parsed.y !== null) {
                  label += '₹' + context.parsed.y.toLocaleString();
                }
                return label;
              }
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: {
              color: 'rgba(0, 0, 0, 0.05)'
            },
            ticks: {
              callback: function(value) {
                return '₹' + value.toLocaleString();
              },
              font: {
                family: "'Poppins', sans-serif",
                size: 11
              }
            }
          },
          x: {
            grid: {
              display: false
            },
            ticks: {
              font: {
                family: "'Poppins', sans-serif",
                size: 11
              }
            }
          }
        }
      }
    });
  }

  /**
   * Create and render the sales by category pie chart
   */
  createSalesPieChart(): void {
    // Rest of the method remains the same...
    const canvas = document.getElementById('salesPieChart') as HTMLCanvasElement;
    if (!canvas) {
      console.error('Sales pie chart canvas element not found');
      return;
    }

    if (this.salesPieChart) {
      this.salesPieChart.destroy();
    }

    // Check if we have category data
    const categories = this.dashboardData?.categories || [];
    const sales = this.dashboardData?.salesPerCategory || [];
    
    if (categories.length === 0 || sales.length === 0) {
      console.warn('No category sales data available');
    }

    // Generate vibrant colors based on number of categories
    const colorPalette = [
      'rgba(255, 99, 132, 0.9)',
      'rgba(54, 162, 235, 0.9)',
      'rgba(255, 206, 86, 0.9)',
      'rgba(75, 192, 192, 0.9)',
      'rgba(153, 102, 255, 0.9)',
      'rgba(255, 159, 64, 0.9)',
      'rgba(76, 222, 166, 0.9)'
    ];
    
    // Expand color palette if needed
    const colors = categories.map((_: any, index: number) => colorPalette[index % colorPalette.length]);

    this.salesPieChart = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: categories,
        datasets: [{
          data: sales,
          backgroundColor: colors,
          borderColor: '#ffffff',
          borderWidth: 2,
          hoverOffset: 15,
          borderRadius: 5
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '65%',
        animation: {
          animateRotate: true,
          animateScale: true,
          duration: 1500
        },
        plugins: {
          legend: {
            display: true,
            position: 'right',
            labels: {
              padding: 15,
              usePointStyle: true,
              pointStyle: 'circle',
              font: {
                family: "'Poppins', sans-serif",
                size: 12
              }
            }
          },
          tooltip: {
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            titleFont: {
              family: "'Poppins', sans-serif",
              size: 14
            },
            bodyFont: {
              family: "'Poppins', sans-serif",
              size: 13
            },
            padding: 12,
            borderWidth: 1,
            borderColor: 'rgba(255, 255, 255, 0.2)',
            callbacks: {
              label: function(context) {
                const label = context.label || '';
                const value = context.raw as number;
                const total = context.dataset.data.reduce((acc: any, curr: any) => acc + curr, 0);
                const percentage = Math.round(value / total * 100);
                return `${label}: ${value} (${percentage}%)`;
              }
            }
          }
        }
      }
    });
  }

  // Other methods remain the same...
  updateRevenueChart(): void {
    if (this.revenueChart) {
      this.revenueChart.data.datasets[0].tension = this.revenuePeriod === 'monthly' ? 0.4 : 0.2;
      this.revenueChart.update();
    }
  }

  refreshCategoryChart(): void {
    if (this.salesPieChart) {
      this.salesPieChart.options.animation = {
        animateRotate: true,
        animateScale: true,
        duration: 1000
      };
      this.salesPieChart.update();
    }
  }

  animateValue(elementId: string, start: number, end: number, duration: number): void {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    if (element.textContent === end.toLocaleString()) return;
    
    let startTimestamp: number | null = null;
    const step = (timestamp: number) => {
      if (!startTimestamp) startTimestamp = timestamp;
      const progress = Math.min((timestamp - startTimestamp) / duration, 1);
      const currentValue = Math.floor(progress * (end - start) + start);
      element.textContent = currentValue.toLocaleString();
      
      if (progress < 1) {
        window.requestAnimationFrame(step);
      } else {
        element.textContent = end.toLocaleString();
      }
    };
    
    window.requestAnimationFrame(step);
  }

  setupScrollAnimations(): void {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('fade-in');
          observer.unobserve(entry.target);
        }
      });
    }, { threshold: 0.2 });
    
    setTimeout(() => {
      const animatedElements = document.querySelectorAll('.chart-card, .order-row');
      animatedElements.forEach(el => observer.observe(el));
    }, 500);
  }
}