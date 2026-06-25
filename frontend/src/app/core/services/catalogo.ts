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

  obtenerCartelera(): Observable<Pelicula[]>{
    return this.http.get<Pelicula[]>(this.apiUrl);
  }

  obtenerPeliculaPorId(id: number): Observable<Pelicula> {
    return this.http.get<Pelicula>(`${this.apiUrl}/${id}`);
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
}
