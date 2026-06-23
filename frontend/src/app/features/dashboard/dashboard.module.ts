import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardRoutingModule } from './dashboard-routing-module';
import { LayoutComponent } from './layout/layout';
import { HomeDashboardComponent } from './home-dashboard/home-dashboard';
import { NavbarDashboardComponent } from '../../shared/navbar-dashboard/navbar-dashboard';

@NgModule({
  declarations: [], 
  imports: [
    CommonModule,
    DashboardRoutingModule,
    LayoutComponent, 
    HomeDashboardComponent,
    NavbarDashboardComponent,
  ],
})
export class DashboardModule {}
