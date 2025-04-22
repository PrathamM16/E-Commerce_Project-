import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, of, throwError } from 'rxjs';

const BASE_URL = 'http://localhost:8080/api/customer'; // Backend base URL for Customer APIs

@Injectable({
  providedIn: 'root'
})
export class CartService {

  constructor(private http: HttpClient) {}

  // Add product to cart
  addToCart(cartDto: { productId: number, userId: number, quantity: number }, token: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(`${BASE_URL}/cart/add`, cartDto, { headers })
      .pipe(
        catchError(error => {
          if (error.status === 200) {
            return of({ success: true });
          }
          return throwError(() => error);
        })
      );
  }

  // ✅ View cart items (accept 2 parameters but ignore userId)
  viewCart(_userId: number, token: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`${BASE_URL}/cart/view`, { headers });
  }

  // Remove cart item
  removeCartItem(cartItemId: number, token: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete(`${BASE_URL}/cart/remove/${cartItemId}`, { headers });
  }

  // Update cart item quantity
  updateCartItemQuantity(cartItemId: number, quantity: number, token: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const body = { cartItemId, quantity };
    return this.http.put(`${BASE_URL}/cart/update`, body, { headers });
  }
}
