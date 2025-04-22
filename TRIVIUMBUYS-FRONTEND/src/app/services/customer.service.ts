import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const BASE_URL = 'http://localhost:8080/api/customer'; // Backend base URL for Customer APIs

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  constructor(private http: HttpClient) {}

  // Get all products (Customer Dashboard)
  getAllProducts(token: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`${BASE_URL}/products`, { headers });
  }

  // Get all categories (For category dropdown filter)
  getAllCategories(token: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`${BASE_URL}/categories`, { headers });
  }

  // Get a single product by ID (Product Details page)
  getProductById(productId: number, token: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`${BASE_URL}/products/${productId}`, { headers });
  }

  getUserProfile(userId: number, token: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`http://localhost:8080/api/customer/profile/${userId}`, { headers });
  }
  
}