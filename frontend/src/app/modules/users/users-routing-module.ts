import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UsersList } from './users-list/users-list';
import { UserForm } from './user-form/user-form';

const routes: Routes = [
  {
    path: '',
    component: UsersList
  },
  {
    path: 'new',
    component: UserForm
  },
  {
    path: 'edit/:id',
    component: UserForm
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsersRoutingModule { }
