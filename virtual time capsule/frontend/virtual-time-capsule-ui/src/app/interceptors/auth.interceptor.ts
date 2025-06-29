import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, take, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service'; // Import your AuthService

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const accessToken = this.authService.getToken();

    // Add access token to the request if it exists
    if (accessToken) {
      request = this.addToken(request, accessToken);
    }

    return next.handle(request).pipe(
      catchError(error => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          // If 401 and it's not a refresh token request itself, try to refresh
          // Make sure to avoid infinite loop if refresh token itself returns 401
          if (request.url.includes('/refresh')) { // This is to prevent an infinite loop if the refresh endpoint itself fails
            this.authService.logout(); // Directly logout if refresh fails
            return throwError(() => error);
          }
          return this.handle401Error(request, next);
        }
        return throwError(() => error);
      })
    );
  }

  private addToken(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({
      headers: request.headers.set('Authorization', `Bearer ${token}`)
    });
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null); // Clear any pending requests waiting for a token

      return this.authService.refreshToken().pipe(
        switchMap((authResponse: any) => {
          this.isRefreshing = false;
          this.refreshTokenSubject.next(authResponse.accessToken); // Set new access token
          return next.handle(this.addToken(request, authResponse.accessToken)); // Retry original request with new token
        }),
        catchError((errRefresh) => {
          this.isRefreshing = false;
          this.authService.logout(); // Logout if refresh token fails
          return throwError(() => errRefresh);
        })
      );
    } else {
      // If already refreshing, wait for the new token and retry the original request
      return this.refreshTokenSubject.pipe(
        filter(token => token !== null), // Wait until a new token is emitted
        take(1), // Take only one value
        switchMap(token => {
          return next.handle(this.addToken(request, token)); // Retry original request with the new token
        })
      );
    }
  }
}
