import { Routes } from '@angular/router';
import { AdminDashboardComponent } from './admin-dashboard.component';
import { ProductManagementComponent } from './product-management/product-management.component';
import { CustomerManagementComponent } from './customer-management/customer-management.component';
import { OrdersComponent } from './orders-management/orders.component';
import { ReportsComponent } from './reports/reports.component';
import { AddAdminComponent } from './add-admins/add-admin.component';
import { AnalyticsComponent } from './analytics-management/analytics-management.component';

export const adminRoutes: Routes = [
  {
    path: '',
    component: AdminDashboardComponent,
    children: [
      { path: '', redirectTo: 'products', pathMatch: 'full' },
      { path: 'products', component: ProductManagementComponent },
      { path: 'customers', component: CustomerManagementComponent },
      { path: 'orders', component: OrdersComponent },
      { path: 'reports', component: ReportsComponent },
      { path: 'add-admins', component: AddAdminComponent },

      // ✅ New Analytics Management Route
      { path: 'analytics-management', component: AnalyticsComponent }
    ]
  }
];
