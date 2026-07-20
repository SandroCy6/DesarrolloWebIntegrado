import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CatalogoService } from '../../../core/services/catalogo';

@Component({
  selector: 'app-gestion-salas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-salas.html',
  styleUrl: './gestion-salas.scss',
})
export class GestionSalasComponent implements OnInit {
  salas: any[] = [];
  cines: any[] = [];

  modalAbierto: boolean = false;

  mensajeExito: string = '';
  mensajeError: string = '';

  salaActual: any = this.crearSalaVacia();

  constructor(
    private catalogoService: CatalogoService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.cargarCines();
    this.cargarSalas();
  }

  cargarCines(): void {
    this.catalogoService.obtenerTodosCines().subscribe({
      next: (data) => {
        this.cines = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al cargar cines', err),
    });
  }

  cargarSalas(): void {
    this.catalogoService.obtenerTodasSalas().subscribe({
      next: (data) => {
        this.salas = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al cargar salas', err),
    });
  }

  abrirModalCrear(): void {
    this.salaActual = this.crearSalaVacia();
    this.modalAbierto = true;
  }

  cerrarModal(): void {
    this.modalAbierto = false;
  }

  guardarSala(): void {
    if (!this.salaActual.numero || this.salaActual.numero <= 0) {
      this.mostrarMensaje('El número de sala es obligatorio.', 'danger');
      return;
    }

    if (!this.salaActual.capacidad || this.salaActual.capacidad <= 0) {
      this.mostrarMensaje('La capacidad debe ser mayor a 0.', 'danger');
      return;
    }

    if (!this.salaActual.cineId) {
      this.mostrarMensaje('Debe seleccionar un cine.', 'danger');
      return;
    }

    const payload = {
      numero: this.salaActual.numero,
      capacidad: this.salaActual.capacidad,
      cine: { id: this.salaActual.cineId },
    };

    this.catalogoService.crearSala(payload).subscribe({
      next: () => {
        this.mostrarMensaje('Sala creada exitosamente.', 'success');
        this.cargarSalas();
        this.cerrarModal();
      },
      error: (err) => {
        console.error('Error al crear sala:', err);
        this.mostrarMensaje(`Error al crear la sala: ${err.status} ${err.statusText}`, 'danger');
      },
    });
  }

  eliminarSala(id: number): void {
    if (confirm('¿Estás seguro de eliminar esta sala?')) {
      this.catalogoService.eliminarSala(id).subscribe({
        next: () => {
          this.mostrarMensaje('Sala eliminada correctamente.', 'success');
          this.cargarSalas();
        },
        error: (err) => {
          console.error('Error al eliminar:', err);
          this.mostrarMensaje(`Error al eliminar: ${err.status} ${err.statusText}`, 'danger');
        },
      });
    }
  }

  crearSalaVacia(): any {
    return {
      numero: null,
      capacidad: null,
      cineId: null,
    };
  }

  mostrarMensaje(texto: string, tipo: 'success' | 'danger'): void {
    if (tipo === 'success') {
      this.mensajeExito = texto;
      this.mensajeError = '';
    } else {
      this.mensajeError = texto;
      this.mensajeExito = '';
    }
    this.cdr.detectChanges();

    setTimeout(() => {
      this.mensajeExito = '';
      this.mensajeError = '';
      this.cdr.detectChanges();
    }, 3000);
  }
}
