import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // <-- Importante para los inputs
import { CatalogoService } from '../../../core/services/catalogo';
import { Pelicula } from '../../../core/models/pelicula';

@Component({
  selector: 'app-gestion-peliculas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-peliculas.html',
  styleUrl: './gestion-peliculas.scss',
})
export class GestionPeliculasComponent implements OnInit {
  peliculas: Pelicula[] = [];

  // Control de vista: 'lista' muestra la tabla, 'formulario' muestra el modo edición/creación
  vistaActual: 'lista' | 'formulario' | 'importar' = 'lista';

  // Objeto para el formulario
  peliculaActual: Pelicula = this.crearPeliculaVacia();

  // Para TMDB
  busquedaTMDB: string = '';
  resultadosTMDB: any[] = [];
  buscandoTMDB: boolean = false;
  importandoId: number | null = null;

  constructor(
    private catalogoService: CatalogoService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.cargarPeliculas();
  }

  cargarPeliculas(): void {
    this.catalogoService.obtenerTodoPeliculas().subscribe({
      next: (data) => {
        this.peliculas = data;
        this.cdr.detectChanges(); // <-- 3. Forzar el dibujo de la tabla
      },
      error: (err) => {
        console.error('Error cargando la lista de películas:', err);
      },
    });
  }

  // --- NAVEGACIÓN INTERNA ---

  abrirPanelImportar(): void {
    this.vistaActual = 'importar';
    this.busquedaTMDB = '';
    this.resultadosTMDB = [];
  }

  abrirFormularioNueva(): void {
    this.peliculaActual = this.crearPeliculaVacia();
    this.vistaActual = 'formulario';
  }

  abrirFormularioEdicion(peli: Pelicula): void {
    // Clonamos el objeto para no editar la tabla en tiempo real hasta guardar
    this.peliculaActual = { ...peli };
    this.vistaActual = 'formulario';
  }

  volverALista(): void {
    this.vistaActual = 'lista';
    this.resultadosTMDB = [];
    this.busquedaTMDB = '';
  }

  // --- OPERACIONES CRUD BÁSICAS ---

  guardarPelicula(): void {
    if (this.peliculaActual.id) {
      this.catalogoService
        .actualizarPelicula(this.peliculaActual.id, this.peliculaActual)
        .subscribe(() => {
          this.cargarPeliculas();
          this.volverALista();
        });
    } else {
      this.catalogoService.crearPelicula(this.peliculaActual).subscribe(() => {
        this.cargarPeliculas();
        this.volverALista();
      });
    }
  }

  // Agrega estas propiedades al componente:
  mensajeExito: string = '';
  mensajeError: string = '';

  eliminar(id: number): void {
    if (confirm('¿Estás seguro de eliminar esta película?')) {
      this.catalogoService.eliminarPelicula(id).subscribe({
        next: () => {
          this.mensajeExito = 'Película eliminada correctamente.';
          this.mensajeError = '';
          this.cargarPeliculas();
          // Ocultar mensaje después de 3 segundos
          setTimeout(() => {
            this.mensajeExito = '';
            this.cdr.detectChanges();
          }, 3000);
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error al eliminar:', err);
          this.mensajeError = `Error al eliminar: ${err.status} ${err.statusText}`;
          this.mensajeExito = '';
          this.cdr.detectChanges();
          setTimeout(() => {
            this.mensajeError = '';
            this.cdr.detectChanges();
          }, 3000);
        },
      });
    }
  }

  // --- INTEGRACIÓN TMDB ---
  buscarTMDB(): void {
    if (!this.busquedaTMDB.trim()) return;
    this.buscandoTMDB = true;
    this.catalogoService.buscarPeliculasTMDB(this.busquedaTMDB).subscribe({
      next: (respuesta) => {
        const data = typeof respuesta === 'string' ? JSON.parse(respuesta) : respuesta;

        this.resultadosTMDB = data.results || [];
        this.buscandoTMDB = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.buscandoTMDB = false;
        this.cdr.detectChanges();
      },
    });
  }

  importarDeTMDB(tmdbId: number): void {
    this.importandoId = tmdbId;
    this.cdr.detectChanges();

    this.catalogoService.importarPeliculaTMDB(tmdbId).subscribe({
      next: (peliculaImportada) => {
        this.importandoId = null;
        this.cargarPeliculas();
        this.volverALista();
      },
      error: (err) => {
        console.error('Error al importar Peliculas:', err);
        alert('Hubo un error al importar. Revisa la consola');
        this.importandoId = null;
        this.cdr.detectChanges();
      },
    });
  }

  // --- UTILIDADES ---

  crearPeliculaVacia(): Pelicula {
    return {
      id: undefined,
      tmdbId: undefined,
      titulo: '',
      sinopsis: '',
      duracion: 0,
      genero: '',
      imagenUrl: '',
      trailerUrl: '',
      fechaEstreno: '',
    };
  }
}
