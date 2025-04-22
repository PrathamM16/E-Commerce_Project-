import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private BASE_URL = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  // ----- Dashboard APIs -----
  getDashboardOverview(token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.get(`${this.BASE_URL}/dashboard/overview`, { headers });
  }

  // ----- Product Management APIs -----
  getAllProducts(token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.get(`${this.BASE_URL}/products/all`, { headers });
  }

  getProductById(id: number, token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.get(`${this.BASE_URL}/products/${id}`, { headers });
  }

  addProduct(formData: FormData, token: string): Observable<any> {
    const headers = this.getAuthHeaders(token, true);  // true = multipart
    return this.http.post(`${this.BASE_URL}/products/add`, formData, { headers });
  }

  updateProduct(id: number, formData: FormData, token: string): Observable<any> {
    const headers = this.getAuthHeaders(token, true);
    return this.http.put(`${this.BASE_URL}/products/update/${id}`, formData, { headers });
  }

  deleteProduct(id: number, token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.delete(`${this.BASE_URL}/products/delete/${id}`, { headers });
  }

  // ----- Category Management APIs -----
  getAllCategories(token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.get(`${this.BASE_URL}/categories/all`, { headers });
  }

  addCategory(category: any, token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.post(`${this.BASE_URL}/categories/add`, category, { headers });
  }

  updateCategory(id: number, category: any, token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.put(`${this.BASE_URL}/categories/update/${id}`, category, { headers });
  }

  deleteCategory(id: number, token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.delete(`${this.BASE_URL}/categories/delete/${id}`, { headers });
  }

  // ----- Customer Management APIs -----
  getAllCustomers(token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.get(`${this.BASE_URL}/customers/all`, { headers });
  }

  searchCustomerById(id: number, token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.get(`${this.BASE_URL}/customers/search/id/${id}`, { headers });
  }

  searchCustomerByName(name: string, token: string): Observable<any> {
    const headers = this.getAuthHeaders(token);
    return this.http.get(`${this.BASE_URL}/customers/search/name/${name}`, { headers });
  }

  // ----- Common Method for Authorization Headers -----
  private getAuthHeaders(token: string, isMultipart: boolean = false): HttpHeaders {
    let headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    if (!isMultipart) {
      headers = headers.set('Content-Type', 'application/json');
    }

    return headers;
  }
}
