import { Component,OnInit,ChangeDetectorRef } from '@angular/core';
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
export class GestionPeliculasComponent implements OnInit{
  peliculas: Pelicula[] = [];
  
  // Control de vista: 'lista' muestra la tabla, 'formulario' muestra el modo edición/creación
  vistaActual: 'lista' | 'formulario' = 'lista'; 
  
  // Objeto para el formulario
  peliculaActual: Pelicula = this.crearPeliculaVacia();

  // Para TMDB
  busquedaTMDB: string = '';
  resultadosTMDB: any[] = [];

  constructor(private catalogoService: CatalogoService,
    private cdr: ChangeDetectorRef
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
      }
    });
  }

  // --- NAVEGACIÓN INTERNA ---
  
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
      this.catalogoService.actualizarPelicula(this.peliculaActual.id, this.peliculaActual).subscribe(() => {
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

  eliminar(id: number): void {
    if (confirm('¿Estás seguro de eliminar esta película?')) {
      this.catalogoService.eliminarPelicula(id).subscribe(() => {
        this.cargarPeliculas();
      });
    }
  }

  // --- UTILIDADES ---
  
  crearPeliculaVacia(): Pelicula {
    return { id: 0, titulo: '', sinopsis: '', duracion: 0, genero: '', imagenUrl: '', trailerUrl: '', fechaEstreno: '' };
  }
}
