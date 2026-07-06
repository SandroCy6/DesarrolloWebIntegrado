export interface Pelicula {
  id?: number;
  tmdbId?: number;
  titulo: string;
  sinopsis: string;
  genero: string;
  duracion: number;
  imagenUrl: string;
  fechaEstreno?: string;
  trailerUrl?: string;
}