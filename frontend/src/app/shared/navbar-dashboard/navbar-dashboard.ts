import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-navbar-dashboard',
  standalone: false,
  templateUrl: './navbar-dashboard.html',
  styleUrl: './navbar-dashboard.scss'
})
export class NavbarDashboardComponent {
  username = '';
  rol = '';

  constructor(private auth: AuthService) {
    this.username = auth.getUsername();
    this.rol = auth.getRol().replace('ROLE_', '');
  }

  logout(): void {
    this.auth.logout();
  }
}