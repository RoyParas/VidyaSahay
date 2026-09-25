import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { CommonTableColumn, CommonTableComponent } from '../../../shared/common-table/common-table.component';
import { ScholarshipSchemeSummary } from '../../../core/models/summaryResponse.dto';
import { ScholarshipSchemeService } from '../../../core/services/scholarship-scheme.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-eligible-scholarships',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CommonTableComponent],
  templateUrl: './eligible-scholarships.component.html',
  styleUrl: './eligible-scholarships.component.css'
})
export class EligibleScholarshipsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly scholarshipService = inject(ScholarshipSchemeService);
  private readonly toast = inject(ToastService);

  rows: ScholarshipSchemeSummary[] = [];
  loading = true;
  checking = false;
  checked = false;
  submitted = false;
  errorMessage = '';
  readonly form = this.fb.nonNullable.group({
    annualFamilyIncome: ['', [Validators.required, Validators.pattern(/^\d{1,13}(?:\.\d{1,2})?$/), Validators.min(0)]],
    academicPercentage: ['', [Validators.required, Validators.pattern(/^\d{1,3}(?:\.\d{1,2})?$/), Validators.min(0), Validators.max(100)]],
  });
  readonly columns: CommonTableColumn[] = [
    { key: 'scholarshipName', label: 'Scholarship' },
    { key: 'scholarshipType', label: 'Type' },
    { key: 'academicYear', label: 'Academic year' },
    { key: 'status', label: 'Status', type: 'badge', badgeMap: { active: 'vs-badge-verified', approved: 'vs-badge-verified', draft: 'vs-badge-draft', inactive: 'vs-badge-neutral', pending: 'vs-badge-pending', rejected: 'vs-badge-rejected' } },
  ];

  ngOnInit(): void { this.loadAll(); }

  loadAll(): void {
    this.loading = true;
    this.scholarshipService.getScholarshipSchemes().pipe(finalize(() => this.loading = false)).subscribe({
      next: rows => { this.rows = rows; this.errorMessage = ''; },
      error: () => { this.rows = []; this.errorMessage = 'Could not load scholarship schemes. Please try again.'; },
    });
  }

  fieldInvalid(name: 'annualFamilyIncome' | 'academicPercentage'): boolean {
    const field = this.form.controls[name];
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
    const { annualFamilyIncome, academicPercentage } = this.form.getRawValue();
    this.scholarshipService.getEligibleScholarshipSchemes(Number(annualFamilyIncome), Number(academicPercentage))
      .pipe(finalize(() => this.checking = false)).subscribe({
        next: rows => {
          this.rows = rows;
          this.errorMessage = '';
          this.toast.success(`${rows.length} eligible scholarship${rows.length === 1 ? '' : 's'} found.`);
        },
        error: error => {
          this.rows = [];
          this.errorMessage = this.apiError(error, 'No eligible scholarship schemes were found.');
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

  private apiError(error: HttpErrorResponse, fallback: string): string {
    const message = error.error?.message ?? error.error?.detail;
    return typeof message === 'string' && message.trim() ? message : fallback;
  }
}
