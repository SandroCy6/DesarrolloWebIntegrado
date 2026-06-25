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
  funcionId: string = '';

  // Objeto que se enviará al backend
  ventaRequest = {
    dni: '',
    cantidad: 1,
    metodoPago: 'TARJETA',
    asientos: [] as string[],
  };

  // Datos simulados (luego se conectarán con el servicio de Catálogo)
  asientosDisponibles: string[] = ['A1', 'A2', 'A3', 'B1', 'B2', 'B3'];
  precioPorBoleto: number = 20.0;

  // Control de la vista
  procesando: boolean = false;
  compraExitosa: boolean = false;
  codigoTransaccion: string = '';
  mensajeError: string = '';

  constructor(
    private route: ActivatedRoute,
    private ventasService: Ventas,
  ) {}

  ngOnInit(): void {
    // Obtenemos el ID de la función desde la URL (ej: /compra/12)
    this.funcionId = this.route.snapshot.paramMap.get('id') || '';
  }

  toggleAsiento(asiento: string) {
    const index = this.ventaRequest.asientos.indexOf(asiento);
    if (index > -1) {
      this.ventaRequest.asientos.splice(index, 1);
    } else if (this.ventaRequest.asientos.length < this.ventaRequest.cantidad) {
      this.ventaRequest.asientos.push(asiento);
    }
  }

  calcularTotal(): number {
    return this.ventaRequest.cantidad * this.precioPorBoleto;
  }
  // Reemplaza el método procesarCompra() con esto:
  procesarCompra() {
    if (this.ventaRequest.asientos.length !== this.ventaRequest.cantidad) {
      this.mensajeError = 'Selecciona todos tus asientos antes de continuar.';
      return;
    }

    this.mensajeError = '';
    this.procesando = true;

    const payload: VentaRequest = {
      clienteDni: this.ventaRequest.dni,
      clienteCorreo: '', // agrega un campo en ventaRequest si lo necesitas
      horarioId: +this.funcionId, // convierte string a number
      metodoPago: this.ventaRequest.metodoPago,
      tokenTarjeta: '', // agrega campo o usa valor temporal
      detalles: this.ventaRequest.asientos.map((asiento) => ({
        tipoItem: 'ENTRADA' as const,
        itemId: 0, // adapta según tu lógica de asientos
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
        this.mensajeError = 'Error al procesar el pago. Verifica tus datos o intenta más tarde.';
        console.error(err);
      },
    });
  }
}
