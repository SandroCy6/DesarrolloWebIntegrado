import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PublicRoutingModule } from './public-routing-module';
import { HomeComponent } from './home/home';
import { CarteleraComponent } from './cartelera/cartelera';
import { HeaderComponent } from '../../shared/header/header';
import { FooterComponent } from '../../shared/footer/footer';

@NgModule({
  declarations: [HomeComponent, CarteleraComponent, HeaderComponent, FooterComponent],
  imports: [CommonModule, PublicRoutingModule],
})
export class PublicModule {}
