import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { UserRole } from './core/enums/user-role.enum';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { PublicLayoutComponent } from './layouts/public-layout/public-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./components/landing-page/landing-page.component')
            .then(c => c.LandingPageComponent)
      },
      {
        path: 'login',
        loadComponent: () =>
          import('./components/auth/login/login.component')
            .then(c => c.LoginComponent)
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./components/auth/register/register.component')
            .then(c => c.RegisterComponent)
      }
    ]
  },
  {
    path: '',
    component: MainLayoutComponent,
    // canActivate: [authGuard, roleGuard],
    data: {
      roles: [UserRole.ADMIN, UserRole.BANK, UserRole.GOVERNMENT, UserRole.INSTITUTION, UserRole.STUDENT]
    },
    children: []
  }
];