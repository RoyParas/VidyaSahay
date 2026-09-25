import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { LoanSchemeService } from '../../../core/services/loan-scheme.service';
import { SchemeCatalogService, DocumentTypeOption, ProfessionOption } from '../../../core/services/scheme-catalog.service';

@Component({
  selector: 'app-create-loan-scheme',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-loan-scheme.component.html',
  styleUrl: './create-loan-scheme.component.css'
})
export class CreateLoanSchemeComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  readonly steps = ['Scheme details', 'Eligibility & documents', 'Repayment'];
  readonly minDate = this.localDate(new Date());
  step = 0;
  documents: DocumentTypeOption[] = [];
  professions: ProfessionOption[] = [];
  loadingOptions = true;
  loadError = '';
  submitting = false;
  submitError = '';
  readonly interestTypes = ['FIXED', 'FLOATING'];
  readonly disbursementTypes = ['YEARLY', 'ONE_TIME'];

  readonly form = this.fb.group({
    schemeName: ['', [Validators.required, Validators.maxLength(200)]],
    effectiveFrom: ['', Validators.required],
    effectiveTo: ['', Validators.required],
    minLoanAmount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    maxLoanAmount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    interestType: ['', Validators.required],
    minInterestRate: [null as number | null, [Validators.min(0)]],
    maxInterestRate: [null as number | null, [Validators.required, Validators.min(0)]],
    disbursementType: ['', Validators.required],
    applierMinAge: [18, [Validators.required, Validators.min(18), Validators.max(100)]],
    applierMaxAge: [100, [Validators.required, Validators.min(18), Validators.max(100)]],
    coBorrowerRequired: [false, Validators.required],
    minCreditScore: [null as number | null, [Validators.min(300), Validators.max(900)]],
    requiredDocumentIds: [[] as string[], Validators.required],
    eligibleProfessionIds: [[] as string[], Validators.required],
    minTenureForRepayment: [1, [Validators.required, Validators.min(1)]],
    maxTenureForRepayment: [1, [Validators.required, Validators.min(1)]],
    prepaymentAllowed: [false, Validators.required],
    foreclosureCharges: [0 as number | null, [Validators.min(0)]],
    coursePeriodIncluded: [false, Validators.required],
    additionalMonths: [0, [Validators.required, Validators.min(0), Validators.max(120)]]
  });

  private readonly stepControls = [
    ['schemeName', 'effectiveFrom', 'effectiveTo', 'minLoanAmount', 'maxLoanAmount', 'interestType', 'minInterestRate', 'maxInterestRate', 'disbursementType'],
    ['applierMinAge', 'applierMaxAge', 'coBorrowerRequired', 'minCreditScore', 'requiredDocumentIds', 'eligibleProfessionIds'],
    ['minTenureForRepayment', 'maxTenureForRepayment', 'prepaymentAllowed', 'foreclosureCharges', 'coursePeriodIncluded', 'additionalMonths']
  ];

  constructor(private catalog: SchemeCatalogService, private schemes: LoanSchemeService, private router: Router) {}

  private localDate(date: Date): string {
    const month = `${date.getMonth() + 1}`.padStart(2, '0');
    const day = `${date.getDate()}`.padStart(2, '0');
    return `${date.getFullYear()}-${month}-${day}`;
  }

  ngOnInit(): void {
    forkJoin({ documents: this.catalog.getDocuments(), professions: this.catalog.getProfessions() }).subscribe({
      next: options => { this.documents = options.documents; this.professions = options.professions; this.loadingOptions = false; },
      error: () => { this.loadingOptions = false; this.loadError = 'Could not load document and profession options. Please refresh and try again.'; }
    });
  }

  next(): void {
    if (!this.validateStep()) return;
    if (this.step < this.steps.length - 1) this.step++;
  }

  back(): void { if (this.step > 0) this.step--; }

  isInvalid(name: string): boolean {
    const control = this.form.get(name);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  validateStep(): boolean {
    const names = this.stepControls[this.step];
    names.forEach(name => this.form.get(name)?.markAsTouched());
    let valid = names.every(name => this.form.get(name)?.valid);
    const value = this.form.getRawValue();
    if (this.step === 0 && value.effectiveFrom && value.effectiveTo && value.effectiveTo < value.effectiveFrom) {
      this.form.get('effectiveTo')?.setErrors({ dateOrder: true }); valid = false;
    }
    if (this.step === 0 && value.effectiveFrom && value.effectiveFrom < this.minDate) {
      this.form.get('effectiveFrom')?.setErrors({ pastDate: true }); valid = false;
    }
    if (this.step === 0 && value.effectiveTo && value.effectiveTo < this.minDate) {
      this.form.get('effectiveTo')?.setErrors({ pastDate: true }); valid = false;
    }
    if (this.step === 0 && value.minLoanAmount != null && value.maxLoanAmount != null && value.maxLoanAmount < value.minLoanAmount) {
      this.form.get('maxLoanAmount')?.setErrors({ range: true }); valid = false;
    }
    if (this.step === 0 && value.minInterestRate != null && value.maxInterestRate != null && value.maxInterestRate < value.minInterestRate) {
      this.form.get('maxInterestRate')?.setErrors({ range: true }); valid = false;
    }
    if (this.step === 1 && value.applierMaxAge != null && value.applierMinAge != null && value.applierMaxAge < value.applierMinAge) {
      this.form.get('applierMaxAge')?.setErrors({ range: true }); valid = false;
    }
    if (this.step === 2 && value.maxTenureForRepayment != null && value.minTenureForRepayment != null && value.maxTenureForRepayment < value.minTenureForRepayment) {
      this.form.get('maxTenureForRepayment')?.setErrors({ range: true }); valid = false;
    }
    return valid;
  }

  submit(): void {
    const lastStep = this.step;
    for (let index = 0; index < this.steps.length; index++) {
      this.step = index;
      if (!this.validateStep()) return;
    }
    this.step = lastStep;
    this.submitting = true; this.submitError = '';
    this.schemes.createLoanScheme(this.form.getRawValue() as unknown as Record<string, unknown>).subscribe({
      next: () => { this.submitting = false; void this.router.navigate(['/loan-schemes-created-by-me']); },
      error: error => { this.submitting = false; this.submitError = error?.error?.message || 'Unable to create the loan scheme. Check the details and try again.'; }
    });
  }
}
