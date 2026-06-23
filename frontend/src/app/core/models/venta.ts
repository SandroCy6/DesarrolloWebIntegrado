export interface DetalleVenta {
  tipoItem: 'ENTRADA' | 'SNACK';
  itemId: number;
  cantidad: number;
  precioUnitario: number;
}

export interface VentaRequest {
  clienteDni: string;
  clienteCorreo: string;
  detalles: DetalleVenta[];
}