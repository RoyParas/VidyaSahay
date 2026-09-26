import { Routes } from '@angular/router';
import { authGuard, changePasswordGuard, completeProfileGuard, onboardingGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { UserRole } from './core/enums/user-role.enum';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { PublicLayoutComponent } from './layouts/public-layout/public-layout.component';
import { homeRedirectGuard, guestOnlyGuard } from './core/guards/home-redirect.guard';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [guestOnlyGuard],
    loadComponent: () =>
      import('./components/auth/login/login.component')
        .then(c => c.LoginComponent)
  },
  {
    path: 'register',
    canActivate: [guestOnlyGuard],
    loadComponent: () =>
      import('./components/auth/register/register.component')
        .then(c => c.RegisterComponent)
  },
  {
    path: 'change-password',
    canActivate: [authGuard, changePasswordGuard],
    loadComponent: () => import('./components/auth/change-password/change-password.component')
      .then(c => c.ChangePasswordComponent)
  },
  {
    path: 'complete-profile',
    canActivate: [authGuard, completeProfileGuard],
    loadComponent: () => import('./components/student/complete-profile/complete-profile.component')
      .then(c => c.CompleteProfileComponent)
  },
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [homeRedirectGuard],
        loadComponent: () =>
          import('./components/landing-page/landing-page.component')
            .then(c => c.LandingPageComponent)
      }
    ]
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard, roleGuard],
    canActivateChild: [onboardingGuard],
    children: [
      // ADMIN ONLY ROUTES
      {
        path: '',
        data: {roles: [UserRole.ADMIN]},
        children: [
          {
            path: 'students',
            loadComponent: () =>
              import('./components/student/student-list/student-list.component')
                .then(c => c.StudentListComponent)
          },
          {
            path: 'student/:studentId',
            loadComponent: () =>
              import('./components/student/student-by-id/student-by-id.component')
                .then(c => c.StudentByIdComponent)
          },
          {
            path: 'institutes',
            loadComponent: () =>
              import('./components/institute/institute-list/institute-list.component')
                .then(c => c.InstituteListComponent)
          },
          {
            path: 'banks',
            loadComponent: () =>
              import('./components/bank/bank-list/bank-list.component')
                .then(c => c.BankListComponent)
          },
          {
            path: 'loan-schemes',
            loadComponent: () =>
              import('./components/loan-schemes/loan-scheme-list/loan-scheme-list.component')
                .then(c => c.LoanSchemeListComponent)
          },
          {
            path: 'scholarship-schemes',
            loadComponent: () =>
              import('./components/scholarship-schemes/scholarship-scheme-list/scholarship-scheme-list.component')
                .then(c => c.ScholarshipSchemeListComponent)
          }
        ]
      },
      {
        path: 'loan-schemes/eligible',
        canActivate: [roleGuard],
        data: {roles: [UserRole.STUDENT]},
        loadComponent: () =>
          import('./components/student/eligible-loans/eligible-loans.component')
            .then(c => c.EligibleLoansComponent)
      },
      // BANK ONLY ROUTES
      {
        path: '',
        data: {roles: [UserRole.BANK]},
        children: [
          {
            path: 'loan-schemes-created-by-me',
            loadComponent: () =>
              import('./components/loan-schemes/loan-scheme-list/loan-scheme-list.component')
                .then(c => c.LoanSchemeListComponent)
          },
          {
            path: 'loan-schemes/:loanSchemeId',
            loadComponent: () =>
              import('./components/loan-schemes/loan-scheme-by-id/loan-scheme-by-id.component')
                .then(c => c.LoanSchemeByIdComponent)
          },
          {
            path: 'loan-scheme/:loanSchemeId/edit',
            loadComponent: () =>
              import('./components/loan-schemes/create-loan-scheme/create-loan-scheme.component')
                .then(c => c.CreateLoanSchemeComponent)
          },
          {
            path: 'loan-scheme/create',
            loadComponent: () =>
              import('./components/loan-schemes/create-loan-scheme/create-loan-scheme.component')
                .then(c => c.CreateLoanSchemeComponent)
          },
        ]
      },
      {
        path: 'scholarship-schemes/eligible',
        canActivate: [roleGuard],
        data: {roles: [UserRole.STUDENT]},
        loadComponent: () =>
          import('./components/student/eligible-scholarships/eligible-scholarships.component')
            .then(c => c.EligibleScholarshipsComponent)
      },
      {
        path: 'applications/apply/:applicationType/:schemeId',
        canActivate: [roleGuard],
        data: {roles: [UserRole.STUDENT]},
        loadComponent: () =>
          import('./components/application/apply-application/apply-application.component')
            .then(c => c.ApplyApplicationComponent)
      },
      // GOVERNMENT ONLY ROUTES
      {
        path: '',
        data: {roles: [UserRole.GOVERNMENT]},
        children: [
          {
            path: 'scholarship-schemes-created-by-me',
            loadComponent: () =>
              import('./components/scholarship-schemes/scholarship-scheme-list/scholarship-scheme-list.component')
                .then(c => c.ScholarshipSchemeListComponent)
          },
          {
            path: 'scholarship-schemes/:scholarshipSchemeId',
            loadComponent: () =>
              import('./components/scholarship-schemes/scholarship-scheme-by-id/scholarship-scheme-by-id.component')
                .then(c => c.ScholarshipSchemeByIdComponent)
          },
          {
            path: 'scholarship-scheme/:scholarshipSchemeId/edit',
            loadComponent: () =>
              import('./components/scholarship-schemes/create-scholarship-scheme/create-scholarship-scheme.component')
                .then(c => c.CreateScholarshipSchemeComponent)
          },
          {
            path: 'scholarship-scheme/create',
            loadComponent: () =>
              import('./components/scholarship-schemes/create-scholarship-scheme/create-scholarship-scheme.component')
                .then(c => c.CreateScholarshipSchemeComponent)
          }
        ]
      },
      // INSTITUTE ONLY ROUTES
      {
        path: '',
        data: {roles: [UserRole.INSTITUTE]},
        children: [
          {
            path: 'my-students',
            loadComponent: () =>
              import('./components/student/student-list/student-list.component')
                .then(c => c.StudentListComponent)
          },
          {
            path: 'student-verification/:studentId',
            loadComponent: () =>
              import('./components/institute/student-verification/student-verification.component')
                .then(c => c.StudentVerificationComponent)
          },
          {
            path: 'student-applications',
            loadComponent: () =>
              import('./components/application/application-list/application-list.component')
                .then(c => c.ApplicationListComponent)
          },
        ]
      },
      // STUDENT ONLY ROUTES
      {
        path: '',
        data: {roles: [UserRole.STUDENT]},
        children: [
          {
            path: 'my-profile',
            data: {selfProfile: true},
            loadComponent: () =>
              import('./components/student/student-by-id/student-by-id.component')
                .then(c => c.StudentByIdComponent)
          },
          {
            path: 'my-profile/edit',
            data: {selfProfile: true, editProfile: true},
            loadComponent: () =>
              import('./components/student/complete-profile/complete-profile.component')
                .then(c => c.CompleteProfileComponent)
          },
        ]
      },
      // BOTH SCHEME-ID ACESSIBLE BY ADMIN, STUDENT AND SPECIFIC SCHEME ACCESSIBLE BY BANK AND GOVT
      {
        path: '',
        data: {roles: [UserRole.ADMIN, UserRole.STUDENT]},
        children: [
          {
            path: 'scholarship-schemes/:scholarshipSchemeId',
            loadComponent: () =>
              import('./components/scholarship-schemes/scholarship-scheme-by-id/scholarship-scheme-by-id.component')
                .then(c => c.ScholarshipSchemeByIdComponent)
          },
          {
            path: 'loan-schemes/:loanSchemeId',
            loadComponent: () =>
              import('./components/loan-schemes/loan-scheme-by-id/loan-scheme-by-id.component')
                .then(c => c.LoanSchemeByIdComponent)
          }
        ]
      },
      // APPLICATION ROUTES  ACCESSIBLE BY GOVERNMENT, BANK AND STUDENT
      {
        path: '',
        data: {roles: [UserRole.GOVERNMENT, UserRole.BANK, UserRole.STUDENT]},
        children: [
          {
            path: 'my-applications',
            loadComponent: () =>
              import('./components/application/application-list/application-list.component')
                .then(c => c.ApplicationListComponent)
          },
          {
            path: 'application/:applicationId',
            loadComponent: () =>
              import('./components/application/application-by-id/application-by-id.component')
                .then(c => c.ApplicationByIdComponent)
          },
        ]
      }
    ]
  }
];
