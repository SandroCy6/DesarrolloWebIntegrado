import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { VentaRequest } from '../models/venta';

@Injectable({
  providedIn: 'root',
})
export class Ventas {
  private apiUrl = `${environment.apiUrl}/api/ventas`;

  getVentas(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getVentaById(id: number | string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  createVenta(venta: VentaRequest): Observable<any> {
    return this.http.post<any>(this.apiUrl, venta);
  }

  actualizarEstadoVenta(id: number | string, estado: string): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/estado`, { estado });
  }
}
