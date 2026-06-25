import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PublicRoutingModule } from './public-routing-module';

import { CompraComponent } from './compra/compra';
import { HomeComponent } from './home/home';
import { CarteleraComponent } from './cartelera/cartelera';
import { HeaderComponent } from '../../shared/header/header';
import { FooterComponent } from '../../shared/footer/footer';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    PublicRoutingModule,
    HomeComponent,
    CarteleraComponent,
    HeaderComponent,
    FooterComponent,
    CompraComponent
  ],
})
export class PublicModule {}
