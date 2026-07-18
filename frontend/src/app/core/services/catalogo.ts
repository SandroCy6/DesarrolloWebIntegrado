import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Pelicula } from '../models/pelicula';
import { Horario } from '../models/horario';
import { Asiento } from '../models/asiento';
@Injectable({
  providedIn: 'root',
})
export class CatalogoService {
  private apiUrl = `${environment.apiUrl}/catalogo/peliculas`;

  constructor(private http: HttpClient) {}

  obtenerTodoPeliculas(): Observable<Pelicula[]>{
    return this.http.get<Pelicula[]>(this.apiUrl);
  }

  obtenerCartelera(): Observable<Pelicula[]> {
    return this.http.get<Pelicula[]>(`${environment.apiUrl}/catalogo/peliculas/cartelera`);
  }

  obtenerProximosEstrenos(): Observable<Pelicula[]> {
    return this.http.get<Pelicula[]>(`${environment.apiUrl}/catalogo/peliculas/proximos-estrenos`);
  }

  obtenerPeliculaPorId(id: number): Observable<Pelicula> {
    return this.http.get<Pelicula>(`${this.apiUrl}/${id}`);
  }

  obtenerEstadoCartelera(id: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${id}/cartelera`);
  }

  obtenerHorariosPorPeliculaYFecha(peliculaId: number, fecha: string): Observable<Horario[]> {
    return this.http.get<Horario[]>(`${environment.apiUrl}/catalogo/horarios/pelicula/${peliculaId}/fecha/${fecha}`);
  }

  obtenerAsientosPorSala(salaId: number): Observable<Asiento[]> {
    return this.http.get<Asiento[]>(`${environment.apiUrl}/api/cines/${salaId}/asientos`);
  }

  obtenerAsientosPorHorario(horarioId: number): Observable<Asiento[]> {
    // Apuntamos al nuevo endpoint transaccional
    return this.http.get<Asiento[]>(`${environment.apiUrl}/api/cines/horarios/${horarioId}/asientos`);
  }

  // --- MÉTODOS ADMINISTRATIVOS (CRUD) ---

  crearPelicula(pelicula: Pelicula): Observable<Pelicula> {
    return this.http.post<Pelicula>(this.apiUrl, pelicula);
  }

  actualizarPelicula(id: number, pelicula: Pelicula): Observable<Pelicula> {
    return this.http.put<Pelicula>(`${this.apiUrl}/${id}`, pelicula);
  }

  eliminarPelicula(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // --- INTEGRACIÓN TMDB ---

  buscarPeliculasTMDB(query: string): Observable<any> {
    // Retorna la cadena JSON directamente desde tu backend
    return this.http.get<any>(`${this.apiUrl}/tmdb/buscar`, { params: { query } });
  }

  importarPeliculaTMDB(tmdbId: number): Observable<Pelicula> {
    return this.http.post<Pelicula>(`${this.apiUrl}/tmdb/importar/${tmdbId}`, {});
  }

  // --- MÉTODOS PARA GESTIÓN DE HORARIOS (FUNCIONES) ---

  obtenerTodosHorarios(): Observable<Horario[]> {
    return this.http.get<Horario[]>(`${environment.apiUrl}/catalogo/horarios`);
  }

  crearHorario(horario: any): Observable<Horario> {
    return this.http.post<Horario>(`${environment.apiUrl}/catalogo/horarios`, horario);
  }

  actualizarHorario(id: number, horario: any): Observable<Horario> {
    return this.http.put<Horario>(`${environment.apiUrl}/catalogo/horarios/${id}`, horario);
  }

  eliminarHorario(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/catalogo/horarios/${id}`);
  }

  // --- MÉTODOS PARA GESTIÓN DE SALAS ---

  obtenerTodasSalas(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/catalogo/salas`);
  }
  
}
