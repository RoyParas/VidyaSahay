import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const homeRedirectGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  if (!authService.isLoggedIn()) return true;

  return inject(Router).parseUrl(authService.getDefaultRoute());
};

export const guestOnlyGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  if (!authService.isLoggedIn()) return true;

  return inject(Router).parseUrl(authService.getDefaultRoute());
};
