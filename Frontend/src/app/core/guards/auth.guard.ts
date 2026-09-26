import { CanActivateChildFn, CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { UserRole } from '../enums/user-role.enum';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const authService = inject(AuthService);

  if (authService.isLoggedIn()) {
    return true;
  }

  return router.createUrlTree(['/login']);
};

export const onboardingGuard: CanActivateChildFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const role = authService.getRole();

  if ((role === UserRole.BANK || role === UserRole.INSTITUTE) && authService.mustChangePassword()) {
    return router.parseUrl('/change-password');
  }

  if (role === UserRole.STUDENT && !authService.isStudentProfileComplete()) {
    return router.parseUrl('/complete-profile');
  }

  return true;
};

export const changePasswordGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const role = authService.getRole();
  return (role === UserRole.BANK || role === UserRole.INSTITUTE) && authService.mustChangePassword()
    ? true
    : router.parseUrl(authService.getDefaultRoute());
};

export const completeProfileGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  return authService.getRole() === UserRole.STUDENT && !authService.isStudentProfileComplete()
    ? true
    : router.parseUrl(authService.getDefaultRoute());
};
