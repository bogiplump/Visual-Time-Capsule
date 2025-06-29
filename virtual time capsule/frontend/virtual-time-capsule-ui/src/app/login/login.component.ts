import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {UserLoginDto} from '../dtos/user-login.dto';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  credentials: UserLoginDto = { username: '', password: '' };
  loading = false;
  message: { text: string; type: 'success' | 'error' | '' } = { text: '', type: '' };

  constructor(private authService: AuthService, private router: Router) { }

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

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
