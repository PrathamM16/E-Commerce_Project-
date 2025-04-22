import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select'; // ✅ ADD THIS

import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-customer-management',
  standalone: true,
  templateUrl: './customer-management.component.html',
  styleUrls: ['./customer-management.component.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatSnackBarModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule, // ✅ ADD THIS
  ]
})
export class CustomerManagementComponent implements OnInit {

  customers: any[] = [];
  displayedColumns: string[] = ['id', 'name', 'email', 'phone', 'address'];
  searchForm!: FormGroup;

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadAllCustomers();
    this.searchForm = this.fb.group({
      searchType: ['id'],
      searchValue: ['']
    });
  }

  loadAllCustomers() {
    const token = localStorage.getItem('token');
    if (token) {
      this.adminService.getAllCustomers(token).subscribe({
        next: (res: any[]) => {
          this.customers = res;
        },
        error: () => {
          this.snackBar.open('Error loading customers', '', { duration: 2000 });
        }
      });
    }
  }

  searchCustomer() {
    const { searchType, searchValue } = this.searchForm.value;
    const token = localStorage.getItem('token');

    if (!searchValue) {
      this.snackBar.open('Please enter search value', '', { duration: 2000 });
      return;
    }

    if (token) {
      if (searchType === 'id') {
        this.adminService.searchCustomerById(searchValue, token).subscribe({
          next: (res: any) => {
            this.customers = [res]; // wrap single customer into array
          },
          error: () => {
            this.snackBar.open('Customer not found', '', { duration: 2000 });
          }
        });
      } else if (searchType === 'name') {
        this.adminService.searchCustomerByName(searchValue, token).subscribe({
          next: (res: any[]) => {
            this.customers = res;
          },
          error: () => {
            this.snackBar.open('No customers found', '', { duration: 2000 });
          }
        });
      }
    }
  }

  resetSearch() {
    this.searchForm.reset({ searchType: 'id', searchValue: '' });
    this.loadAllCustomers();
  }
}
