import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest } from '../models/login-request';
import { LoginResponse } from '../models/login-response';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, data).pipe(
      tap((res) => {
        localStorage.setItem('token', res.token);
        const payload = this.decodeToken(res.token);
        if (payload) {
          localStorage.setItem('username', payload.sub);
          localStorage.setItem('rol', payload.rol);
        }
      }),
    );
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/acceso/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }
  getToken(): string | null {
    return localStorage.getItem('token');
  }
  getUsername(): string {
    return localStorage.getItem('username') || '';
  }

  getRol(): string {
    return localStorage.getItem('rol') || '';
  }

  private decodeToken(token: string): any {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  }
}
