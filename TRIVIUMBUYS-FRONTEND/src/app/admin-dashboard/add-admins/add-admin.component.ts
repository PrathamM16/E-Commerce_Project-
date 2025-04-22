import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-add-admin',
  standalone: true,
  templateUrl: './add-admin.component.html',
  styleUrls: ['./add-admin.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class AddAdminComponent {
  admin = {
    name: '',
    username: '',
    email: '',
    phone: '',
    password: '',
    address: ''
  };

  confirmPassword: string = '';

  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  adminAccess = {
    dashboard: false,
    products: false,
    customers: false,
    orders: false,
    reports: false,
    settings: false
  };

  blockedFields = {
    dashboard: false,
    orders: false,
    reports: false,
    settings: false
  };

  constructor(
    private http: HttpClient,
    private toastr: ToastrService
  ) {}

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  checkAccessRestrictions() {
    if (this.adminAccess.products && this.adminAccess.customers) {
      this.blockedFields.dashboard = true;
      this.blockedFields.orders = true;
      this.blockedFields.reports = true;
      this.blockedFields.settings = true;
    } else {
      this.blockedFields.dashboard = false;
      this.blockedFields.orders = false;
      this.blockedFields.reports = false;
      this.blockedFields.settings = false;
    }
  }

  selectAllAccess(event: any) {
    const isChecked = event.target.checked;

    this.adminAccess.dashboard = isChecked;
    this.adminAccess.products = isChecked;
    this.adminAccess.customers = isChecked;
    this.adminAccess.orders = isChecked;
    this.adminAccess.reports = isChecked;
    this.adminAccess.settings = isChecked;

    this.checkAccessRestrictions(); // check restrictions after select all
  }

  validateForm(): boolean {
    // ✅ Check all mandatory fields
    if (!this.admin.name || !this.admin.username || !this.admin.email || !this.admin.phone || !this.admin.password || !this.confirmPassword || !this.admin.address) {
      this.toastr.error('All fields are required.', 'Validation Error');
      return false;
    }

    // ✅ Check Phone Number (exactly 10 digits)
    const phonePattern = /^[0-9]{10}$/;
    if (!phonePattern.test(this.admin.phone)) {
      this.toastr.error('Phone number must be exactly 10 digits.', 'Validation Error');
      return false;
    }

    // ✅ Check Password Match
    if (this.admin.password !== this.confirmPassword) {
      this.toastr.error('Passwords do not match!', 'Validation Error');
      return false;
    }

    return true;
  }

  createAdmin() {
    if (!this.validateForm()) {
      return;
    }
  
    const accessRights = [];
  
    if (this.adminAccess.products) accessRights.push('PRODUCTS');
    if (this.adminAccess.customers) accessRights.push('CUSTOMERS');
    if (this.adminAccess.orders) accessRights.push('ORDERS');
    if (this.adminAccess.reports) accessRights.push('REPORTS');
    if (this.adminAccess.settings) accessRights.push('SETTINGS');
  
    const requestBody = {
      ...this.admin,
      accessRights: accessRights
    };
  
    const token = localStorage.getItem('token');
    const headers = {
      'Authorization': `Bearer ${token}`
    };
  
    this.http.post('http://localhost:8080/api/admin-management/add-admin', requestBody, { headers, responseType: 'text' })
      .subscribe({
        next: (response) => {
          console.log(response);
          this.toastr.success(response, 'Success');
        },
        error: (error) => {
          console.error(error);
          if (error.status === 400 && error.error) {
            const errorMessage = error.error;
  
            if (errorMessage.includes('Username')) {
              this.toastr.error('Username already exists!', 'Error');
            } else if (errorMessage.includes('Email')) {
              this.toastr.error('Email already exists!', 'Error');
            } else if (errorMessage.includes('Phone')) {
              this.toastr.error('Phone number already exists!', 'Error');
            } else {
              this.toastr.error(errorMessage, 'Error');
            }
  
          } else {
            this.toastr.error('Something went wrong!', 'Error');
          }
        }
      });
  }
  
}
