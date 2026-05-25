import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard, RoleGuard } from './core/guards/auth';
import { MainLayout } from './modules/layout/main-layout/main-layout';

const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./modules/auth/auth-module').then(m => m.AuthModule)
  },
  {
    path: '',
    component: MainLayout,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () => import('./modules/dashboard/dashboard-module').then(m => m.DashboardModule)
      },
      {
        path: 'reservations',
        loadChildren: () => import('./modules/reservations/reservations-module').then(m => m.ReservationsModule),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR', 'RECEPCIONISTA'] }
      },
      {
        path: 'services',
        loadChildren: () => import('./modules/services/services-module').then(m => m.ServicesModule)
      },
      {
        path: 'reports',
        loadChildren: () => import('./modules/reports/reports-module').then(m => m.ReportsModule),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR'] }
      },
      {
        path: 'users',
        loadChildren: () => import('./modules/users/users-module').then(m => m.UsersModule),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'settings',
        loadChildren: () => import('./modules/settings/settings-module').then(m => m.SettingsModule)
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
