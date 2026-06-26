import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth';
import { Ventas } from '../../../core/services/ventas';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-home-dashboard',
  standalone: false,
  templateUrl: './home-dashboard.html',
  styleUrl: './home-dashboard.scss',
})
export class HomeDashboardComponent implements OnInit {
  username = '';
  cargando = true;

  // KPIs
  totalVentas = 0;
  totalIngresos = 0;
  totalPeliculas = 0;
  totalClientes = 0;

  // Últimas ventas
  ultimasVentas: any[] = [];

  constructor(
    private auth: AuthService,
    private ventasService: Ventas,
    private http: HttpClient,
  ) {
    this.username = auth.getUsername();
  }

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    // Ventas
    this.ventasService.getVentas().subscribe({
      next: (ventas: any[]) => {
        this.totalVentas = ventas.length;
        this.totalIngresos = ventas.reduce((sum, v) => sum + (v.total || 0), 0);
        this.ultimasVentas = ventas.slice(-5).reverse();
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
      },
    });

    // Películas
    this.http.get<any[]>(`${environment.apiUrl}/api/peliculas`).subscribe({
      next: (p) => (this.totalPeliculas = p.length),
      error: () => {},
    });

    // Clientes
    this.http.get<any[]>(`${environment.apiUrl}/api/clientes`).subscribe({
      next: (c) => (this.totalClientes = c.length),
      error: () => {},
    });
  }
}
