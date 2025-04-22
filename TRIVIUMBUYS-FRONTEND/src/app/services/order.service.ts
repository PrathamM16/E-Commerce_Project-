import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const BASE_URL = 'http://localhost:8080/api/customer/order';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  constructor(private http: HttpClient) {}

  placeOrder(orderRequest: any): Observable<any> {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    
    // Create headers with proper authorization and userId
    const httpHeaders = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('Authorization', `Bearer ${token}`)
      .set('userId', userId || '');
    
    return this.http.post(`${BASE_URL}/place`, orderRequest, { 
      headers: httpHeaders 
    });
  }
  
  getUserOrders(userId: number): Observable<any> {
    const token = localStorage.getItem('token');
    
    const headers = new HttpHeaders()
      .set('Authorization', `Bearer ${token}`)
      .set('userId', userId.toString());

    return this.http.get(`${BASE_URL}/my-orders/${userId}`, { headers });
  }
  
  getOrderDetails(orderId: number): Observable<any> {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    
    const headers = new HttpHeaders()
      .set('Authorization', `Bearer ${token}`)
      .set('userId', userId || '');
      
    return this.http.get(`${BASE_URL}/${orderId}`, { headers });
  }
}