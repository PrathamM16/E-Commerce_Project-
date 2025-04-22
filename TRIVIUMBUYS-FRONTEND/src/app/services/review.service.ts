import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, tap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  // Make sure this matches your Java backend URL exactly
  private baseUrl = 'http://localhost:8080/api/reviews';

  constructor(private http: HttpClient) {}

  addReview(reviewData: any): Observable<any> {
    // Remove Authorization header since the backend doesn't require it
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    console.log('Submitting review to:', `${this.baseUrl}/add`);
    console.log('Review payload:', reviewData);
    
    return this.http.post(`${this.baseUrl}/add`, reviewData, { headers }).pipe(
      tap(response => console.log('Review submission successful. Response:', response)),
      catchError(error => {
        console.error('Review submission error:', error);
        console.error('Status:', error.status);
        console.error('Error message:', error.message);
        console.error('Error details:', error.error);
        return throwError(() => error);
      })
    );
  }

  getReviewsByProductId(productId: number): Observable<any[]> {
    console.log('Fetching reviews from:', `${this.baseUrl}/product/${productId}`);
    
    // No authentication header needed
    return this.http.get<any[]>(`${this.baseUrl}/product/${productId}`).pipe(
      tap(reviews => console.log('Reviews fetched successfully. Count:', reviews.length)),
      catchError(error => {
        console.error('Error fetching reviews:', error);
        console.error('Status:', error.status);
        console.error('Error message:', error.message);
        return throwError(() => error);
      })
    );
  }
}