import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

import { AdminService } from '../../../services/admin.service';
import { MaterialModule } from '../../../material.module';
import { ManageCategoriesDialogComponent } from '../manage-categories-dialog/manage-categories-dialog.component';

@Component({
  selector: 'app-add-product-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MaterialModule
  ],
  templateUrl: './add-product-dialog.component.html',
  styleUrls: ['./add-product-dialog.component.scss']
})
export class AddProductDialogComponent {
  form: FormGroup;
  imageFile!: File;
  categories: any[] = [];
  imagePreview: string | null = null;
  isEditMode: boolean = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddProductDialogComponent>,
    private dialog: MatDialog,
    private adminService: AdminService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.categories = data.categories || [];
    this.isEditMode = !!data.product;

    this.form = this.fb.group({
      id: [data.product?.id || null],
      name: [data.product?.name || '', Validators.required],
      description: [data.product?.description || '', Validators.required],
      brand: [data.product?.brand || '', Validators.required], // ✅ Added brand control here!
      price: [data.product?.price || '', [Validators.required, Validators.min(1)]],
      discount: [data.product?.discount || 0],
      stock: [data.product?.stock || '', [Validators.required, Validators.min(0)]],
      taxRate: [data.product?.taxRate || 18],
      categoryId: [data.product?.category?.id || '', Validators.required]
    });

    if (data.product?.imageUrl) {
      this.imagePreview = data.product.imageUrl;
    }
  }

  onFileChange(event: any) {
    const file = event.target.files?.[0];
    if (file) {
      this.imageFile = file;
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreview = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  submit() {
    if (!this.form.valid) {
      this.snackBar.open('Please fill all required fields', '', { duration: 2000 });
      return;
    }

    const token = localStorage.getItem('token')!;
    const formData = new FormData();

    formData.append('name', this.form.get('name')?.value);
    formData.append('description', this.form.get('description')?.value);
    formData.append('brand', this.form.get('brand')?.value); // ✅ Added brand here!
    formData.append('price', this.form.get('price')?.value);
    formData.append('discount', this.form.get('discount')?.value);
    formData.append('stock', this.form.get('stock')?.value);
    formData.append('taxRate', this.form.get('taxRate')?.value);
    formData.append('categoryId', this.form.get('categoryId')?.value);

    if (this.imageFile) {
      formData.append('img', this.imageFile);
    }

    if (this.isEditMode) {
      const id = this.form.get('id')?.value;
      this.adminService.updateProduct(id, formData, token).subscribe({
        next: () => {
          this.snackBar.open('Product updated successfully!', '', { duration: 2000 });
          this.dialogRef.close('refresh');
        },
        error: (err) => {
          console.error(err);
          this.snackBar.open('Error updating product', '', { duration: 2000 });
        }
      });
    } else {
      this.adminService.addProduct(formData, token).subscribe({
        next: () => {
          this.snackBar.open('Product added successfully!', '', { duration: 2000 });
          this.dialogRef.close('refresh');
        },
        error: (err) => {
          console.error(err);
          this.snackBar.open('Error adding product', '', { duration: 2000 });
        }
      });
    }
  }

  cancel() {
    this.dialogRef.close();
  }

  openManageCategories() {
    const dialogRef = this.dialog.open(ManageCategoriesDialogComponent, {
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'refresh') {
        this.reloadCategories();
      }
    });
  }

  reloadCategories() {
    const token = localStorage.getItem('token')!;
    this.adminService.getAllCategories(token).subscribe({
      next: (res) => (this.categories = res),
      error: () => {
        this.snackBar.open('Error loading categories', '', { duration: 2000 });
      }
    });
  }
}
