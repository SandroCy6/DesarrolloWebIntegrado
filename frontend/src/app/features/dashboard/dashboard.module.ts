import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardRoutingModule } from './dashboard-routing-module';
import { LayoutComponent } from './layout/layout';
import { HomeDashboardComponent } from './home-dashboard/home-dashboard';
import { NavbarDashboardComponent } from '../../shared/navbar-dashboard/navbar-dashboard';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http'; // ← AGREGAR

@NgModule({
  declarations: [LayoutComponent, NavbarDashboardComponent, HomeDashboardComponent],
  imports: [
    CommonModule,
    DashboardRoutingModule,
    RouterModule,
    HttpClientModule, // ← AGREGAR
  ],
})
export class DashboardModule {}
