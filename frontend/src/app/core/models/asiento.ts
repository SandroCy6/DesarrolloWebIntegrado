export enum EstadoAsiento {
  LIBRE = 'LIBRE',
  OCUPADO = 'OCUPADO',
  MANTENIMIENTO = 'MANTENIMIENTO'
}

export interface Asiento {
  id: number;
  numero: string;
  estado: EstadoAsiento;
  precio: number;
  salaId: number;
  horarioId: number;
}