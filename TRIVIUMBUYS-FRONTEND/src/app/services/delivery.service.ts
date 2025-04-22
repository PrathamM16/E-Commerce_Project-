import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {

  private BASE_URL = 'http://localhost:8080/api/delivery/dashboard';
  private AUTH_URL = 'http://localhost:8080/api/delivery';
  private ORDERS_URL = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  getAssignedOrders(token: string): Observable<any[]> {
    const headers = this.getAuthHeaders(token);
    return this.http.get<any[]>(`${this.BASE_URL}/assigned-orders`, { headers });
  }

  registerDeliveryPerson(data: any): Observable<any> {
    return this.http.post(`${this.AUTH_URL}/register`, data);
  }
  
  loginDeliveryPerson(credentials: any): Observable<any> {
    return this.http.post(`${this.AUTH_URL}/login`, credentials);
  }

  updateOrderStatus(orderId: number, status: string, token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.put(`${this.BASE_URL}/update-status/${orderId}?status=${status}`, null, { headers });
  }

  // Add method to get order details
  getOrderDetails(orderId: number, token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.get<any>(`${this.ORDERS_URL}/details/${orderId}`, { headers });
  }

  private getAuthHeaders(token: string): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}