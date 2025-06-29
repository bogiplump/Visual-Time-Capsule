import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { map, take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    // AuthGuard now directly checks the isAuthenticated$ BehaviorSubject
    return this.authService.isAuthenticated$.pipe(
      take(1), // Take the current value and complete
      map(isAuthenticated => {
        if (isAuthenticated) {
          return true;
        } else {
          // If not authenticated, navigate to the login page
          return this.router.createUrlTree(['/login']);
        }
      })
    );
  }
}
