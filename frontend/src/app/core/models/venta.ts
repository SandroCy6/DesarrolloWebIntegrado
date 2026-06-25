export interface DetalleVenta {
  tipoItem: 'ENTRADA' | 'SNACK';
  itemId: number;
  cantidad: number;
  precioUnitario: number;
}

export interface VentaRequest {
  clienteDni: string;
  clienteCorreo: string;
  clienteCelular?: string; // opcional en backend
  clienteNombre?: string; // opcional en backend
  horarioId: number; // ← NUEVO, obligatorio
  asientosIds?: number[]; // ← NUEVO
  metodoPago: string; // ← NUEVO, obligatorio (ej: 'visa', 'master')
  tokenTarjeta: string; // ← NUEVO, obligatorio
  detalles: DetalleVenta[];
}
