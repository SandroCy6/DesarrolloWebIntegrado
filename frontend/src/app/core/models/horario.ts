export interface Horario {
  id: number;
  fecha: string;
  horaInicio: string; // Llegará en formato "HH:mm:ss"
  precio: number;
  peliculaId: number;
  tituloPelicula: string;
  salaId: number;
  numeroSala: number;
  cineId: number;
  nombreCine: string;
}