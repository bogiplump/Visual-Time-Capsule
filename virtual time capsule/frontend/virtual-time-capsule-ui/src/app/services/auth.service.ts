import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import { Observable, BehaviorSubject, of, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { UserCreateDto } from '../dtos/user-create.dto';
import { UserLoginDto } from '../dtos/user-login.dto';
import { UsersAuthResponse } from '../dtos/users-auth-response.dto';
import { UserResponseDto } from '../dtos/user-response.dto';
import { Router } from '@angular/router';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidToken());
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();


  public currentUserSubject: BehaviorSubject<UserResponseDto | null>;
  public currentUser$: Observable<UserResponseDto | null>;

  constructor(private http: HttpClient, private router: Router) {

    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<UserResponseDto | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  public getCurrentUsername(): string {
    return this.currentUserSubject.getValue()?.username || '';
  }


  getCurrentUser(): Observable<UserResponseDto | null> {
    return this.currentUser$;
  }

  private hasValidToken(): boolean {

    return !!localStorage.getItem('accessToken');
  }

  public getToken(): string | null {
    return localStorage.getItem("accessToken");
  }

  login(credentials: UserLoginDto): Observable<UsersAuthResponse> {
    return this.http.post<UsersAuthResponse>(`${environment.backendUrl}/auth/login`, credentials).pipe(
      tap(response => {
        console.log(response);
        localStorage.setItem('accessToken', response.accessToken);
        localStorage.setItem('refreshToken', response.refreshToken);
        this.isAuthenticatedSubject.next(true);

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

  register(userData: UserCreateDto): Observable<any> {
    return this.http.post<any>(`${environment.backendUrl}/auth/register`, userData).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Registration failed:', error);
        console.log(error.error);
        return throwError(() => new Error(error.error));
      })
    );
  }

  refreshToken(): Observable<UsersAuthResponse> {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
      this.logout();
      return throwError(() => new Error('No refresh token found. Please log in again.'));
    }

    return this.http.post<UsersAuthResponse>(`${environment.backendUrl}/auth/refresh`, { refreshToken }).pipe(
      tap(response => {
        localStorage.setItem('accessToken', response.accessToken);
        localStorage.setItem('refreshToken', response.refreshToken);
        this.isAuthenticatedSubject.next(true);

        if (response.user) {
          localStorage.setItem('currentUser', JSON.stringify(response.user));
          this.currentUserSubject.next(response.user);
        } else {
          console.warn('Refresh token response did not include user data. getCurrentUserId() might not work as expected.');
        }
      }),
      catchError(error => {
        console.error('Token refresh failed:', error);
        this.logout();
        return throwError(() => new Error(error.error?.message || 'Failed to refresh token! Please log in again.'));
      })
    );
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser');
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }
}
