import { Component, OnInit } from '@angular/core';
import { AuthService } from './services/auth.service';
import { CommonModule} from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  constructor(protected authService: AuthService) {
  }

  ngOnInit(): void {

  }

  logout(): void {
    this.authService.logout();
  }
}
