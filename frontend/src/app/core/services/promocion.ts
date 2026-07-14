import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Promocion } from '../models/promocion';

@Injectable({
  providedIn: 'root'
})
export class PromocionService {

  private url = `${environment.apiUrl}/promociones`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Promocion[]> {
    return this.http.get<Promocion[]>(this.url);
  }

  listarActivas(): Observable<Promocion[]> {
    return this.http.get<Promocion[]>(`${this.url}/activas`);
  }

  crear(promocion: Promocion): Observable<Promocion> {
    return this.http.post<Promocion>(this.url, promocion);
  }

  actualizar(id: number, promocion: Promocion): Observable<Promocion> {
    return this.http.put<Promocion>(`${this.url}/${id}`, promocion);
  }

  eliminar(id: number): Observable<string> {
    return this.http.delete(`${this.url}/${id}`, {
      responseType: 'text'
    });
  }
}
