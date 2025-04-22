import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CustomerService } from '../services/customer.service';
import { CartService } from '../services/cart.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-details',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatSnackBarModule, RouterLink, FormsModule,  ],
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.scss']
})
export class ProductDetailsComponent implements OnInit {

  product: any;
  userId!: number;
  quantity: number = 1;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private customerService: CustomerService,
    private cartService: CartService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const productId = this.route.snapshot.paramMap.get('id');
    this.userId = Number(localStorage.getItem('userId'));
    if (productId) {
      this.loadProductDetails(Number(productId));
    }
  }

  loadProductDetails(productId: number) {
    const token = localStorage.getItem('token')!;
    this.customerService.getProductById(productId, token).subscribe({
      next: (res: any) => {
        this.product = res;
      },
      error: () => {
        this.snackBar.open('Error fetching product details', '', { duration: 2000 });
      }
    });
  }

  addToCart() {
    const token = localStorage.getItem('token')!;
    const cartDto = {
      productId: this.product.id,
      userId: this.userId,
      quantity: this.quantity
    };
    this.cartService.addToCart(cartDto, token).subscribe({
      next: () => {
        this.snackBar.open('Product added to cart!', '', { duration: 2000 });
      },
      error: () => {
        this.snackBar.open('Error adding to cart', '', { duration: 2000 });
      }
    });
  }
  navigateToCart(): void {
    console.log('Navigating to cart');
    this.router.navigate(['/cart']).then(() => {
      console.log('Navigation successful, reloading page');
      window.location.reload();
    }).catch(err => {
      console.error('Navigation error:', err);
    });
  }

  buyNow() {
    const token = localStorage.getItem('token')!;
    const cartDto = {
      productId: this.product.id,
      userId: this.userId,
      quantity: this.quantity
    };
    
    // First, add to cart
    this.cartService.addToCart(cartDto, token).subscribe({
      next: (response) => {
        console.log('Product added to cart via Buy Now', response);
        this.snackBar.open('Product added, redirecting to cart...', '', { duration: 1500 });
        setTimeout(() => this.navigateToCart(), 1000);
      },
      error: (err) => {
        // If it's a 200 status, treat it as success despite being in error block
        if (err && err.status === 200) {
          console.log('Server returned 200 but was treated as error, proceeding anyway');
          this.snackBar.open('Product added, redirecting to cart...', '', { duration: 1500 });
          setTimeout(() => this.navigateToCart(), 1000);
        } else {
          console.error('Error in Buy Now:', err);
          this.snackBar.open('Error processing your request', '', { duration: 2000 });
        }
      }
    });
  }
  
  increaseQuantity() {
    this.quantity++;
  }

  decreaseQuantity() {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }
  onManualQuantityChange() {
    if (this.quantity < 1) {
      this.quantity = 1;
    } else if (this.quantity > this.product.stock) {
      this.quantity = this.product.stock;
    }
  }
  
  
  
}
