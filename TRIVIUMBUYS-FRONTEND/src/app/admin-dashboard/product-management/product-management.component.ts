// product-management.component.ts
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AdminService } from '../../services/admin.service';
import { AddProductDialogComponent } from './add-product-dialog/add-product-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from '../../material.module';

@Component({
  selector: 'app-product-management',
  standalone: true,
  templateUrl: './product-management.component.html',
  styleUrls: ['./product-management.component.scss'],
  imports: [
    CommonModule,
    FormsModule,
    MaterialModule
  ]
})
export class ProductManagementComponent implements OnInit {
  products: any[] = [];
  filteredProducts: any[] = [];
  lowStockProducts: any[] = [];
  outOfStockProducts: any[] = [];
  categories: any[] = [];

  searchText: string = '';
  selectedCategory: string = '';

  constructor(
    private adminService: AdminService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadCategories();
  }

  loadProducts() {
    const token = localStorage.getItem('token')!;
    this.adminService.getAllProducts(token).subscribe({
      next: (res) => {
        this.products = res;
        this.filterProducts();
        this.updateStockStatus();
      },
      error: () => {
        this.snackBar.open('Error loading products', '', { duration: 2000 });
      }
    });
  }

  loadCategories() {
    const token = localStorage.getItem('token')!;
    this.adminService.getAllCategories(token).subscribe({
      next: (res) => (this.categories = res),
      error: () => {
        this.snackBar.open('Error loading categories', '', { duration: 2000 });
      }
    });
  }

  filterProducts() {
    this.filteredProducts = this.products.filter(p => {
      const matchText = p.name.toLowerCase().includes(this.searchText.toLowerCase());
      const matchCategory = this.selectedCategory ? p.category?.name === this.selectedCategory : true;
      return matchText && matchCategory;
    });
    
    // Update stock status sections after filtering
    this.updateStockStatus();
  }

  updateStockStatus() {
    // Update low stock products (stock > 0 and < 10)
    this.lowStockProducts = this.filteredProducts.filter(p => p.stock > 0 && p.stock < 10);
    
    // Update out of stock products (stock = 0)
    this.outOfStockProducts = this.filteredProducts.filter(p => p.stock === 0);
  }

  openAddDialog(product: any = null) {
    const dialogRef = this.dialog.open(AddProductDialogComponent, {
      width: '600px',
      data: {
        product,
        categories: this.categories
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'refresh') {
        this.loadProducts();
        this.loadCategories();
      }
    });
  }

  deleteProduct(id: number) {
    const token = localStorage.getItem('token')!;
    if (confirm('Are you sure you want to delete this product?')) {
      this.adminService.deleteProduct(id, token).subscribe({
        next: (res) => {
          if (res && res.message) {
            this.snackBar.open(res.message, '', { duration: 2000 });
          } else {
            this.snackBar.open('Product deleted successfully', '', { duration: 2000 });
          }
          this.loadProducts();
        },
        error: (err) => {
          if (err.error && err.error.message) {
            this.snackBar.open(err.error.message, '', { duration: 3000 });
          } else {
            this.snackBar.open('Error deleting product', '', { duration: 2000 });
          }
        }
      });
    }
  }

  getImageUrl(product: any): string {
    if (!product || !product.id) {
      return 'assets/no-image.png'; // fallback if no image
    }
    return `http://localhost:8080/uploads/${product.id}`;
  }
}