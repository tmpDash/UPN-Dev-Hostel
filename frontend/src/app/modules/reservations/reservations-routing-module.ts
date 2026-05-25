import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ReservationList } from './reservation-list/reservation-list';
import { ReservationForm } from './reservation-form/reservation-form';

const routes: Routes = [
  {
    path: '',
    component: ReservationList
  },
  {
    path: 'new',
    component: ReservationForm
  },
  {
    path: 'edit/:id',
    component: ReservationForm
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReservationsRoutingModule { }
