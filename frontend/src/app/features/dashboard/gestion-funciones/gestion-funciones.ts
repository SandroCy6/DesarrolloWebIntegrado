import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CatalogoService } from '../../../core/services/catalogo';
import { Pelicula } from '../../../core/models/pelicula';
import { Horario } from '../../../core/models/horario';

@Component({
  selector: 'app-gestion-funciones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-funciones.html',
  styleUrl: './gestion-funciones.scss',
})
export class GestionFuncionesComponent implements OnInit {
  funciones: any[] = [];
  peliculas: any[] = [];
  salas: any[] = [];

  // Control del Modal
  modalAbierto: boolean = false;
  modoEdicion: boolean = false;

  // Objeto para el formulario
  funcionActual: any = {
    peliculaId: null,
    salaId: null,
    fecha: '',
    horaInicio: '',
    precio: 0
  };

  constructor(private catalogoService: CatalogoService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.cargarDatosAdicionales();
    this.cargarFunciones();
  }

  // Cargar películas y salas para los <select> del formulario
  cargarDatosAdicionales(): void {
    this.catalogoService.obtenerTodoPeliculas().subscribe({
      next: (data) => this.peliculas = data,
      error: (err) => console.error('Error al cargar películas', err)
    });
    this.catalogoService.obtenerTodasSalas().subscribe({
      next: (data) => this.salas = data,
      error: (err) => console.error('Error al cargar salas', err)
    });
  }

  // 1. READ: Listar Funciones (Horarios)
  cargarFunciones(): void {
    this.catalogoService.obtenerTodosHorarios().subscribe({
      next: (data) => {
        this.funciones = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al cargar las funciones', err)
    });
  }

  // Abrir modal para CREATE
  abrirModalCrear(): void {
    this.modoEdicion = false;
    this.funcionActual = {
      peliculaId: null,
      salaId: null,
      fecha: '',
      horaInicio: '', // formato HH:mm:ss o HH:mm
      precio: 0
    };
    this.modalAbierto = true;
  }

  // Abrir modal para UPDATE
  abrirModalEditar(funcion: any): void {
    this.modoEdicion = true;
    this.funcionActual = { ...funcion }; // Clonamos para no afectar la tabla directamente
    this.modalAbierto = true;
  }

  cerrarModal(): void {
    this.modalAbierto = false;
  }

  // 2 y 3. CREATE & UPDATE: Guardar Función
  guardarFuncion(): void {

    const payload = {
      fecha: this.funcionActual.fecha,
      horaInicio: this.funcionActual.horaInicio,
      precio: this.funcionActual.precio,
      pelicula: { id: this.funcionActual.peliculaId },
      sala: { id: this.funcionActual.salaId }
    };
    // 2. Enviamos el payload moldeado en vez de this.funcionActual
    if (this.modoEdicion) {
      this.catalogoService.actualizarHorario(this.funcionActual.id, payload).subscribe({
        next: () => {
          alert('Función actualizada exitosamente');
          this.cargarFunciones();
          this.cerrarModal();
        },
        error: (err) => alert('Error al actualizar la función.')
      });
    } else {
      this.catalogoService.crearHorario(payload).subscribe({
        next: () => {
          alert('Función programada exitosamente');
          this.cargarFunciones();
          this.cerrarModal();
        },
        error: (err) => alert('Error al crear la función.')
      });
    }
  }

  // 4. DELETE: Eliminar Función
  eliminarFuncion(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar esta función de la cartelera?')) {
      this.catalogoService.eliminarHorario(id).subscribe({
        next: () => {
          alert('Función eliminada correctamente');
          this.cargarFunciones();
        },
        error: (err) => alert('Error al eliminar la función.')
      });
    }
  }

}
