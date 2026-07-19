import { Component, OnInit,ChangeDetectorRef } from '@angular/core';
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
    private cdr: ChangeDetectorRef,
  ) {
    this.username = auth.getUsername();
  }

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    // 1. Cargar Ventas
    this.ventasService.getVentas().subscribe({
      next: (ventas: any) => {
        // Manejamos si viene con paginación (data.content) o un arreglo directo
        const listaVentas = ventas.content ? ventas.content : ventas;
        
        this.totalVentas = listaVentas.length;
        this.totalIngresos = listaVentas.reduce((sum: any, v: any) => sum + (v.total || 0), 0);
        this.ultimasVentas = listaVentas.slice(-5).reverse();
        this.cargando = false; 
        this.cdr.detectChanges();
      },
      error: () => {
        this.cargando = false; 
        this.cdr.detectChanges();
      },
    });

    // 2. Cargar Películas (Ruta Corregida a /catalogo/peliculas)
    this.http.get<any[]>(`${environment.apiUrl}/catalogo/peliculas`).subscribe({
      next: (p) => {
        this.totalPeliculas = p.length;
        this.cdr.detectChanges();
      },
      error: () => {
        this.totalPeliculas = 0; // Evita que se quede colgado si falla
      },
    });

    // 3. Cargar Clientes/Usuarios (Ruta Corregida a /usuarios)
    this.http.get<any[]>(`${environment.apiUrl}/usuarios`).subscribe({
      next: (c) => {
        this.totalClientes = c.length;
        this.cdr.detectChanges();
      },
      error: () => {
        this.totalClientes = 0; // Evita que se quede colgado si falla
      },
    });
  }
}
