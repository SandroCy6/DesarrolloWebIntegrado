export interface Funcion {
  id: number;
  peliculaId: number;
  sala: string;
  fechaHora: string;
  precio: number;
  asientosDisponibles?: number;
}
