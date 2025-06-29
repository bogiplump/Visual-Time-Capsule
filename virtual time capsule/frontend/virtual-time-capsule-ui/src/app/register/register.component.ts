import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserCreateDto } from '../models/auth.models';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'] // Link to the component's CSS file
})
export class RegisterComponent {
  userData: UserCreateDto = { username: '', email: '', password: '', firstName: '', lastName: '' };
  loading = false;
  message: { text: string; type: 'success' | 'error' | '' } = { text: '', type: '' };

  constructor(private authService: AuthService, private router: Router) { }

  /**
   * Handles the registration form submission.
   */
  onSubmit(): void {
    this.loading = true;
    this.message = { text: '', type: '' };

    this.authService.register(this.userData).subscribe({
      next: () => {
        this.message = { text: 'Registration successful! You can now log in.', type: 'success' };
        // Clear form fields after successful registration
        this.userData = { username: '', email: '', password: '', firstName: '', lastName: '' };
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.message = { text: err.message || 'An unexpected error occurred during registration.', type: 'error' };
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  /**
   * Navigates to the login page.
   */
  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
}
