import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PromocionService } from '../../../core/services/promocion';
import { Promocion } from '../../../core/models/promocion';

@Component({
  selector: 'app-promociones',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './promociones.html',
  styleUrl: './promociones.scss',
})
export class Promociones implements OnInit {
  promociones: Promocion[] = [];
  cargando: boolean = true;
  error: string | null = null;

  constructor(
    private promocionService: PromocionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarPromociones();
  }

  cargarPromociones(): void {
    console.log('1. Iniciando petición al Dashboard de Promociones...');

    this.promocionService.listarActivas().subscribe({
      next: (data: Promocion[]) => {
        console.log('2. ¡Éxito! Promociones activas cargadas:', data);
        this.promociones = data;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('Error al conectar con el módulo de promociones', err);
        this.error = 'No pudimos cargar las promociones en este momento. Intenta de nuevo más tarde.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  aplicarOVerMas(promo: Promocion): void {
    console.log('Promoción seleccionada del listado:', promo);
  }
}
