import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Ventas } from '../../../core/services/ventas';
import { VentaRequest } from '../../../core/models/venta';

@Component({
  selector: 'app-compra',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './compra.html',
  styleUrl: './compra.scss',
})
export class CompraComponent implements OnInit {
  // Datos recibidos desde detalle-pelicula via queryParams
  asientosDisponibles: string[] = [];
  horarioId: number = 0;
  funcionId: string = '';
  peliculaNombre: string = '';
  fecha: string = '';
  hora: string = '';
  sala: string = '';
  asientosSeleccionados: string[] = [];
  total: number = 0;
  cantidad: number = 0;
  precioPorBoleto: number = 0;

  // Datos del cliente (formulario)
  ventaRequest = {
    dni: '',
    correo: '',
    celular: '',
    nombre: '',
    tokenTarjeta: '',
    metodoPago: 'TARJETA',
    cantidad: 1, // ← re-agrega
    asientos: [] as string[], // ← re-agrega
  };

  // Control de vista
  procesando: boolean = false;
  compraExitosa: boolean = false;
  codigoTransaccion: string = '';
  mensajeError: string = '';

  constructor(
    private route: ActivatedRoute,
    private ventasService: Ventas,
  ) {}
  toggleAsiento(asiento: string): void {
    const index = this.ventaRequest.asientos.indexOf(asiento);
    if (index > -1) {
      this.ventaRequest.asientos.splice(index, 1);
    } else if (this.ventaRequest.asientos.length < this.ventaRequest.cantidad) {
      this.ventaRequest.asientos.push(asiento);
    }
  }
  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.horarioId = +params['horarioId'] || 0;
      this.funcionId = params['funcionId'] || '';
      this.peliculaNombre = params['pelicula'] || '';
      this.fecha = params['fecha'] || '';
      this.hora = params['hora'] || '';
      this.sala = params['sala'] || '';
      this.total = +params['total'] || 0;
      this.cantidad = +params['cantidad'] || 0;
      this.asientosSeleccionados = params['asientos'] ? params['asientos'].split(',') : [];
      this.precioPorBoleto = this.cantidad > 0 ? this.total / this.cantidad : 0;

      // Sincroniza con ventaRequest para que el HTML funcione
      this.ventaRequest.cantidad = this.cantidad;
      this.ventaRequest.asientos = [...this.asientosSeleccionados];
      this.asientosDisponibles = [...this.asientosSeleccionados];
    });
  }

  procesarCompra(): void {
    if (!this.ventaRequest.dni || !this.ventaRequest.correo || !this.ventaRequest.tokenTarjeta) {
      this.mensajeError = 'Por favor completa todos los campos requeridos.';
      return;
    }

    this.mensajeError = '';
    this.procesando = true;

    const payload: VentaRequest = {
      clienteDni: this.ventaRequest.dni,
      clienteCorreo: this.ventaRequest.correo,
      clienteCelular: this.ventaRequest.celular,
      clienteNombre: this.ventaRequest.nombre,
      horarioId: this.horarioId,
      metodoPago: this.ventaRequest.metodoPago,
      tokenTarjeta: this.ventaRequest.tokenTarjeta,
      detalles: this.asientosSeleccionados.map((asiento) => ({
        tipoItem: 'ENTRADA' as const,
        itemId: 0,
        cantidad: 1,
        precioUnitario: this.precioPorBoleto,
      })),
    };

    this.ventasService.createVenta(payload).subscribe({
      next: (response) => {
        this.procesando = false;
        this.compraExitosa = true;
        this.codigoTransaccion = response.id
          ? `TXN-${response.id}`
          : 'TXN-' + Math.floor(Math.random() * 1000000);
      },
      error: (err) => {
        this.procesando = false;
        this.mensajeError = 'Error al procesar el pago. Verifica tus datos e intenta de nuevo.';
        console.error(err);
      },
    });
  }

  calcularTotal(): number {
    return this.total;
  }
}
