import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Ventas } from '../../../core/services/ventas';

@Component({
  selector: 'app-gestion-ventas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-ventas.html',
  styleUrl: './gestion-ventas.scss',
})
export class GestionVentasComponent implements OnInit {
  ventas: any[] = [];
  ventaSeleccionada: any = null;
  nuevoEstado: string = '';
  modalAbierto: boolean = false;

  constructor(private ventasService: Ventas) {}

  ngOnInit(): void {
    this.cargarVentas();
  }

  cargarVentas(): void {
    this.ventasService.getVentas().subscribe({
      next: (response: any) => {
        this.ventas = response?.ventas?.content ?? [];
      },
      error: (err) => {
        console.error('Error al cargar la lista de ventas', err);
        this.ventas = [];
      },
    });
  }

  abrirModalDetalle(id: number | string): void {
    this.ventasService.getVentaById(id).subscribe({
      next: (response: any) => {
        this.ventaSeleccionada = response?.venta ?? null;
        this.nuevoEstado = this.ventaSeleccionada?.estadoPago ?? '';
        this.modalAbierto = true;
      },
      error: (err) => {
        console.error('Error al cargar el detalle de la venta', err);
      },
    });
  }

  cerrarModal(): void {
    this.modalAbierto = false;
    setTimeout(() => {
      this.ventaSeleccionada = null;
    }, 300);
  }

  actualizarEstado(): void {
    if (!this.ventaSeleccionada) return;

    this.ventasService
      .actualizarEstadoVenta(this.ventaSeleccionada.id, this.nuevoEstado)
      .subscribe({
        next: () => {
          alert('Estado actualizado correctamente');
          this.cargarVentas();
          this.cerrarModal();
        },
        error: (err) => {
          console.error('Error al actualizar estado', err);
          alert('Hubo un error al actualizar el estado. Revisa la consola.');
        },
      });
  }
}
