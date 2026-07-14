export interface Promocion {
  id_promocion?: number;
  titulo: string;
  descripcion: string;
  tipo: string;
  fechaInicio: string;
  fechaFin: string;
  estado: boolean;

  regla?: {
      tipo: string;
      valor1: number;
    };
}
