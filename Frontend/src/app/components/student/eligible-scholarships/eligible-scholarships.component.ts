import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router'
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { CommonTableColumn, CommonTableComponent } from '../../../shared/common-table/common-table.component';
import { ScholarshipSchemeSummary } from '../../../core/models/summaryResponse.dto';
import { ScholarshipSchemeService } from '../../../core/services/scholarship-scheme.service';
import { ToastService } from '../../../core/services/toast.service';
import { StudentService } from '../../../core/services/student.service';

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
  private readonly router = inject(Router);
  private readonly studentService = inject(StudentService);

  rows: ScholarshipSchemeSummary[] = [];
  loading = true;
  checking = false;
  checked = false;
  submitted = false;
  errorMessage = '';
  profileLoading = true;
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

  ngOnInit(): void {
    this.loadAll();
    this.studentService.getMyProfile().subscribe({
      next: profile => {
        this.form.controls.annualFamilyIncome.setValue(String(profile.annualFamilyIncome));
        this.form.controls.annualFamilyIncome.disable();
        this.profileLoading = false;
      },
      error: err => {
        this.profileLoading = false;
        this.toast.error(err?.error?.message ?? 'Could not load your profile income.');
      }
    });
  }

  loadAll(): void {
    this.loading = true;
    this.scholarshipService.getActiveScholarshipSchemes().pipe(finalize(() => this.loading = false)).subscribe({
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
    if (this.form.invalid || this.checking || this.profileLoading) {
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

  onRowClick(row: Record<string, unknown>): void {
    const scholarshipSchemeId = row['scholarshipSchemeId'] as string;
    if (!scholarshipSchemeId) return;
    const { annualFamilyIncome, academicPercentage } = this.form.getRawValue();
    this.router.navigate(['/scholarship-schemes', scholarshipSchemeId], {
      queryParams: this.checked && academicPercentage !== ''
        ? { eligible: 'true', annualFamilyIncome, academicPercentage }
        : {}
    });
  }

  private apiError(error: HttpErrorResponse, fallback: string): string {
    const message = error.error?.message ?? error.error?.detail;
    return typeof message === 'string' && message.trim() ? message : fallback;
  }
}
