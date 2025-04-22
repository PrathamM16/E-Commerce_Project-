import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../../../services/admin.service';
import { MaterialModule } from '../../../material.module';

@Component({
  selector: 'app-manage-categories-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatSnackBarModule,
    MatButtonModule,
    MatInputModule,
    MatIconModule,
    MaterialModule
  ],
  templateUrl: './manage-categories-dialog.component.html',
  styleUrls: ['./manage-categories-dialog.component.scss']
})
export class ManageCategoriesDialogComponent {
  categories: any[] = [];
  categoryForm: FormGroup;
  isEditMode = false;
  editingCategoryId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<ManageCategoriesDialogComponent>,
    private adminService: AdminService,
    private snackBar: MatSnackBar
  ) {
    this.categoryForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required]
    });
    this.loadCategories();
  }

  loadCategories() {
    const token = localStorage.getItem('token')!;
    this.adminService.getAllCategories(token).subscribe({
      next: (res) => this.categories = res,
      error: () => {
        this.snackBar.open('Error loading categories', '', { duration: 2000 });
      }
    });
  }

  addCategory() {
    if (this.categoryForm.invalid) {
      return;
    }

    const token = localStorage.getItem('token')!;
    const payload = {
      name: this.categoryForm.get('name')?.value,
      description: this.categoryForm.get('description')?.value
    };

    if (this.isEditMode && this.editingCategoryId != null) {
      // Update category
      this.adminService.updateCategory(this.editingCategoryId, payload, token).subscribe({
        next: () => {
          this.snackBar.open('Category updated successfully', '', { duration: 2000 });
          this.categoryForm.reset();
          this.isEditMode = false;
          this.editingCategoryId = null;
          this.loadCategories();
        },
        error: () => {
          this.snackBar.open('Error updating category', '', { duration: 2000 });
        }
      });
    } else {
      // Add category
      this.adminService.addCategory(payload, token).subscribe({
        next: () => {
          this.snackBar.open('Category added successfully', '', { duration: 2000 });
          this.categoryForm.reset();
          this.loadCategories();
        },
        error: () => {
          this.snackBar.open('Error adding category', '', { duration: 2000 });
        }
      });
    }
  }

  editCategory(category: any) {
    this.categoryForm.patchValue({
      name: category.name,
      description: category.description
    });
    this.isEditMode = true;
    this.editingCategoryId = category.id;
  }

  deleteCategory(categoryId: number) {
    const confirmDelete = confirm('Are you sure you want to delete this category?');
    if (!confirmDelete) return;
  
    const token = localStorage.getItem('token')!;
    this.adminService.deleteCategory(categoryId, token).subscribe({
      next: (res) => {
        this.snackBar.open('Category deleted successfully', '', { duration: 2000 });
        this.loadCategories();
      },
      error: (err) => {
        if (err.status === 400 && err.error) {
          // Backend returned BAD_REQUEST because products are associated
          this.snackBar.open(err.error, '', { duration: 3000 });
        } else {
          this.snackBar.open('Error deleting category', '', { duration: 2000 });
        }
      }
    });
  }
  

  cancelEdit() {
    this.isEditMode = false;
    this.editingCategoryId = null;
    this.categoryForm.reset();
  }

  close() {
    this.dialogRef.close('refresh');
  }
  scrollToBottom(): void {
    window.scrollTo(0, document.body.scrollHeight);
  }
}
