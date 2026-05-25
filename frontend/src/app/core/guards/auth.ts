import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth';

export const AuthGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  // Redirigir al login si no está autenticado
  router.navigate(['/auth/login'], { queryParams: { returnUrl: state.url } });
  return false;
};

export const RoleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const expectedRoles = route.data['roles'] as Array<string>;

  if (authService.isAuthenticated() && authService.hasAnyRole(expectedRoles)) {
    return true;
  }

  // Si no tiene permiso, redirigir al dashboard
  router.navigate(['/dashboard']);
  return false;
};
