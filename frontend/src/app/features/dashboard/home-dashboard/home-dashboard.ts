import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth';
import { Ventas } from '../../../core/services/ventas';
import { ClienteService } from '../../../core/services/cliente';
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

  totalVentas = 0;
  totalIngresos = 0;
  totalPeliculas = 0;
  totalClientes = 0;

  ultimasVentas: any[] = [];

  constructor(
    private auth: AuthService,
    private ventasService: Ventas,
    private clienteService: ClienteService,
    private http: HttpClient,
  ) {
    this.username = auth.getUsername();
  }

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando = true;
    console.log('Dashboard: iniciando carga');

    this.ventasService.getVentas().subscribe({
      next: (response: any) => {
        console.log('Ventas response:', response);

        const listaVentas = response?.ventas?.content ?? [];
        console.log('Lista ventas:', listaVentas);

        this.totalVentas = listaVentas.length;
        this.totalIngresos = listaVentas.reduce(
          (sum: number, v: any) => sum + Number(v.total || 0),
          0,
        );
        this.ultimasVentas = [...listaVentas].slice(-5).reverse();
        this.cargando = false;

        console.log('Dashboard: carga ventas completada, cargando=', this.cargando);
      },
      error: (err) => {
        console.error('Error al cargar ventas', err);
        this.totalVentas = 0;
        this.totalIngresos = 0;
        this.ultimasVentas = [];
        this.cargando = false;
      },
    });

    this.http.get<any>(`${environment.apiUrl}/catalogo/peliculas`).subscribe({
      next: (p) => {
        console.log('Películas response:', p);
        this.totalPeliculas = Array.isArray(p) ? p.length : (p?.content?.length ?? 0);
      },
      error: (err) => {
        console.error('Error al cargar películas', err);
        this.totalPeliculas = 0;
      },
    });

    this.clienteService.listarClientes().subscribe({
      next: (clientes) => {
        console.log('Clientes response:', clientes);
        this.totalClientes = Array.isArray(clientes) ? clientes.length : 0;
      },
      error: (err) => {
        console.error('Error al cargar clientes', err);
        this.totalClientes = 0;
      },
    });
  }
}
