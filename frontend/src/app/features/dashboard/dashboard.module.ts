import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardRoutingModule } from './dashboard-routing-module';
import { LayoutComponent } from './layout/layout';
import { HomeDashboardComponent } from './home-dashboard/home-dashboard';
import { NavbarDashboardComponent } from '../../shared/navbar-dashboard/navbar-dashboard';
// Agrega los demás componentes conforme los vayas llenando

@NgModule({
  declarations: [
    LayoutComponent,
    HomeDashboardComponent,
    NavbarDashboardComponent
  ],
  imports: [CommonModule, DashboardRoutingModule]
})
export class DashboardModule {}