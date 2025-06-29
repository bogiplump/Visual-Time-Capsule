// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import {CapsuleDetailsComponent} from './capsule-details/capsule-details.component';
import {UsersListComponent} from './users/users-list.component';
import {UserPublicCapsulesComponent} from './users/user-public-capsules.component';

// Lazy load components for better performance (recommended for standalone)
export const routes: Routes = [
  // Authentication Routes with Lazy Loading
  {
    path: 'login',
    loadComponent: () => import('./login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./register/register.component').then(m => m.RegisterComponent)
  },
  // Dashboard Route with Lazy Loading and AuthGuard
  {
    path: 'dashboard',
    loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [AuthGuard] // Protect this route, only authenticated users can access
  },
  // Capsule Details Route (remains eagerly loaded for now)
  {
    path: 'capsules/:id',
    component: CapsuleDetailsComponent,
      canActivate: [AuthGuard] // Protect this route as well
  },
  // User List and Public Capsules Routes (remains eagerly loaded for now)
  {
    path: 'users',
    component: UsersListComponent,
    canActivate: [AuthGuard] // Protect this route
  },
  {
    path: 'users/:userId/capsules',
    component: UserPublicCapsulesComponent,
    canActivate: [AuthGuard] // Protect this route
  },
  // Default Route: Redirect to login page initially
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  // Wildcard Route: Redirect any unmatched paths to the login page
  { path: '**', redirectTo: '/login' }
];
