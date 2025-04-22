// cart.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CartService } from '../services/cart.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cart-page',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class CartPageComponent implements OnInit {

  cartItems: any[] = [];
  totalPrice: number = 0;
  userId!: number;
  loading: boolean = true;

  constructor(
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userId = Number(localStorage.getItem('userId'));
    this.loadCartItems();
  }

  loadCartItems() {
    this.loading = true;
    const token = localStorage.getItem('token')!;
    this.cartService.viewCart(this.userId, token).subscribe({
      next: (res: any[]) => {
        this.cartItems = res;
        this.calculateTotal();
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error fetching cart items', err);
        this.loading = false;
      }
    });
  }
  

  // ✅ Increase Quantity (with stock limit)
  increaseQuantity(item: any) {
    if (item.quantity < item.product.stock) {
      const token = localStorage.getItem('token')!;
      this.cartService.updateCartItemQuantity(item.id, item.quantity + 1, token).subscribe({
        next: () => this.loadCartItems(),
        error: (err: any) => console.error('Error increasing quantity', err)
      });
    }
  }

  // ✅ Decrease Quantity
  decreaseQuantity(item: any) {
    if (item.quantity > 1) {
      const token = localStorage.getItem('token')!;
      this.cartService.updateCartItemQuantity(item.id, item.quantity - 1, token).subscribe({
        next: () => this.loadCartItems(),
        error: (err: any) => console.error('Error decreasing quantity', err)
      });
    }
  }

  // ✅ Manual Input Change for Quantity
  onQuantityInputChange(item: any, inputQuantity: any) {
    let quantity = Number(inputQuantity);
    if (isNaN(quantity) || quantity < 1) {
      quantity = 1;
    } else if (quantity > item.product.stock) {
      quantity = item.product.stock;
    }
    const token = localStorage.getItem('token')!;
    this.cartService.updateCartItemQuantity(item.id, quantity, token).subscribe({
      next: () => this.loadCartItems(),
      error: (err: any) => console.error('Error updating quantity manually', err)
    });
  }

  removeItem(cartItemId: number) {
    const token = localStorage.getItem('token')!;
    this.cartService.removeCartItem(cartItemId, token).subscribe({
      next: () => this.loadCartItems(),
      error: (err: any) => console.error('Error removing item', err)
    });
  }

  calculateTotal() {
    this.totalPrice = this.cartItems.reduce((acc, item) => {
      const price = item.product ? (item.product.price || 0) : 0;
      return acc + (price * item.quantity);
    }, 0);
  }

  proceedToCheckout() {
    this.router.navigate(['/payment']);
  }

  continueShopping() {
    this.router.navigate(['/customer-dashboard']);
  }

  clearCart() {
    if (confirm('Are you sure you want to clear your entire cart?')) {
      const token = localStorage.getItem('token')!;
      const removePromises = this.cartItems.map(item => 
        new Promise<void>((resolve, reject) => {
          this.cartService.removeCartItem(item.id, token).subscribe({
            next: () => resolve(),
            error: (err) => {
              console.error('Error removing item', err);
              reject(err);
            }
          });
        })
      );
      Promise.all(removePromises)
        .then(() => {
          this.cartItems = [];
          this.totalPrice = 0;
          alert('Cart has been cleared successfully');
        })
        .catch(() => {
          alert('There was an error clearing your cart. Please try again.');
          this.loadCartItems();
        });
    }
  }
  handleQuantityChange(event: Event, item: any) {
    const inputElement = event.target as HTMLInputElement;
    let quantity = Number(inputElement.value);
  
    if (isNaN(quantity) || quantity < 1) {
      quantity = 1;
    } else if (quantity > item.product.stock) {
      quantity = item.product.stock;
    }
  
    const token = localStorage.getItem('token')!;
    this.cartService.updateCartItemQuantity(item.id, quantity, token).subscribe({
      next: () => this.loadCartItems(),
      error: (err: any) => console.error('Error updating quantity manually', err)
    });
  }
  
  
  
}
