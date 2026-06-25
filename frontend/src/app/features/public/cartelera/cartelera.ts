import { Component,OnInit,ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CatalogoService } from '../../../core/services/catalogo';
import { Pelicula } from '../../../core/models/pelicula';
@Component({
  selector: 'app-cartelera',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cartelera.html',
  styleUrl: './cartelera.scss',
})
export class CarteleraComponent implements OnInit{
  peliculas: Pelicula[] = [];
  cargando: boolean = true;
  error: String | null = null;

  constructor(private catalogoService: CatalogoService,
    private cdr: ChangeDetectorRef){}

  ngOnInit(): void {
    this.cargarPeliculas();
  }

  cargarPeliculas(): void {
    console.log('1. Iniciando petición a la API...'); // Rastreador 1
    this.catalogoService.obtenerCartelera().subscribe({
      next: (data) =>{
        console.log('2. ¡Éxito! Datos recibidos en el componente:', data); // Rastreador 2
        this.peliculas = data;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar la cartelera', err)
        console.error('2. Error atrapado por Angular:', err); // Rastreador 3
        this.error = 'No pudimos cargar la cartelera en este momento. Intenta de nuevo más tarde.'
        this.cargando = false
        this.cdr.detectChanges();
      }
    },)
  }
  
}
