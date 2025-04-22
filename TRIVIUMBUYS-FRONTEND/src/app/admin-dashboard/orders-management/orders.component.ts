import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-orders',
  standalone: true,
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class OrdersComponent implements OnInit {

  orders: any[] = [];
  allOrders: any[] = [];
  orderStatuses: string[] = ['PLACED', 'DISPATCHED', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'];
  selectedDate: string = '';
  selectedStatus: string = '';
  loading: boolean = false;

  private BASE_URL = 'http://localhost:8080';

  constructor(
    private http: HttpClient,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.fetchAllOrders();
  }

  fetchAllOrders(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Token missing. Please login again.');
      this.router.navigate(['/login']);
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.loading = true;

    this.http.get<any[]>(`${this.BASE_URL}/api/admin/orders`, { headers })
      .subscribe({
        next: (res: any[]) => {
          this.allOrders = res;
          this.orders = res;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching orders', err);
          this.loading = false;
          if (err.status === 403 || err.status === 401) {
            alert('Unauthorized access. Please login again.');
            this.router.navigate(['/login']);
          } else {
            alert('Failed to fetch orders.');
          }
        }
      });
  }

  updateOrderStatus(orderId: number, newStatus: string): void {
    const token = localStorage.getItem('token')!;
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.loading = true;

    this.http.put(`${this.BASE_URL}/api/admin/orders/${orderId}/status?status=${newStatus}`, {}, { headers })
      .subscribe({
        next: () => {
          this.snackBar.open('Order status updated successfully!', 'Close', { duration: 3000 });
          this.fetchAllOrders();
        },
        error: (err) => {
          console.error('Error updating order status', err);
          this.snackBar.open('Failed to update order status', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
  }

  filterOrders() {
    this.orders = this.allOrders.filter(order => {
      const matchesDate = this.selectedDate ? 
        new Date(order.date).toDateString() === new Date(this.selectedDate).toDateString() : true;

      const matchesStatus = this.selectedStatus ? 
        order.status === this.selectedStatus : true;

      return matchesDate && matchesStatus;
    });
  }

  clearFilters() {
    this.selectedDate = '';
    this.selectedStatus = '';
    this.orders = [...this.allOrders];
  }
}
vjkrbfrskbfkj