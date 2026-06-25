import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { CatalogoService } from '../../../core/services/catalogo';
import { Pelicula } from '../../../core/models/pelicula';
import { Horario } from '../../../core/models/horario';
import { Asiento, EstadoAsiento } from '../../../core/models/asiento';

@Component({
  selector: 'app-detalle-pelicula',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './detalle-pelicula.html',
  styleUrl: './detalle-pelicula.scss',
})
export class DetallePeliculaComponent implements OnInit {
  pelicula: Pelicula | null = null;
  horarios: Horario[] = [];
  trailerSeguro: SafeResourceUrl | null = null;
  cargando: boolean = true;

  horarioSeleccionado: Horario | null = null;
  asientos: Asiento[] = [];
  asientosSeleccionados: Asiento[] = [];
  cargandoAsientos: boolean = false;

  cargandoHorarios: boolean = false;
  error: string | null = null;

  fechaSeleccionada: string = new Date().toISOString().split('T')[0];

  constructor(
    private route: ActivatedRoute,
    private catalogoService: CatalogoService,
    private sanitizer: DomSanitizer,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // 1. Capturamos el ID de la URL
    const id = Number(this.route.snapshot.paramMap.get('id'));
    
    if (id) {
      this.cargarDetalle(id);
    } else {
      this.error = "Película no encontrada";
      this.cargando = false;
    }
  }

  cargarDetalle(id: number): void {
    this.catalogoService.obtenerPeliculaPorId(id).subscribe({
      next: (data) => {
        this.pelicula = data;
        
        // 2. Convertimos la URL normal de YouTube a una URL 'embed' segura
        if (data.trailerUrl) {
          // Extrae el ID del video de YouTube (ej: de ?v=avengers a avengers)
          const videoId = data.trailerUrl.split('v=')[1] || data.trailerUrl.split('/').pop();
          const embedUrl = `https://www.youtube.com/embed/${videoId}`;
          this.trailerSeguro = this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
        }

        this.cargando = false;
        this.cdr.detectChanges(); // Forzamos actualización visual
        this.cargarHorarios(id);
      },
      error: (err) => {
        console.error('Error al cargar detalle', err);
        this.error = 'No se pudo cargar la información de la película.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }
  
  cargarHorarios(peliculaId: number): void {
    this.cargandoHorarios = true;
    this.catalogoService.obtenerHorariosPorPeliculaYFecha(peliculaId, this.fechaSeleccionada).subscribe({
      next: (data) => {
        this.horarios = data;
        this.cargandoHorarios = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('No se pudieron cargar los horarios', err);
        this.cargandoHorarios = false;
        this.cdr.detectChanges();
      }
    });
  }

  cambiarFecha(event: any): void {
    this.fechaSeleccionada = event.target.value;
    if (this.pelicula) {
      this.cargarHorarios(this.pelicula.id);
    }
  }

  seleccionarHorario(horario: Horario): void {
    this.horarioSeleccionado = horario;
    this.asientosSeleccionados = []; // Reiniciamos selección
    this.cargandoAsientos = true;
    this.cdr.detectChanges();
    
    this.catalogoService.obtenerAsientosPorHorario(horario.id).subscribe({
      next: (data) => {
        setTimeout(() => {
          this.asientos = data;
          this.cargandoAsientos = false;
          this.cdr.detectChanges();
        }, 0);
      },
      error: (err) => {
        console.error('Error cargando asientos:', err);
        setTimeout(() => {
          this.cargandoAsientos = false;
          this.cdr.detectChanges();
        }, 0);
      }
    });
  }
  // Lógica para marcar o desmarcar un asiento
  toggleAsiento(asiento: Asiento): void {
    if (asiento.estado !== EstadoAsiento.LIBRE) return; // Protección extra

    const index = this.asientosSeleccionados.findIndex(a => a.id === asiento.id);
    if (index > -1) {
      // Si ya estaba seleccionado, lo quitamos
      this.asientosSeleccionados.splice(index, 1);
    } else {
      // Si no estaba, lo agregamos
      this.asientosSeleccionados.push(asiento);
    }
    this.cdr.detectChanges();
  }

  // Verifica visualmente si un asiento está en la lista de seleccionados
  esAsientoSeleccionado(asiento: Asiento): boolean {
    return this.asientosSeleccionados.some(a => a.id === asiento.id);
  }

  // Calcula el total a pagar en tiempo real
  calcularTotal(): number {
    return this.asientosSeleccionados.reduce((total, asiento) => total + asiento.precio, 0);
  }

  obtenerNumerosAsientos(): string {
    return this.asientosSeleccionados.map(asiento => asiento.numero).join(', ');
  }
}
