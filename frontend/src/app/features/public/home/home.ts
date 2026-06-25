import { Component,OnInit,ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CatalogoService } from '../../../core/services/catalogo';
import { Pelicula } from '../../../core/models/pelicula';

@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterModule],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class HomeComponent {
  peliculasDestacadas: Pelicula[] = [];
  proximosEstrenos: Pelicula[] = [];
  cargandoProximos: boolean = true;
  cargando: boolean = true;
  error: string | null = null;
  errorProximos: string | null = null;

  constructor(
    private catalogoService: CatalogoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarDestacadas();
    this.cargarProximosEstrenos();
  }

  cargarDestacadas(): void {
    this.catalogoService.obtenerCartelera().subscribe({
      next: (data) => {
        // Tomamos solo las primeras 4 películas para la sección de destacadas
        this.peliculasDestacadas = data.slice(0, 4);
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error cargando destacadas:', err);
        this.error = 'No pudimos cargar los estrenos destacados.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  cargarProximosEstrenos(): void {
    this.catalogoService.obtenerProximosEstrenos().subscribe({
      next: (data) => {
        // Tomamos también las 4 primeras para mantener simetría
        this.proximosEstrenos = data.slice(0, 4);
        this.cargandoProximos = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorProximos = 'No pudimos cargar los próximos estrenos.';
        this.cargandoProximos = false;
        this.cdr.detectChanges();
      }
    });
  }

}
