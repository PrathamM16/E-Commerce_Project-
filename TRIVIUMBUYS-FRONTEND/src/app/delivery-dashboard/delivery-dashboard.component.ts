import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DeliveryService } from '../services/delivery.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-delivery-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './delivery-dashboard.component.html',
  styleUrls: ['./delivery-dashboard.component.scss']
})
export class DeliveryDashboardComponent implements OnInit, OnDestroy {

  assignedOrders: any[] = [];
  selectedStatus: { [orderId: number]: string } = {};  // ✅ Mapping order ID to status
  statusOptions: string[] = ['DISPATCHED', 'OUT_FOR_DELIVERY', 'DELIVERED'];
  loading: boolean = true;
  error: string | null = null;
  refreshInterval: any;

  constructor(private deliveryService: DeliveryService) {}

  ngOnInit(): void {
    this.fetchAssignedOrders();
    
    // Poll for new orders every 30 seconds
    this.refreshInterval = setInterval(() => {
      this.fetchAssignedOrders(false);
    }, 30000);
  }

  ngOnDestroy(): void {
    // Clean up interval on component destruction
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  fetchAssignedOrders(showLoading: boolean = true): void {
    const token = localStorage.getItem('token');
    
    if (!token) {
      this.error = 'Authentication token not found. Please log in again.';
      this.loading = false;
      return;
    }
    
    if (showLoading) {
      this.loading = true;
    }
    
    this.error = null;
    
    this.deliveryService.getAssignedOrders(token).subscribe({
      next: (orders: any[]) => {
        console.log('Received orders:', orders);
        this.assignedOrders = orders;
        this.loading = false;
        
        // Initialize status dropdowns with current status
        orders.forEach(order => {
          if (!this.selectedStatus[order.id]) {
            this.selectedStatus[order.id] = order.status;
          }
        });
      },
      error: (err: any) => {
        console.error('Error fetching assigned orders', err);
        this.error = 'Failed to load assigned orders. Please try again.';
        this.loading = false;
      }
    });
  }

  onStatusChange(orderId: number, newStatus: string): void {
    this.selectedStatus[orderId] = newStatus;
  }

  updateOrderStatus(orderId: number): void {
    const token = localStorage.getItem('token');
    
    if (!token) {
      alert('Authentication token not found. Please log in again.');
      return;
    }
    
    const status = this.selectedStatus[orderId];

    if (!status) {
      alert('Please select a status before updating!');
      return;
    }

    this.deliveryService.updateOrderStatus(orderId, status, token).subscribe({
      next: (response) => {
        console.log('Status update response:', response);
        alert('Order status updated successfully!');
        this.fetchAssignedOrders();  // Refresh the order list
      },
      error: (err: any) => {
        console.error('Error updating order status', err);
        alert('Failed to update order status: ' + (err.error?.message || err.message || 'Unknown error'));
      }
    });
  }
}