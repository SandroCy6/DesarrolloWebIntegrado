export interface Cliente {
  id?: number;
  dni: string;
  nombre: string;
  correo: string;
  telefono: string;

  activo?: boolean;
  dniVerificado?: boolean;
  intentosFallidos?: number;
  bloqueadoHasta?: string | null;
  fechaRegistro?: string;
  fechaActualizacion?: string;
}
