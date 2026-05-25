import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ServicesList } from './services-list/services-list';
import { ServiceForm } from './service-form/service-form';

const routes: Routes = [
  {
    path: '',
    component: ServicesList
  },
  {
    path: 'new',
    component: ServiceForm
  },
  {
    path: 'edit/:id',
    component: ServiceForm
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ServicesRoutingModule { }
