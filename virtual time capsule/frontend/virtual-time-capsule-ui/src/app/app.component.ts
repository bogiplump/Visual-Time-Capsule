// src/app/app.component.ts
import { Component, OnInit } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Observable } from 'rxjs';
import { CommonModule, NgIf, AsyncPipe } from '@angular/common'; // Import CommonModule and its pipes
import { RouterModule } from '@angular/router'; // Import RouterModule for routerLink and router-outlet

@Component({
  selector: 'app-root',
  standalone: true, // Mark AppComponent as standalone
  imports: [
    CommonModule, // Provides NgIf, NgFor, etc.
    RouterModule // Provides routerLink, router-outlet
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'] // Link to the component's CSS file
})
export class AppComponent implements OnInit {
  title = 'virtual-time-capsule-ui';

  constructor(protected authService: AuthService) {
  }

  ngOnInit(): void {
    // Initialization logic can go here if needed
  }

  logout(): void {
    this.authService.logout();
  }
}
