import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import { Observable, BehaviorSubject, of, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
// Assuming these DTOs are in the 'dtos' folder based on previous discussions
import { UserCreateDto } from '../dtos/user-create.dto';
import { UserLoginDto } from '../dtos/user-login.dto';
import { UsersAuthResponse } from '../dtos/users-auth-response.dto'; // This DTO must include 'user: UserResponseDto'
import { UserResponseDto } from '../dtos/user-response.dto'; // Import UserResponseDto
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth'; // *** IMPORTANT: Replace with your actual backend URL (e.g., http://localhost:8080/auth if /api is not part of it) ***

  // BehaviorSubject to track authentication status (as provided by you)
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidToken());
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  // BehaviorSubject to hold the current user's information (for getting ID)
  private currentUserSubject: BehaviorSubject<UserResponseDto | null>;
  public currentUser$: Observable<UserResponseDto | null>; // Exposed as an observable

  constructor(private http: HttpClient, private router: Router) {
    // Initialize currentUserSubject from localStorage if user data was previously stored
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<UserResponseDto | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  /**
   * Returns the current user's ID if logged in and user data is available, otherwise null.
   */
  getCurrentUserId(): number | null {
    const user = this.currentUserSubject.value;
    return user ? user.id : null;
  }

  /**
   * Checks if an access token exists in localStorage.
   * @returns boolean
   */
  private hasValidToken(): boolean {
    // In a real app, you'd also want to check token expiration
    return !!localStorage.getItem('accessToken');
  }

  public getToken(): string | null {
    return localStorage.getItem("accessToken");
  }

  /**
   * Performs user login.
   * @param credentials UserLoginDto containing username and password.
   * @returns Observable of UsersAuthResponse.
   */
  login(credentials: UserLoginDto): Observable<UsersAuthResponse> {
    return this.http.post<UsersAuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        console.log(response);
        localStorage.setItem('accessToken', response.accessToken);
        localStorage.setItem('refreshToken', response.refreshToken);
        this.isAuthenticatedSubject.next(true);

        // Store user data if provided by the backend
        // *** IMPORTANT: This 'response.user' property requires your Java UsersAuthResponse DTO
        // *** to have 'private UserResponseDto user;' field.
        if (response.user) {
          localStorage.setItem('currentUser', JSON.stringify(response.user));
          this.currentUserSubject.next(response.user);
        } else {
          console.warn('Login response did not include user data. getCurrentUserId() might not work as expected.');
        }
      }),
      catchError(error => {
        console.error('Login failed:', error);
        return throwError(() => new Error(error.error?.message || 'Login failed! Please check your credentials.'));
      })
    );
  }

  /**
   * Registers a new user.
   * @param userData UserCreateDto containing registration details.
   * @returns Observable of any (or a success message).
   */
  register(userData: UserCreateDto): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, userData).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Registration failed:', error);

        let errorMessage = 'Registration failed! An unknown error occurred.';

        if (error && error.error) {
          if (Array.isArray(error.error) && error.error.length > 0) {
            const processedMessages = error.error.map((msg: string) => {
              if (msg.length > 0) {
                msg = msg.charAt(0).toUpperCase() + msg.slice(1);
              }
              return msg;
            });
            processedMessages.sort();
            errorMessage = processedMessages.join('\n');
          } else if (Array.isArray(error.error.errors) && error.error.errors.length > 0) {
            errorMessage = error.error.errors.map((err: any) =>
              err.defaultMessage || err.message || err
            ).join('\n ');
          } else if (typeof error.error.message === 'string' && error.error.message.length > 0) {
            errorMessage = error.error.message;
          } else if (typeof error.error === 'string' && error.error.length > 0) {
            errorMessage = error.error;
          } else if (error.status === 409) {
            errorMessage = 'Registration failed! Username or email might be taken.';
          }
        } else if (error && error.message) {
          errorMessage = error.message;
        }

        console.log(errorMessage);
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  /**
   * Refreshes the access token using the refresh token.
   * @returns Observable of UsersAuthResponse.
   */
  refreshToken(): Observable<UsersAuthResponse> {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
      this.logout();
      return throwError(() => new Error('No refresh token found. Please log in again.'));
    }

    return this.http.post<UsersAuthResponse>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap(response => {
        localStorage.setItem('accessToken', response.accessToken);
        localStorage.setItem('refreshToken', response.refreshToken);
        this.isAuthenticatedSubject.next(true);

        // Store user data if provided by the backend
        // *** IMPORTANT: This 'response.user' property requires your Java UsersAuthResponse DTO
        // *** to have 'private UserResponseDto user;' field.
        if (response.user) {
          localStorage.setItem('currentUser', JSON.stringify(response.user));
          this.currentUserSubject.next(response.user);
        } else {
          console.warn('Refresh token response did not include user data. getCurrentUserId() might not work as expected.');
        }
      }),
      catchError(error => {
        console.error('Token refresh failed:', error);
        this.logout(); // Logout if token refresh fails
        return throwError(() => new Error(error.error?.message || 'Failed to refresh token! Please log in again.'));
      })
    );
  }

  /**
   * Logs out the user by clearing tokens and updating authentication status.
   */
  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser'); // Clear stored user data
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null); // Clear user in BehaviorSubject
    this.router.navigate(['/login']);
  }

  /**
   * Get the current authentication status.
   * @returns boolean
   */
  get isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }
}
