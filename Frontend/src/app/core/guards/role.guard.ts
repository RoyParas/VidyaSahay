import { CanActivateFn } from '@angular/router';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  const userRole = authService.getRole();
  const allowedRoles = route.data?.['roles'];

  if (!allowedRoles) {
    return true;
  }

  if (allowedRoles.includes(userRole)) {
    return true;
  }

  return router.createUrlTree(['/unauthorized']);
};