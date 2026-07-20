import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout';
import { HomeDashboardComponent } from './home-dashboard/home-dashboard';
import { GestionPeliculasComponent } from './gestion-peliculas/gestion-peliculas';
import { GestionClientesComponent } from './gestion-clientes/gestion-clientes';
import { GestionPromocionesComponent } from './gestion-promociones/gestion-promociones';
import { GestionVentasComponent } from './gestion-ventas/gestion-ventas';
import { GestionFuncionesComponent } from './gestion-funciones/gestion-funciones';
import { GestionSalasComponent } from './gestion-salas/gestion-salas';
import { GestionCinesComponent } from './gestion-cines/gestion-cines';
const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },

      { path: 'home', component: HomeDashboardComponent },

      { path: 'gestion-peliculas', component: GestionPeliculasComponent },

      { path: 'gestion-clientes', component: GestionClientesComponent },

      { path: 'gestion-promociones', component: GestionPromocionesComponent },

      { path: 'gestion-ventas', component: GestionVentasComponent },

      { path: 'gestion-funciones', component: GestionFuncionesComponent },

      { path: 'gestion-salas', component: GestionSalasComponent },

      { path: 'gestion-cines', component: GestionCinesComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashboardRoutingModule {}
