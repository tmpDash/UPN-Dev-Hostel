import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { ServicesRoutingModule } from './services-routing-module';
import { ServicesList } from './services-list/services-list';
import { ServiceForm } from './service-form/service-form';

@NgModule({
  declarations: [ServicesList, ServiceForm],
  imports: [
    CommonModule, 
    ServicesRoutingModule,
    ReactiveFormsModule
  ],
})
export class ServicesModule {}
