import { AfterViewInit, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { Ventas } from '../../../core/services/ventas';
import { VentaRequest } from '../../../core/models/venta';

declare var MercadoPago: any;

@Component({
  selector: 'app-compra',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './compra.html',
  styleUrl: './compra.scss',
})
export class CompraComponent implements OnInit, AfterViewInit {
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
    cantidad: 1, // re-agrega
    asientos: [] as string[], // re-agrega
  };

  // Control de vista
  titularTarjeta: string = '';
  procesando: boolean = false;
  compraExitosa: boolean = false;
  codigoTransaccion: string = '';
  mensajeError: string = '';

  // Variables para Mercado Pago
  mp: any;
  cardNumberElement: any;
  expirationDateElement: any;
  securityCodeElement: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router, // <-- Inyectado Router para redirecciones
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

  // Inicializamos Mercado Pago DESPUES de que el HTML cargo
  ngAfterViewInit(): void {
    this.mp = new MercadoPago('TEST-2cd64bf7-a255-4f9a-be35-1fbc8e3bff4b', { locale: 'es-PE' });

    // Montamos los campos con texto blanco para diseño dark
    const styleConfig = { style: { color: '#ffffff' } };

    this.cardNumberElement = this.mp.fields.create('cardNumber', { 
        placeholder: '0000 0000 0000 0000', ...styleConfig 
    }).mount('form-checkout__cardNumber');

    this.expirationDateElement = this.mp.fields.create('expirationDate', { 
        placeholder: 'MM/YY', ...styleConfig 
    }).mount('form-checkout__expirationDate');

    this.securityCodeElement = this.mp.fields.create('securityCode', { 
        placeholder: 'CVV', ...styleConfig 
    }).mount('form-checkout__securityCode');
  }

  async procesarCompra() {
    if (!this.ventaRequest.dni || !this.ventaRequest.correo) {
      this.mensajeError = 'Por favor completa todos los campos requeridos.';
      return;
    }

    this.mensajeError = '';
    this.procesando = true;

    try {
      // PASO 1: Pedirle a Mercado Pago que genere el Token real
      const tokenResponse = await this.mp.fields.createCardToken({
        cardholderName: this.titularTarjeta,
        identificationType: 'DNI',
        identificationNumber: this.ventaRequest.dni,
      });

      if (!tokenResponse || !tokenResponse.id) {
        throw new Error("No se pudo generar el token");
      }

      // PASO 2: Asignamos el token real a nuestro Request
      this.ventaRequest.tokenTarjeta = tokenResponse.id;

      // PASO 3: Construimos el payload y enviamos al Backend
      const payload: VentaRequest = {
        clienteDni: this.ventaRequest.dni,
        clienteCorreo: this.ventaRequest.correo,
        clienteCelular: this.ventaRequest.celular,
        clienteNombre: this.ventaRequest.nombre,
        horarioId: this.horarioId,
        metodoPago: 'master', // Puede hacerlo dinamico despues
        tokenTarjeta: this.ventaRequest.tokenTarjeta,
        asientosIds: this.asientosSeleccionados.map((_, index) => index + 1), // se mapea los IDs numericos si se requiere
        detalles: this.asientosSeleccionados.map((asiento) => ({
          tipoItem: 'ENTRADA' as const,
          itemId: 0, //
          cantidad: 1,
          precioUnitario: this.precioPorBoleto,
        })),
      };

      this.ventasService.createVenta(payload).subscribe({
        next: (response) => {
          this.procesando = false;

          // Logica de Mercado Pago
          if (response.estadoPago === 'RECHAZADO') {
            this.compraExitosa = false;
            this.mensajeError = 'Tu pago fue rechazado por Mercado Pago. Verifica el saldo de tarjeta.';
            return;
          }

          this.compraExitosa = true;
          this.codigoTransaccion = response.id
            ? `TXN-${response.id}`
            : 'TXN-' + Math.floor(Math.random() * 1000000);
        },
        error: (err) => {
          this.procesando = false;
          this.mensajeError = err.error?.message || 'Error de conexión con la pasarela de pago. Intenta de nuevo.';
          console.error(err);
        },
      });

    } catch (error) {
    this.procesando = false;
    this.mensajeError = 'Revisa los datos de tu tarjeta, son incorrectos.';
    console.error('Error de MP:', error);
    }
  }

  calcularTotal(): number {
    return this.total;
  }

  volverAlInicio(): void {
    this.router.navigate(['/cartelera']);
  }

  imprimirTicket(): void {
    window.print();
  }
}
