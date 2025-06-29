import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import {CapsuleDetailsComponent} from './capsule-details/capsule-details.component';
import {UsersListComponent} from './users/users-list.component';
import {UserPublicCapsulesComponent} from './users/user-public-capsules.component';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'capsules/:id',
    component: CapsuleDetailsComponent,
      canActivate: [AuthGuard]
  },
  {
    path: 'users',
    component: UsersListComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'users/:userId/capsules',
    component: UserPublicCapsulesComponent,
    canActivate: [AuthGuard]
  },

  { path: '', redirectTo: '/login', pathMatch: 'full' },

  { path: '**', redirectTo: '/login' }
];
