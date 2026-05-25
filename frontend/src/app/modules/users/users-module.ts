import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { UsersRoutingModule } from './users-routing-module';
import { UsersList } from './users-list/users-list';
import { UserForm } from './user-form/user-form';

@NgModule({
  declarations: [
    UsersList,
    UserForm
  ],
  imports: [
    CommonModule,
    UsersRoutingModule,
    ReactiveFormsModule
  ]
})
export class UsersModule { }
