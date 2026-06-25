export interface Pelicula {
  id: number;
  titulo: string;
  sinopsis: string;
  genero: string;
  duracion: number;
  imagenUrl: string;
  fechaEstreno?: string;
  trailerUrl?: string;
}