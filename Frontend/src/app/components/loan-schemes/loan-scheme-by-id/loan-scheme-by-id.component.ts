import { CommonModule } from '@angular/common';
import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { LoanSchemeService } from '../../../core/services/loan-scheme.service';
import { ToastService } from '../../../core/services/toast.service';
import { LoanSchemeDetails } from '../../../core/models/loan-scheme-detailed.model';
import { AuthService } from '../../../core/services/auth.service';
import { UserRole } from '../../../core/enums/user-role.enum';
import { StudentService } from '../../../core/services/student.service';

@Component({
  selector: 'app-loan-scheme-by-id',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './loan-scheme-by-id.component.html',
  styleUrl: './loan-scheme-by-id.component.css',
})
export class LoanSchemeByIdComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly loanSchemeService = inject(LoanSchemeService);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);
  private readonly studentService = inject(StudentService);

  loanScheme: LoanSchemeDetails | null = null;
  loanSchemeId = '';
  canUpdate = false;
  canApply = false;

  loading = true;
  errorMessage = '';

  get isStudent() : boolean {
    return this.authService.getRole() === UserRole.STUDENT;
  }

  get schemeListRoute(): string {
    return this.authService.getRole() === UserRole.BANK
      ? '/loan-schemes-created-by-me'
      : this.isStudent ? '/loan-schemes/eligible' : '/loan-schemes';
  }

  ngOnInit(): void {
    const loanSchemeId =
      this.route.snapshot.paramMap.get('loanSchemeId')?.trim() ?? '';

    if (!loanSchemeId) {
      this.loading = false;
      this.errorMessage = 'The loan scheme ID is missing from the URL.';

      this.toastService.error(
        this.errorMessage,
        'Unable to load loan scheme'
      );

      return;
    }

    this.loanSchemeId = loanSchemeId;
    this.loadLoanScheme();
  }

  loadLoanScheme(): void {
    if (!this.loanSchemeId) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.loanScheme = null;

    this.loanSchemeService
      .getLoanSchemeById(this.loanSchemeId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (loanScheme) => {
          this.loanScheme = loanScheme;
          this.loading = false;
          this.checkApplicationEligibility();
          if (this.authService.getRole() === UserRole.BANK) {
            this.loanSchemeService.getLoanSchemesCreatedByMe()
              .pipe(takeUntilDestroyed(this.destroyRef))
              .subscribe({
                next: schemes => this.canUpdate = schemes.some(scheme => scheme.loanSchemeId === this.loanSchemeId),
                error: () => this.canUpdate = false
              });
          }
        },
        error: (error: unknown) => {
          this.loading = false;
          this.errorMessage = this.getErrorMessage(error);

          this.toastService.error(
            this.errorMessage,
            'Unable to load loan scheme'
          );
        },
      });
  }

  private checkApplicationEligibility(): void {
    if (!this.isStudent || this.route.snapshot.queryParamMap.get('eligible') !== 'true') return;
    const amount = Number(this.route.snapshot.queryParamMap.get('requestedLoanAmount'));
    if (!Number.isFinite(amount) || amount <= 0) return;
    forkJoin({
      profile: this.studentService.getMyProfile(),
      eligibleSchemes: this.loanSchemeService.getEligibleLoanSchemes(amount)
    }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: ({ profile, eligibleSchemes }) => {
        this.canApply = profile.verificationStatus === 'VERIFIED'
          && eligibleSchemes.some(scheme => scheme.loanSchemeId === this.loanSchemeId);
      },
      error: () => this.canApply = false
    });
  }

  applyForScheme(): void {
    if (!this.canApply) return;
    this.router.navigate(['/applications/apply/LOAN', this.loanSchemeId], {
      queryParams: { requestedLoanAmount: this.route.snapshot.queryParamMap.get('requestedLoanAmount') }
    });
  }

  formatEnumValue(value: string | null | undefined): string {
    if (!value) {
      return 'Not available';
    }

    return value
      .toLowerCase()
      .split('_')
      .map(
        (word) =>
          word.charAt(0).toUpperCase() + word.slice(1)
      )
      .join(' ');
  }

  getStatusClass(status: string | null | undefined): string {
    switch (status?.toUpperCase()) {
      case 'ACTIVE':
      case 'APPROVED':
        return 'badge badge-approved';

      case 'PENDING':
        return 'badge badge-pending';

      case 'INACTIVE':
        return 'badge badge-neutral';

      case 'REJECTED':
        return 'badge badge-rejected';

      default:
        return 'badge badge-info';
    }
  }

  getBooleanBadgeClass(value: boolean): string {
    return value
      ? 'badge badge-approved'
      : 'badge badge-neutral';
  }

  getBooleanLabel(
    value: boolean,
    positiveLabel = 'Yes',
    negativeLabel = 'No'
  ): string {
    return value ? positiveLabel : negativeLabel;
  }

  trackByValue(index: number, value: string): string {
    return value || String(index);
  }

  private getErrorMessage(error: unknown): string {
    if (
      typeof error === 'object' &&
      error !== null &&
      'status' in error
    ) {
      const httpError = error as {
        status?: number;
        error?: {
          message?: string;
        };
      };

      if (httpError.status === 404) {
        return 'The requested loan scheme could not be found.';
      }

      if (httpError.status === 403) {
        return 'You do not have permission to view this loan scheme.';
      }

      if (httpError.status === 0) {
        return 'Unable to connect to the server. Please check whether the backend is running.';
      }

      if (httpError.error?.message) {
        return httpError.error.message;
      }
    }

    return 'Could not load the loan scheme. Please try again.';
  }
}
