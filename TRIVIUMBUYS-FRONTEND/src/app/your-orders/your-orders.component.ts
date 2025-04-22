import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-your-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './your-orders.component.html',
  styleUrls: ['./your-orders.component.scss']
})
export class YourOrdersComponent implements OnInit {
  orders: any[] = [];
  userId!: number;
  loading: boolean = true;
  showDialog: boolean = false;
  selectedOrder: any = null;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userId = Number(localStorage.getItem('userId'));
    this.fetchOrders();
  }

  fetchOrders() {
    const token = localStorage.getItem('token')!;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    this.http.get<any[]>(`http://localhost:8080/api/orders/customer`, { headers }).subscribe({
      next: (res) => {
        this.orders = res;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching orders', err);
        this.loading = false;
      }
    });
  }

  viewOrderDetails(orderId: number) {
    // Find the order details from the existing orders array
    const order = this.orders.find(o => o.id === orderId);
    if (order) {
      this.selectedOrder = order;
      this.showDialog = true;
    } else {
      // Alternatively, fetch the details from the server
      this.fetchOrderDetails(orderId);
    }
  }

  fetchOrderDetails(orderId: number) {
    const token = localStorage.getItem('token')!;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    this.http.get<any>(`http://localhost:8080/api/orders/${orderId}`, { headers }).subscribe({
      next: (res) => {
        this.selectedOrder = res;
        this.showDialog = true;
      },
      error: (err) => {
        console.error('Error fetching order details', err);
      }
    });
  }

  closeDialog() {
    this.showDialog = false;
  }

  goToDashboard() {
    this.router.navigate(['/customer-dashboard']);
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  getStatusClass(status: string): string {
    status = status || 'PLACED';
    switch (status.toUpperCase()) {
      case 'DELIVERED':
        return 'status-delivered';
      case 'SHIPPED':
        return 'status-shipped';
      case 'PROCESSING':
        return 'status-processing';
      case 'CANCELLED':
        return 'status-cancelled';
      default:
        return 'status-placed';
    }
  }
  
  // To prevent dialog close when clicking inside the dialog
  preventClose(event: Event) {
    event.stopPropagation();
  }
}