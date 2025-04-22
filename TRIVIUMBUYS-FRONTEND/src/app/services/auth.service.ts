import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { UserProfile } from '../models/user-profile';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  getUserId(): number {
    throw new Error('Method not implemented.');
  }
  googleSignup() {
    throw new Error('Method not implemented.');
  }
  facebookSignup() {
    throw new Error('Method not implemented.');
  }
  private baseUrl = 'http://localhost:8080/api/auth';
  // Flag to control mock behavior
  private useMockData = true; // Set to false when backend is ready

  constructor(private http: HttpClient) { }

  /**
   * Register a new user
   * @param signupData Registration data
   */
  signup(signupData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/signup`, signupData);
  }

  /**
   * Authenticate user and get token
   * @param loginData Login credentials
   */
  login(loginData: any): Observable<any> {
    // If using mock data and this is an admin login, provide mock response
    if (this.useMockData && loginData.username === 'admin') {
      const mockResponse = {
        token: 'mock-jwt-token-for-admin-user',
        user: {
          id: 1,
          name: 'Pratham',
          email: 'admin@triviumbuys.com',
          role: 'ADMIN',
          avatar: 'assets/images/pratham.jpg'
        }
      };
      
      // Store mock data
      localStorage.setItem('token', mockResponse.token);
      localStorage.setItem('user', JSON.stringify(mockResponse.user));
      
      return of(mockResponse);
    }
    
    // Otherwise use the real API
    return this.http.post(`${this.baseUrl}/login`, loginData)
      .pipe(
        tap((response: any) => {
          if (response && response.token) {
            localStorage.setItem('token', response.token);
            
            // Optionally store user info if returned from login
            if (response.user) {
              localStorage.setItem('user', JSON.stringify(response.user));
            }
          }
        })
      );
  }

  /**
   * Get the authenticated user's profile
   */
  getUserProfile(token: string): Observable<UserProfile> {
    // Return mock profile data if using mock mode
    if (this.useMockData) {
      const storedUser = this.getStoredUser();
      
      if (storedUser) {
        return of(storedUser);
      }
      
      const mockProfile: UserProfile = {
        id: 1,
        name: 'Pratham',
        email: 'admin@triviumbuys.com',
        role: 'ADMIN',
        avatar: 'assets/images/pratham.jpg'
      };
      
      return of(mockProfile);
    }
    
    // Otherwise call the real API
    return this.http.get<UserProfile>(`${this.baseUrl}/profile`, {
      headers: this.getAuthHeaders()
    }).pipe(
      catchError(error => {
        console.error('API Error when getting user profile:', error);
        // Fallback to stored user if API fails
        const storedUser = this.getStoredUser();
        return storedUser ? of(storedUser) : throwError(() => error);
      })
    );
  }

  /**
   * Validate the current token
   */
  validateToken(): Observable<any> {
    // In mock mode, always return valid token response
    if (this.useMockData) {
      return of({ valid: true, user: this.getStoredUser() || { id: 1, name: 'Pratham' } });
    }
    
    return this.http.get(`${this.baseUrl}/validate-token`, {
      headers: this.getAuthHeaders()
    });
  }

  /**
   * Log out the current user
   */
  logout(): void {
    if (this.useMockData) {
      this.clearLocalStorage();
      return;
    }
    
    // Optional: Call backend to invalidate token
    const token = this.getToken();
    if (token) {
      this.http.post(`${this.baseUrl}/logout`, {}, {
        headers: this.getAuthHeaders()
      }).subscribe({
        next: () => this.clearLocalStorage(),
        error: () => this.clearLocalStorage()
      });
    } else {
      this.clearLocalStorage();
    }
  }

  /**
   * Check if user is currently logged in
   */
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  /**
   * Get current token from localStorage
   */
  getToken(): string | null {
    return sessionStorage.getItem('token');
  }

  getStoredUser(): UserProfile | null {
    const userData = sessionStorage.getItem('user');
    return userData ? JSON.parse(userData) : null;
  }
  /**
   * Update user profile
   * @param userData Updated user data
   */
  updateProfile(userData: Partial<UserProfile>): Observable<UserProfile> {
    if (this.useMockData) {
      // Update stored user in localStorage
      const currentUser = this.getStoredUser();
      const updatedUser = { ...currentUser, ...userData };
      localStorage.setItem('user', JSON.stringify(updatedUser));
      return of(updatedUser as UserProfile);
    }
    
    return this.http.put<UserProfile>(`${this.baseUrl}/profile`, userData, {
      headers: this.getAuthHeaders()
    }).pipe(
      tap((updatedUser: UserProfile) => {
        // Update stored user data
        const currentUser = this.getStoredUser();
        if (currentUser) {
          localStorage.setItem('user', JSON.stringify({
            ...currentUser,
            ...updatedUser
          }));
        }
      })
    );
  }

  /**
   * Change user password
   * @param passwordData Object containing current and new password
   */
  changePassword(passwordData: { currentPassword: string; newPassword: string }): Observable<any> {
    if (this.useMockData) {
      return of({ success: true, message: 'Password changed successfully' });
    }
    
    return this.http.post(`${this.baseUrl}/change-password`, passwordData, {
      headers: this.getAuthHeaders()
    });
  }

  /**
   * Request password reset
   * @param email User's email address
   */
  requestPasswordReset(email: string): Observable<any> {
    if (this.useMockData) {
      return of({ success: true, message: 'Reset link sent to your email' });
    }
    
    return this.http.post(`${this.baseUrl}/forgot-password`, { email });
  }

  /**
   * Reset password using reset token
   * @param resetData Object containing reset token and new password
   */
  resetPassword(resetData: { token: string; newPassword: string }): Observable<any> {
    if (this.useMockData) {
      return of({ success: true, message: 'Password reset successfully' });
    }
    
    return this.http.post(`${this.baseUrl}/reset-password`, resetData);
  }

  /**
   * Clear all auth data from localStorage
   */
  private clearLocalStorage(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  /**
   * Create authorization headers
   */
  private getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }
}