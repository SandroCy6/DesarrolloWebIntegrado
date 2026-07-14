import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Promocion } from '../../../core/models/promocion';
import { PromocionService } from '../../../core/services/promocion';

@Component({
  selector: 'app-gestion-promociones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-promociones.html',
  styleUrl: './gestion-promociones.scss',
})
export class GestionPromocionesComponent implements OnInit {

  promociones: Promocion[] = [];

  get promocionesActivas(): number {
    return this.promociones.filter(
      promocion => promocion.estado
    ).length;
  }

  vistaActual: 'lista' | 'formulario' = 'lista';

  promocionActual: Promocion = this.crearPromocionVacia();

  cargando = true;
  guardando = false;

  mensaje = '';
  tipoMensaje: 'success' | 'danger' | '' = '';

  constructor(
    private promocionService: PromocionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarPromociones();
  }

  cargarPromociones(): void {
    this.cargando = true;

    this.promocionService.listar().subscribe({
      next: (data) => {
        this.promociones = data;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar promociones:', err);
        this.cargando = false;
        this.mostrarMensaje(
          'No fue posible cargar las promociones.',
          'danger'
        );
        this.cdr.detectChanges();
      }
    });
  }

  abrirFormularioNueva(): void {
    this.promocionActual = this.crearPromocionVacia();
    this.vistaActual = 'formulario';
  }

  abrirFormularioEdicion(promocion: Promocion): void {
    this.promocionActual = { ...promocion,
      regla: promocion.regla ? { ...promocion.regla } : { tipo: '', valor1: 0 }
      };
    this.vistaActual = 'formulario';
  }

  volverALista(): void {
    this.vistaActual = 'lista';
  }

  guardarPromocion(): void {
    if (!this.validarFormulario()) {
      return;
    }

    this.guardando = true;

    const id = this.promocionActual.id_promocion;

    if (id !== undefined) {
      this.promocionService
        .actualizar(id, this.promocionActual)
        .subscribe({
          next: () => {
            this.guardando = false;
            this.volverALista();
            this.cargarPromociones();
            this.mostrarMensaje(
              'Promoción actualizada correctamente.',
              'success'
            );
          },
          error: (err) => {
            console.error('Error al actualizar:', err);
            this.guardando = false;
            this.mostrarMensaje(
              'No fue posible actualizar la promoción.',
              'danger'
            );
          }
        });

    } else {
      this.promocionService
        .crear(this.promocionActual)
        .subscribe({
          next: () => {
            this.guardando = false;
            this.volverALista();
            this.cargarPromociones();
            this.mostrarMensaje(
              'Promoción creada correctamente.',
              'success'
            );
          },
          error: (err) => {
            console.error('Error al crear:', err);
            this.guardando = false;
            this.mostrarMensaje(
              'No fue posible crear la promoción.',
              'danger'
            );
          }
        });
    }
  }

  eliminarPromocion(promocion: Promocion): void {
    if (promocion.id_promocion === undefined) {
      return;
    }

    const confirmar = confirm(
      `¿Deseas eliminar la promoción "${promocion.titulo}"?`
    );

    if (!confirmar) {
      return;
    }

    this.promocionService
      .eliminar(promocion.id_promocion)
      .subscribe({
        next: () => {
          this.cargarPromociones();
          this.mostrarMensaje(
            'Promoción eliminada correctamente.',
            'success'
          );
        },
        error: (err) => {
          console.error('Error al eliminar:', err);
          this.mostrarMensaje(
            'No fue posible eliminar la promoción.',
            'danger'
          );
        }
      });
  }

  validarFormulario(): boolean {
    if (!this.promocionActual.titulo.trim()) {
      this.mostrarMensaje(
        'El título es obligatorio.',
        'danger'
      );
      return false;
    }

    if (!this.promocionActual.tipo.trim()) {
      this.mostrarMensaje(
        'El tipo de promoción es obligatorio.',
        'danger'
      );
      return false;
    }

    if (
      !this.promocionActual.fechaInicio ||
      !this.promocionActual.fechaFin
    ) {
      this.mostrarMensaje(
        'Las fechas de inicio y fin son obligatorias.',
        'danger'
      );
      return false;
    }

    if (
      this.promocionActual.fechaFin <
      this.promocionActual.fechaInicio
    ) {
      this.mostrarMensaje(
        'La fecha de fin no puede ser anterior a la fecha de inicio.',
        'danger'
      );
      return false;
    }

    return true;
  }

  crearPromocionVacia(): Promocion {
    return {
      titulo: '',
      descripcion: '',
      tipo: '',
      fechaInicio: '',
      fechaFin: '',
      estado: true
      // 🔽 REGLA 🔽
            regla: {
              tipo: '',
              valor1: 0
    };
  }

  mostrarMensaje(
    texto: string,
    tipo: 'success' | 'danger'
  ): void {
    this.mensaje = texto;
    this.tipoMensaje = tipo;

    setTimeout(() => {
      this.mensaje = '';
      this.tipoMensaje = '';
      this.cdr.detectChanges();
    }, 3000);
  }
}
