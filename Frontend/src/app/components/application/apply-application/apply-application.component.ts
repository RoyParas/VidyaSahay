import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { ApplicationService } from '../../../core/services/application.service';
import { ApplicationByIdService } from '../../../core/services/application-by-id.service';
import { ApplicationDetailsResponse } from '../../../core/models/application-by-id.models';
import { LoanSchemeService } from '../../../core/services/loan-scheme.service';
import { ScholarshipSchemeService } from '../../../core/services/scholarship-scheme.service';
import { StudentService } from '../../../core/services/student.service';
import { StudentDetailedResponse } from '../../../core/models/student-verification.dto/student-verification.dto';
import { LoanSchemeDetails, SchemeDocumentRequirement } from '../../../core/models/loan-scheme-detailed.model';
import { ScholarshipSchemeDetails } from '../../../core/models/scholarship-scheme.model';
import { ToastService } from '../../../core/services/toast.service';

type ApplicationType = 'LOAN' | 'SCHOLARSHIP';
type SchemeDetails = LoanSchemeDetails | ScholarshipSchemeDetails;

@Component({
  selector: 'app-apply-application',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './apply-application.component.html',
  styleUrl: './apply-application.component.css'
})
export class ApplyApplicationComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly students = inject(StudentService);
  private readonly loans = inject(LoanSchemeService);
  private readonly scholarships = inject(ScholarshipSchemeService);
  private readonly applications = inject(ApplicationService);
  private readonly applicationDetails = inject(ApplicationByIdService);
  private readonly toast = inject(ToastService);

  readonly maxFileSize = 5 * 1024 * 1024;
  readonly steps = ['Profile', 'Application details', 'Documents', 'Review', 'Submit'];
  applicationType: ApplicationType | null = null;
  schemeId = '';
  resubmissionId: string | null = null;
  scheme: SchemeDetails | null = null;
  profile: StudentDetailedResponse | null = null;
  documentRequirements: SchemeDocumentRequirement[] = [];
  selectedFiles: Record<string, File | null> = {};
  step = 0;
  loading = true;
  submitting = false;
  errorMessage = '';

  readonly form = this.fb.nonNullable.group({
    requestedLoanAmount: [''],
    academicPercentage: [''],
    loanPurpose: [''],
    repaymentTenureYears: [''],
    coBorrowerName: [''],
    coBorrowerIncome: ['']
  });

  get title(): string {
    return this.applicationType === 'LOAN' ? 'Loan application' : 'Scholarship application';
  }

  get loanScheme(): LoanSchemeDetails | null {
    return this.applicationType === 'LOAN' ? this.scheme as LoanSchemeDetails | null : null;
  }

  get scholarshipScheme(): ScholarshipSchemeDetails | null {
    return this.applicationType === 'SCHOLARSHIP' ? this.scheme as ScholarshipSchemeDetails | null : null;
  }

  ngOnInit(): void {
    const type = this.route.snapshot.paramMap.get('applicationType')?.toUpperCase();
    this.schemeId = this.route.snapshot.paramMap.get('schemeId') ?? '';
    this.resubmissionId = this.route.snapshot.queryParamMap.get('resubmit');
    if ((type !== 'LOAN' && type !== 'SCHOLARSHIP') || !this.schemeId) {
      this.loading = false;
      this.errorMessage = 'The application scheme could not be identified.';
      return;
    }
    this.applicationType = type;
    const profileRequest = this.students.getMyProfile();
    const schemeRequest = type === 'LOAN'
      ? this.loans.getLoanSchemeById(this.schemeId)
      : this.scholarships.getScholarshipSchemeById(this.schemeId);
    const existingApplicationRequest = this.resubmissionId
      ? this.applicationDetails.getApplicationById(this.resubmissionId)
      : of(null);
    forkJoin({ profile: profileRequest, scheme: schemeRequest, existingApplication: existingApplicationRequest }).subscribe({
      next: ({ profile, scheme, existingApplication }) => {
        this.profile = profile;
        this.scheme = scheme;
        if (profile.verificationStatus !== 'VERIFIED') {
          this.errorMessage = 'Your institute must verify your profile before you can apply.';
          this.loading = false;
          return;
        }
        this.documentRequirements = scheme.documentRequirements ?? [];
        this.documentRequirements.forEach(document => this.selectedFiles[document.documentTypeId] = null);
        this.configureForm();
        if (this.resubmissionId) {
          if (!existingApplication || !this.isRevertedApplication(existingApplication)) {
            this.errorMessage = 'Only a reverted application can be resubmitted.';
            this.loading = false;
            return;
          }
          this.populateFromApplication(existingApplication);
        }
        this.validateEligibility();
      },
      error: err => {
        this.errorMessage = err?.error?.message ?? 'Could not load the profile or scheme details.';
        this.loading = false;
      }
    });
  }

  private isRevertedApplication(application: ApplicationDetailsResponse): boolean {
    return application.applicationSummary.status === 'REVERTED'
      && application.applicationSummary.schemeId === this.schemeId
      && application.applicationSummary.applicationType === this.applicationType;
  }

  private populateFromApplication(application: ApplicationDetailsResponse): void {
    const details = application.submissionDetails;
    if (this.applicationType === 'LOAN') {
      this.form.patchValue({
        requestedLoanAmount: details.requestedLoanAmount === null ? '' : String(details.requestedLoanAmount),
        loanPurpose: details.loanPurpose ?? '',
        repaymentTenureYears: details.repaymentTenureYears === null ? '' : String(details.repaymentTenureYears),
        coBorrowerName: details.coBorrowerName ?? '',
        coBorrowerIncome: details.coBorrowerIncome === null ? '' : String(details.coBorrowerIncome)
      });
    } else {
      this.form.controls.academicPercentage.setValue(
        details.academicPercentage === null ? '' : String(details.academicPercentage));
    }
  }

  private configureForm(): void {
    if (this.loanScheme) {
      const scheme = this.loanScheme;
      const amount = this.route.snapshot.queryParamMap.get('requestedLoanAmount') ?? '';
      this.form.controls.requestedLoanAmount.setValue(amount);
      this.form.controls.requestedLoanAmount.setValidators([
        Validators.required, Validators.min(Number(scheme.minAmount)), Validators.max(Number(scheme.maxAmount))
      ]);
      this.form.controls.loanPurpose.setValidators([Validators.required, Validators.maxLength(500)]);
      this.form.controls.repaymentTenureYears.setValidators([
        Validators.required, Validators.min(scheme.minTenureForRepayment), Validators.max(scheme.maxTenureForRepayment)
      ]);
      if (scheme.coBorrowerRequired) {
        this.form.controls.coBorrowerName.setValidators([Validators.required, Validators.maxLength(150)]);
        this.form.controls.coBorrowerIncome.setValidators([Validators.required, Validators.min(0)]);
      }
    }
    if (this.scholarshipScheme) {
      this.form.controls.academicPercentage.setValue(
        this.route.snapshot.queryParamMap.get('academicPercentage') ?? ''
      );
      this.form.controls.academicPercentage.setValidators([
        Validators.required,
        Validators.min(this.scholarshipScheme.minPercentageCriteria),
        Validators.max(100)
      ]);
    }
    this.form.updateValueAndValidity();
  }

  private validateEligibility(): void {
    if (!this.profile || !this.scheme || !this.applicationType) return;
    if (this.applicationType === 'LOAN') {
      this.loans.getEligibleLoanSchemes(Number(this.form.controls.requestedLoanAmount.value)).subscribe({
        next: rows => this.finishEligibilityCheck(rows.some(row => row.loanSchemeId === this.schemeId)),
        error: () => this.finishEligibilityCheck(false)
      });
      return;
    }
    this.scholarships.getEligibleScholarshipSchemes(
      this.profile.annualFamilyIncome,
      Number(this.form.controls.academicPercentage.value)
    ).subscribe({
      next: rows => this.finishEligibilityCheck(rows.some(row => row.scholarshipSchemeId === this.schemeId)),
      error: () => this.finishEligibilityCheck(false)
    });
  }

  private finishEligibilityCheck(eligible: boolean): void {
    if (!eligible) this.errorMessage = 'You are not eligible for this scheme with the current profile and application details.';
    this.loading = false;
  }

  get controlLabels(): Record<string, string> {
    return {
      requestedLoanAmount: 'Requested loan amount',
      academicPercentage: 'Academic percentage',
      loanPurpose: 'Loan purpose',
      repaymentTenureYears: 'Repayment tenure',
      coBorrowerName: "Co-borrower's name",
      coBorrowerIncome: "Co-borrower's annual income"
    };
  }

  next(): void {
    if (this.step === 1) {
      this.form.markAllAsTouched();
      if (this.form.invalid) return;
    }
    if (this.step === 2 && !this.documentsValid()) return;
    if (this.step < this.steps.length - 1) this.step++;
    else this.submit();
  }

  previous(): void {
    if (this.step > 0 && !this.submitting) this.step--;
  }

  isInvalid(name: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[name];
    return control.invalid && (control.touched || control.dirty);
  }

  onFileSelected(documentTypeId: string, event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    if (!file) return;
    if (file.size > this.maxFileSize) {
      this.toast.error('Each document must be 5 MB or smaller.');
      input.value = '';
      this.selectedFiles[documentTypeId] = null;
      return;
    }
    const extension = file.name.split('.').pop()?.toLowerCase() ?? '';
    const allowed: Record<string, string[]> = {
      'application/pdf': ['pdf'],
      'image/jpeg': ['jpg', 'jpeg'],
      'image/png': ['png']
    };
    if (!allowed[file.type]?.includes(extension)) {
      this.toast.error('Upload a PDF, JPG, JPEG, or PNG file.');
      input.value = '';
      this.selectedFiles[documentTypeId] = null;
      return;
    }
    this.selectedFiles[documentTypeId] = file;
  }

  private documentsValid(): boolean {
    const missing = this.documentRequirements.some(document => !this.selectedFiles[document.documentTypeId]);
    if (missing) this.toast.error('Upload every document required by this scheme.');
    return !missing;
  }

  get canSubmit(): boolean {
    return !this.submitting && this.documentsValidSilently();
  }

  private documentsValidSilently(): boolean {
    return this.documentRequirements.every(document => !!this.selectedFiles[document.documentTypeId]);
  }

  submit(): void {
    if (!this.applicationType || !this.profile || !this.canSubmit) return;
    this.submitting = true;
    const value = this.form.getRawValue();
    const request = {
      applicationType: this.applicationType,
      schemeId: this.schemeId,
      requestedLoanAmount: this.applicationType === 'LOAN' ? Number(value.requestedLoanAmount) : undefined,
      academicPercentage: this.applicationType === 'SCHOLARSHIP' ? Number(value.academicPercentage) : undefined,
      loanPurpose: this.applicationType === 'LOAN' ? value.loanPurpose.trim() : undefined,
      repaymentTenureYears: this.applicationType === 'LOAN' ? Number(value.repaymentTenureYears) : undefined,
      coBorrowerName: this.applicationType === 'LOAN' ? value.coBorrowerName.trim() : undefined,
      coBorrowerIncome: this.applicationType === 'LOAN' && value.coBorrowerIncome !== ''
        ? Number(value.coBorrowerIncome) : undefined,
      documents: this.documentRequirements.map(document => ({
        documentTypeId: document.documentTypeId,
        file: this.selectedFiles[document.documentTypeId]!
      }))
    };
    const submission = this.resubmissionId
      ? this.applications.resubmit(this.resubmissionId, request)
      : this.applications.apply(request);
    submission.subscribe({
      next: () => {
        this.submitting = false;
        this.toast.success(this.resubmissionId ? 'Application resubmitted successfully.' : 'Application submitted successfully.');
        this.router.navigateByUrl('/my-applications');
      },
      error: err => {
        this.submitting = false;
        this.toast.error(err?.error?.message ?? 'Could not submit the application.');
      }
    });
  }
}
