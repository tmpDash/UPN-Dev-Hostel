import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { ReservationsRoutingModule } from './reservations-routing-module';
import { ReservationList } from './reservation-list/reservation-list';
import { ReservationForm } from './reservation-form/reservation-form';

@NgModule({
  declarations: [
    ReservationList,
    ReservationForm
  ],
  imports: [
    CommonModule,
    ReservationsRoutingModule,
    ReactiveFormsModule
  ]
})
export class ReservationsModule { }
