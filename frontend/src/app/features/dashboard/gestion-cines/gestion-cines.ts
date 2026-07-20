import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CineService } from '../../../core/services/cine';
import { Cine } from '../../../core/models/cine';

@Component({
  selector: 'app-gestion-cines',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-cines.html',
  styleUrl: './gestion-cines.scss',
})
export class GestionCinesComponent implements OnInit {
  cines: Cine[] = [];

  modalAbierto = false;
  cineActual: Cine = this.crearCineVacio();

  mensajeExito: string | null = null;
  mensajeError: string | null = null;

  constructor(private cineService: CineService) {}

  ngOnInit(): void {
    this.cargarCines();
  }

  cargarCines(): void {
    this.cineService.obtenerTodos().subscribe({
      next: (data) => (this.cines = data),
      error: (err) => {
        console.error('Error cargando cines:', err);
        this.mostrarError('No se pudieron cargar los cines.');
      },
    });
  }

  abrirModalCrear(): void {
    this.cineActual = this.crearCineVacio();
    this.modalAbierto = true;
  }

  cerrarModal(): void {
    this.modalAbierto = false;
  }

  guardarCine(): void {
    if (
      !this.cineActual.nombre?.trim() ||
      !this.cineActual.direccion?.trim() ||
      !this.cineActual.ciudad?.trim()
    ) {
      this.mostrarError('Todos los campos son obligatorios.');
      return;
    }

    this.cineService.crear(this.cineActual).subscribe({
      next: () => {
        this.cargarCines();
        this.cerrarModal();
        this.mostrarExito('Cine creado correctamente.');
      },
      error: (err) => {
        console.error('Error creando cine:', err);
        this.mostrarError('Ocurrió un error al guardar el cine.');
      },
    });
  }

  eliminarCine(id?: number): void {
    if (!id) return;
    if (confirm('¿Estás seguro de eliminar este cine?')) {
      this.cineService.eliminar(id).subscribe({
        next: () => {
          this.cargarCines();
          this.mostrarExito('Cine eliminado correctamente.');
        },
        error: (err) => {
          console.error('Error eliminando cine:', err);
          this.mostrarError('No se pudo eliminar el cine (verifica que no tenga salas asociadas).');
        },
      });
    }
  }

  private mostrarExito(msg: string): void {
    this.mensajeExito = msg;
    this.mensajeError = null;
    setTimeout(() => (this.mensajeExito = null), 3000);
  }

  private mostrarError(msg: string): void {
    this.mensajeError = msg;
    this.mensajeExito = null;
    setTimeout(() => (this.mensajeError = null), 4000);
  }

  private crearCineVacio(): Cine {
    return { nombre: '', direccion: '', ciudad: '' };
  }
}
