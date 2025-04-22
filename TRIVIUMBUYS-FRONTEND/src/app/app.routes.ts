import { Routes } from '@angular/router';
import { AdminLayoutComponent } from './admin-dashboard/admin-layout.component';
import { CustomerManagementComponent } from './admin-dashboard/customer-management/customer-management.component';
import { DashboardComponent } from './admin-dashboard/dashboard/dashboard.component';
import { ProductManagementComponent } from './admin-dashboard/product-management/product-management.component';
import { CustomerDashboardComponent } from './customer-dashboard/customer-dashboard.component';
import { AuthGuard } from './guards/auth.guard';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { AddAdminComponent } from './admin-dashboard/add-admins/add-admin.component';
import { provideToastr } from 'ngx-toastr';
import { AnalyticsComponent } from './admin-dashboard/analytics-management/analytics-management.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // Public Routes
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'delivery-signup', loadComponent: () => import('./delivery-signup/delivery-signup.component').then(m => m.DeliverySignupComponent) },

  // Admin Protected Routes
  {
    path: 'admin-dashboard',
    component: AdminLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', component: DashboardComponent },
      { path: 'products', component: ProductManagementComponent },
      { path: 'customers', component: CustomerManagementComponent },
      {
        path: 'orders',
        loadComponent: () => import('./admin-dashboard/orders-management/orders.component').then(m => m.OrdersComponent),
        canActivate: [AuthGuard]
      },
      {
        path: 'reports',
        loadComponent: () => import('./admin-dashboard/reports/reports.component').then(m => m.ReportsComponent),
        canActivate: [AuthGuard]
      },
      {
        path: 'add-admins',
        loadComponent: () => import('./admin-dashboard/add-admins/add-admin.component').then(m => m.AddAdminComponent),
        providers: [provideToastr()]
      },

      // ✅ NEW Analytics Management Route
      {
        path: 'analytics-management',
        component: AnalyticsComponent,
        canActivate: [AuthGuard]
      }
    ]
  },

  // 🚚 Delivery Dashboard (Independent)
  {
    path: 'delivery-dashboard',
    loadComponent: () => import('./delivery-dashboard/delivery-dashboard.component').then(m => m.DeliveryDashboardComponent),
    canActivate: [AuthGuard]
  },

  // Customer Protected Routes
  {
    path: 'customer-dashboard',
    component: CustomerDashboardComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'customer-dashboard/product/:id',
    loadComponent: () => import('./customer-dashboard/product-details.component')
      .then(m => m.ProductDetailsComponent),
    canActivate: [AuthGuard]
  },
  
  { 
    path: 'cart', 
    loadComponent: () => import('./cart/cart.component').then(m => m.CartPageComponent),
    canActivate: [AuthGuard]
  },
  { 
    path: 'payment', 
    loadComponent: () => import('./payment-page/payment-page.component').then(m => m.PaymentPageComponent) 
  },
  { 
    path: 'your-orders', 
    loadComponent: () => import('./your-orders/your-orders.component').then(m => m.YourOrdersComponent) 
  },

  // Wildcard fallback
  { path: '**', redirectTo: 'login' }
];
