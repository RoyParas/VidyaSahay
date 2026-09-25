import { CommonModule } from '@angular/common';
import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { LoanSchemeService } from '../../../core/services/loan-scheme.service';
import { ToastService } from '../../../core/services/toast.service';
import { LoanSchemeDetails } from '../../../core/models/loan-scheme-detailed.model';

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
  private readonly toastService = inject(ToastService);
  private readonly destroyRef = inject(DestroyRef);

  loanScheme: LoanSchemeDetails | null = null;
  loanSchemeId = '';

  loading = true;
  errorMessage = '';

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