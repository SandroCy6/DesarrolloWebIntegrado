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

  // Listar todas las ventas
  cargarVentas(): void {
    this.ventasService.getVentas().subscribe({
      next: (data) => {
        this.ventas = data;
      },
      error: (err) => {
        console.error('Error al cargar la lista de ventas', err);
      }
    });
  }

  // Ver Detalle de una venta
  abrirModalDetalle(id: number | string): void {
    this.ventasService.getVentaById(id).subscribe({
      next: (data) => {
        this.ventaSeleccionada = data;
        this.nuevoEstado = data.estadoPago; 
        this.modalAbierto = true; // Mostramos el modal
      },
      error: (err) => {
        console.error('Error al cargar el detalle de la venta', err);
      }
    });
  }

  cerrarModal(): void {
    this.modalAbierto = false;
    setTimeout(() => {
      this.ventaSeleccionada = null;
    }, 300); // Esperar a que termine la animación
  }

  // Actualizar el estado (CRUD)
  actualizarEstado(): void {
    if (!this.ventaSeleccionada) return;

    this.ventasService.actualizarEstadoVenta(this.ventaSeleccionada.id, this.nuevoEstado).subscribe({
      next: (res) => {
        alert('Estado actualizado correctamente');
        this.cargarVentas(); // Refrescar la tabla de ventas
        this.cerrarModal();
      },
      error: (err) => {
        console.error('Error al actualizar estado', err);
        alert('Hubo un error al actualizar el estado. Revisa la consola.');
      }
    });
  }

}
