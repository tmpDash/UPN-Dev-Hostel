import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Profile } from './profile/profile';
import { Config } from './config/config';

const routes: Routes = [
  {
    path: 'profile',
    component: Profile
  },
  {
    path: 'config',
    component: Config
  },
  {
    path: '',
    redirectTo: 'profile',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SettingsRoutingModule { }
