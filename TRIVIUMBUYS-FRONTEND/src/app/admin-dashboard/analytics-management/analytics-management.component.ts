import { Component, OnInit } from '@angular/core';
import { AnalyticsService } from '../../services/analytics.service';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, Color, ScaleType } from '@swimlane/ngx-charts';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-analytics-management',
  standalone: true,
  templateUrl: './analytics-management.component.html',
  styleUrls: ['./analytics-management.component.scss'],
  imports: [CommonModule, NgxChartsModule],
})
export class AnalyticsComponent implements OnInit {

  selectedOrdersFile!: File;
  selectedProductsFile!: File;
  selectedCustomersFile!: File;

  analyticsData: any = {
    totalRevenue: 0,
    totalOrders: 0,
    totalCustomers: 0,
    revenueByDate: {},
    orderStatusCount: {},
    categorySales: {},
    lowStockProducts: []
  };

  predictionData: { [key: string]: number } = {};

  revenueChartData: any[] = [];
  orderStatusData: any[] = [];
  categorySalesData: any[] = [];

  isLoading: boolean = false;
  error: string | null = null;
  dataUploaded: boolean = false;

  // Helper properties
  get hasPredictionData(): boolean {
    return this.predictionData && Object.keys(this.predictionData).length > 0;
  }

  get hasRevenueChartData(): boolean {
    return this.revenueChartData && this.revenueChartData.length > 0;
  }

  get hasOrderStatusData(): boolean {
    return this.orderStatusData && this.orderStatusData.length > 0;
  }

  get hasCategorySalesData(): boolean {
    return this.categorySalesData && this.categorySalesData.length > 0;
  }

  colorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#5AA454', '#C7B42C', '#A10A28', '#AAAAAA']
  };

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.loadInitialData();
  }

  loadInitialData() {
    this.loadAnalytics();
    this.loadPrediction();
  }

  onOrdersFileSelected(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedOrdersFile = event.target.files[0];
    }
  }

  onProductsFileSelected(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedProductsFile = event.target.files[0];
    }
  }

  onCustomersFileSelected(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedCustomersFile = event.target.files[0];
    }
  }

  uploadOrders() {
    if (this.selectedOrdersFile) {
      this.isLoading = true;
      this.error = null;

      this.analyticsService.uploadOrders(this.selectedOrdersFile).subscribe({
        next: (response) => {
          this.dataUploaded = true;
          this.isLoading = false;
          alert(response?.message || 'Orders uploaded successfully.');
          this.refreshAnalytics();
        },
        error: (error: HttpErrorResponse) => {
          this.handleUploadError(error, 'orders');
        }
      });
    } else {
      alert('Please select a file first.');
    }
  }

  uploadProducts() {
    if (this.selectedProductsFile) {
      this.isLoading = true;
      this.error = null;

      this.analyticsService.uploadProducts(this.selectedProductsFile).subscribe({
        next: (response) => {
          this.dataUploaded = true;
          this.isLoading = false;
          alert(response?.message || 'Products uploaded successfully.');
          this.refreshAnalytics();
        },
        error: (error: HttpErrorResponse) => {
          this.handleUploadError(error, 'products');
        }
      });
    } else {
      alert('Please select a file first.');
    }
  }

  uploadCustomers() {
    if (this.selectedCustomersFile) {
      this.isLoading = true;
      this.error = null;

      this.analyticsService.uploadCustomers(this.selectedCustomersFile).subscribe({
        next: (response) => {
          this.dataUploaded = true;
          this.isLoading = false;
          alert(response?.message || 'Customers uploaded successfully.');
          this.refreshAnalytics();
        },
        error: (error: HttpErrorResponse) => {
          this.handleUploadError(error, 'customers');
        }
      });
    } else {
      alert('Please select a file first.');
    }
  }

  loadAnalytics() {
    this.isLoading = true;
    this.error = null;

    this.analyticsService.getAnalyticsReport().subscribe({
      next: (data) => {
        this.analyticsData = data || this.analyticsData;
        this.isLoading = false;
        this.prepareChartData(data);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading analytics:', error);
        this.isLoading = false;
        this.error = (error.status === 403)
          ? 'Access denied. Please check your permissions.'
          : 'Failed to load analytics data. Please try again later.';
      }
    });
  }

  prepareChartData(data: any) {
    if (!data) return;

    // Revenue Chart
    this.revenueChartData = (data.revenueByDate && Object.keys(data.revenueByDate).length > 0) ?
      [{
        name: 'Revenue',
        series: Object.entries(data.revenueByDate).map(([date, revenue]) => ({
          name: date,
          value: revenue as number
        }))
      }] : [];

    // Order Status Pie Chart
    this.orderStatusData = (data.orderStatusCount && Object.keys(data.orderStatusCount).length > 0) ?
      Object.entries(data.orderStatusCount).map(([status, count]) => ({
        name: status,
        value: count as number
      })) : [];

    // Category Sales Bar Chart
    this.categorySalesData = (data.categorySales && Object.keys(data.categorySales).length > 0) ?
      Object.entries(data.categorySales).map(([category, sales]) => ({
        name: category,
        value: sales as number
      })) : [];
  }

  loadPrediction() {
    this.analyticsService.getRevenuePrediction(7).subscribe({
      next: (data) => {
        this.predictionData = data || {};
      },
      error: (error) => {
        console.error('Error loading prediction data:', error);
      }
    });
  }

  refreshAnalytics() {
    this.loadAnalytics();
    this.loadPrediction();
  }

  downloadPDFReport() {
    this.isLoading = true;

    this.analyticsService.downloadReport().subscribe({
      next: (blob) => {
        this.isLoading = false;
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'Analytics_Report.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Error downloading report:', error);
        this.isLoading = false;
        alert('Failed to download report. Please try again later.');
      }
    });
  }

  handleUploadError(error: HttpErrorResponse, type: string) {
    console.error(`Error uploading ${type}:`, error);
    this.isLoading = false;

    if (error.status === 403) {
      this.error = 'Access denied. Please check if you are logged in.';
    } else if (error.error && typeof error.error === 'string') {
      this.error = error.error;
    } else if (error.error && error.error.message) {
      this.error = error.error.message;
    } else {
      this.error = `Failed to upload ${type}. Please check file format and try again.`;
    }

    alert(this.error);
  }
}
