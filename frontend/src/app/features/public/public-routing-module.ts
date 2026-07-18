import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home';
import { CarteleraComponent } from './cartelera/cartelera';
import { DetallePeliculaComponent } from './detalle-pelicula/detalle-pelicula';
import { CompraComponent } from './compra/compra';
import { Promociones } from './promociones/promociones';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'cartelera', component: CarteleraComponent },
  { path: 'detalle-pelicula/:id', component: DetallePeliculaComponent },
  { path: 'compra', component: CompraComponent },
  { path: 'promociones', component: Promociones }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PublicRoutingModule {}
