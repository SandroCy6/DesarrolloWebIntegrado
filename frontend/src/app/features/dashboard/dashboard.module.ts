import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardRoutingModule } from './dashboard-routing-module';
import { LayoutComponent } from './layout/layout';
import { HomeDashboardComponent } from './home-dashboard/home-dashboard';
import { NavbarDashboardComponent } from '../../shared/navbar-dashboard/navbar-dashboard';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [
    LayoutComponent,
    NavbarDashboardComponent,
    HomeDashboardComponent,  // ← aquí, en declarations
  ],
  imports: [
    CommonModule,
    DashboardRoutingModule,
    RouterModule,
  ],
})
export class DashboardModule {}