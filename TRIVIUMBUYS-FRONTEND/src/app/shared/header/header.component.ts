import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { CartService } from '../../services/cart.service';
import { Subscription, interval } from 'rxjs';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule, 
    RouterLink, 
    RouterLinkActive, 
    MatButtonModule, 
    MatIconModule, 
    MatBadgeModule
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {
  cartItemCount: number = 0;
  userId: number = 0;
  private cartUpdateSubscription?: Subscription;
  private refreshInterval?: Subscription;

  constructor(
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userId = Number(localStorage.getItem('userId'));
    
    // Initial cart count fetch
    this.getCartCount();
    
    // Set up periodic refresh (every 30 seconds)
    this.refreshInterval = interval(30000).subscribe(() => {
      if (this.isLoggedIn()) {
        this.getCartCount();
      }
    });
    
    // Listen for cart updates if service supports it
    this.setupCartUpdateListener();
  }
  
  ngOnDestroy(): void {
    // Clean up subscriptions to prevent memory leaks
    if (this.cartUpdateSubscription) {
      this.cartUpdateSubscription.unsubscribe();
    }
    
    if (this.refreshInterval) {
      this.refreshInterval.unsubscribe();
    }
  }
  
  private setupCartUpdateListener(): void {
    // If your CartService has an observable for cart updates
    // Uncomment and modify the following code based on your service implementation
    /*
    if (this.cartService.cartUpdated) {
      this.cartUpdateSubscription = this.cartService.cartUpdated.subscribe(() => {
        this.getCartCount();
      });
    }
    */
    
    // If you don't have an observable, you could enhance your CartService
    // to include a Subject that emits when the cart is updated
  }

  getCartCount(): void {
    if (this.userId && this.isLoggedIn()) {
      const token = localStorage.getItem('token')!;
      this.cartService.viewCart(this.userId, token).subscribe({
        next: (res: any[]) => {
          this.cartItemCount = res.length;
          // Optionally store in localStorage for quick access on page refresh
          localStorage.setItem('cartCount', this.cartItemCount.toString());
        },
        error: (err: any) => {
          console.error('Error fetching cart count', err);
          // Try to get from localStorage as fallback
          const storedCount = localStorage.getItem('cartCount');
          if (storedCount) {
            this.cartItemCount = parseInt(storedCount, 10);
          }
        }
      });
    } else {
      this.cartItemCount = 0;
      localStorage.removeItem('cartCount');
    }
  }
  
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }
  
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('cartCount');
    this.cartItemCount = 0;
    this.router.navigate(['/login']);
  }
}