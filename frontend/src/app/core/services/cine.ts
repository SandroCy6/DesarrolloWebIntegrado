// src/app/core/services/cine.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cine } from '../models/cine';

@Injectable({
  providedIn: 'root',
})
export class CineService {
  private apiUrl = `${environment.apiUrl}/api/cines`;

  constructor(private http: HttpClient) {}

  obtenerTodos(): Observable<Cine[]> {
    return this.http.get<Cine[]>(this.apiUrl);
  }

  obtenerPorId(id: number): Observable<Cine> {
    return this.http.get<Cine>(`${this.apiUrl}/${id}`);
  }

  crear(cine: Cine): Observable<Cine> {
    return this.http.post<Cine>(this.apiUrl, cine);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
