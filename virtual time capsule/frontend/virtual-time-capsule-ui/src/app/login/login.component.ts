import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserLoginDto } from '../models/auth.models';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common'; // Import CommonModule for ngIf

@Component({
  selector: 'app-login',
  standalone: true, // Use standalone components in modern Angular
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'] // Link to the component's CSS file
})
export class LoginComponent {
  credentials: UserLoginDto = { username: '', password: '' };
  loading = false;
  message: { text: string; type: 'success' | 'error' | '' } = { text: '', type: '' };

  constructor(private authService: AuthService, private router: Router) { }

  /**
   * Handles the login form submission.
   */
  onSubmit(): void {
    this.loading = true;
    this.message = { text: '', type: '' };

    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.message = { text: 'Login successful!', type: 'success' };
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.message = { text: err.message || 'An unexpected error occurred during login.', type: 'error' };
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  /**
   * Navigates to the registration page.
   */
  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
