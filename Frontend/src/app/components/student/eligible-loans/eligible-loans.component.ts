import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { CommonTableColumn, CommonTableComponent } from '../../../shared/common-table/common-table.component';
import { LoanSchemeSummary } from '../../../core/models/summaryResponse.dto';
import { LoanSchemeService } from '../../../core/services/loan-scheme.service';
import { ToastService } from '../../../core/services/toast.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-eligible-loans',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CommonTableComponent],
  templateUrl: './eligible-loans.component.html',
  styleUrl: './eligible-loans.component.css'
})
export class EligibleLoansComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly loanService = inject(LoanSchemeService);
  private readonly router = inject(Router)
  private readonly toast = inject(ToastService);

  rows: LoanSchemeSummary[] = [];
  loading = true;
  checking = false;
  checked = false;
  submitted = false;
  errorMessage = '';
  readonly form = this.fb.nonNullable.group({
    requiredLoanAmount: ['', [Validators.required, Validators.pattern(/^(?:[1-9]\d{0,12}|0)(?:\.\d{1,2})?$/), Validators.min(0.01)]],
  });
  readonly columns: CommonTableColumn[] = [
    { key: 'schemeName', label: 'Scheme' },
    { key: 'interestType', label: 'Interest type' },
    { key: 'minAmount', label: 'Minimum amount', type: 'currency' },
    { key: 'maxAmount', label: 'Maximum amount', type: 'currency' },
    { key: 'status', label: 'Status', type: 'badge', badgeMap: { active: 'vs-badge-verified', approved: 'vs-badge-verified', inactive: 'vs-badge-neutral', pending: 'vs-badge-pending' } },
  ];

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.loading = true;
    this.loanService.getActiveLoanSchemes().pipe(finalize(() => this.loading = false)).subscribe({
      next: rows => { this.rows = rows; this.errorMessage = ''; },
      error: () => { this.rows = []; this.errorMessage = 'Could not load loan schemes. Please try again.'; },
    });
  }

  fieldInvalid(): boolean {
    const field = this.form.controls.requiredLoanAmount;
    return field.invalid && (field.touched || this.submitted);
  }

  checkEligibility(): void {
    this.submitted = true;
    if (this.form.invalid || this.checking) {
      this.form.markAllAsTouched();
      return;
    }
    this.checked = true;
    this.checking = true;
    const amount = Number(this.form.controls.requiredLoanAmount.value);
    this.loanService.getEligibleLoanSchemes(amount).pipe(finalize(() => this.checking = false)).subscribe({
      next: rows => {
        this.rows = rows;
        this.errorMessage = '';
        this.toast.success(`${rows.length} eligible loan scheme${rows.length === 1 ? '' : 's'} found.`);
      },
      error: error => {
        this.rows = [];
        this.errorMessage = this.apiError(error, 'No eligible loan schemes were found.');
        this.toast.warning(this.errorMessage, 'Eligibility check');
      },
    });
  }

  resetEligibility(): void {
    this.checked = false;
    this.submitted = false;
    this.form.reset();
    this.loadAll();
  }

  onRowClick(row: Record<string, unknown>): void {
    const loanSchemeId = row['loanSchemeId'] as string;
    if (!loanSchemeId) return;
    const amount = Number(this.form.controls.requiredLoanAmount.value);
    this.router.navigate(['/loan-schemes', loanSchemeId], {
      queryParams: this.checked && amount > 0 ? { eligible: 'true', requestedLoanAmount: amount } : {}
    });
  }

  private apiError(error: HttpErrorResponse, fallback: string): string {
    const message = error.error?.message ?? error.error?.detail;
    return typeof message === 'string' && message.trim() ? message : fallback;
  }
}
