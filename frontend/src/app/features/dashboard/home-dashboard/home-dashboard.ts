import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-home-dashboard',
  standalone: false,
  templateUrl: './home-dashboard.html',
  styleUrl: './home-dashboard.scss'
})
export class HomeDashboardComponent {
  username = '';
  constructor(private auth: AuthService) {
    this.username = auth.getUsername();
  }
}