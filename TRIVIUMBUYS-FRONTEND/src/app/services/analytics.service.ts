import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private apiUrl = 'http://localhost:8080/api/analytics'; // Direct hardcoded URL for testing

  constructor(private http: HttpClient) { }

  // Helper method to get authorization headers
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  uploadOrders(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/upload-orders`, formData, {
      headers: this.getHeaders()
    });
  }

  uploadProducts(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/upload-products`, formData, {
      headers: this.getHeaders()
    });
  }

  uploadCustomers(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/upload-customers`, formData, {
      headers: this.getHeaders()
    });
  }

  getAnalyticsReport(): Observable<any> {
    return this.http.get(`${this.apiUrl}/get-report`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error("Error fetching analytics report:", error);
        // Return empty data structure instead of propagating error
        return of({
          totalRevenue: 0,
          totalOrders: 0,
          totalCustomers: 0,
          revenueByDate: {},
          orderStatusCount: {},
          categorySales: {},
          lowStockProducts: []
        });
      })
    );
  }

  getRevenuePrediction(days: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/predict-revenue?days=${days}`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error("Error fetching prediction data:", error);
        // Return empty data structure instead of propagating error
        return of({});
      })
    );
  }

  downloadReport(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/export-report`, {
      headers: this.getHeaders(),
      responseType: 'blob'
    });
  }
}