import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { LoanSchemeService } from '../../../core/services/loan-scheme.service';
import { SchemeCatalogService, DocumentTypeOption, ProfessionOption } from '../../../core/services/scheme-catalog.service';
import { LoanSchemeDetails } from '../../../core/models/loan-scheme-detailed.model';

@Component({
  selector: 'app-create-loan-scheme',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-loan-scheme.component.html',
  styleUrl: './create-loan-scheme.component.css'
})
export class CreateLoanSchemeComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  readonly steps = ['Scheme details', 'Eligibility & documents', 'Repayment'];
  readonly minDate = this.localDate(new Date());
  step = 0;
  documents: DocumentTypeOption[] = [];
  professions: ProfessionOption[] = [];
  loadingOptions = true;
  loadError = '';
  submitting = false;
  submitError = '';
  loanSchemeId = '';
  readonly schemeStatuses = ['ACTIVE', 'INACTIVE'];
  readonly interestTypes = ['FIXED', 'FLOATING'];
  readonly disbursementTypes = ['YEARLY', 'ONE_TIME'];

  readonly form = this.fb.group({
    schemeName: ['', [Validators.required, Validators.maxLength(200)]],
    effectiveFrom: ['', Validators.required],
    effectiveTo: ['', Validators.required],
    minLoanAmount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    maxLoanAmount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    interestType: ['', Validators.required],
    minInterestRate: [null as number | null, [Validators.required, Validators.min(0)]],
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
    foreclosureCharges: [null as number | null, [Validators.min(0)]],
    coursePeriodIncluded: [false, Validators.required],
    additionalMonths: [0, [Validators.required, Validators.min(0), Validators.max(120)]],
    status: ['ACTIVE', Validators.required]
  });

  private readonly stepControls = [
    ['schemeName', 'effectiveFrom', 'effectiveTo', 'minLoanAmount', 'maxLoanAmount', 'interestType', 'minInterestRate', 'maxInterestRate', 'disbursementType'],
    ['applierMinAge', 'applierMaxAge', 'coBorrowerRequired', 'minCreditScore', 'requiredDocumentIds', 'eligibleProfessionIds'],
    ['minTenureForRepayment', 'maxTenureForRepayment', 'prepaymentAllowed', 'foreclosureCharges', 'coursePeriodIncluded', 'additionalMonths', 'status']
  ];

  constructor(private catalog: SchemeCatalogService, private schemes: LoanSchemeService, private router: Router) {}

  get isEditMode(): boolean { return !!this.loanSchemeId; }

  private localDate(date: Date): string {
    const month = `${date.getMonth() + 1}`.padStart(2, '0');
    const day = `${date.getDate()}`.padStart(2, '0');
    return `${date.getFullYear()}-${month}-${day}`;
  }

  ngOnInit(): void {
    this.loanSchemeId = this.route.snapshot.paramMap.get('loanSchemeId')?.trim() ?? '';
    this.form.controls.coBorrowerRequired.valueChanges.subscribe(required => {
      this.setConditionalRequired('minCreditScore', required === true, [Validators.min(300), Validators.max(900)]);
    });
    this.form.controls.prepaymentAllowed.valueChanges.subscribe(allowed => {
      this.setConditionalRequired('foreclosureCharges', allowed === true, [Validators.min(0)]);
    });
    forkJoin({ documents: this.catalog.getDocuments(), professions: this.catalog.getProfessions() }).subscribe({
      next: options => {
        this.documents = options.documents;
        this.professions = options.professions;
        if (this.isEditMode) this.loadLoanSchemeForEdit();
        else this.loadingOptions = false;
      },
      error: () => { this.loadingOptions = false; this.loadError = 'Could not load document and profession options. Please refresh and try again.'; }
    });
  }

  private loadLoanSchemeForEdit(): void {
    this.schemes.getLoanSchemeById(this.loanSchemeId).subscribe({
      next: scheme => {
        const requiredDocumentIds = this.documents
          .filter(option => scheme.requiredDocumentIds.includes(option.name))
          .map(option => option.documentTypeId);
        const eligibleProfessionIds = this.professions
          .filter(option => scheme.eligibleProfessionIds.includes(option.name))
          .map(option => option.id);
        if (requiredDocumentIds.length !== scheme.requiredDocumentIds.length ||
            eligibleProfessionIds.length !== scheme.eligibleProfessionIds.length) {
          this.loadError = 'Some saved documents or professions are no longer available. Update options before editing this scheme.';
          this.loadingOptions = false;
          return;
        }

        this.form.patchValue({
          schemeName: scheme.schemeName,
          effectiveFrom: scheme.effectiveFrom,
          effectiveTo: scheme.effectiveTo,
          minLoanAmount: scheme.minAmount,
          maxLoanAmount: scheme.maxAmount,
          interestType: scheme.interestType,
          minInterestRate: scheme.minInterestRate ?? scheme.maxInterestRate,
          maxInterestRate: scheme.maxInterestRate,
          disbursementType: scheme.disbursementType,
          applierMinAge: scheme.applierMinAge,
          applierMaxAge: scheme.applierMaxAge,
          coBorrowerRequired: scheme.coBorrowerRequired,
          minCreditScore: scheme.minCreditScore,
          requiredDocumentIds,
          eligibleProfessionIds,
          minTenureForRepayment: scheme.minTenureForRepayment,
          maxTenureForRepayment: scheme.maxTenureForRepayment,
          prepaymentAllowed: scheme.prepaymentAllowed,
          foreclosureCharges: scheme.foreclosureCharges,
          coursePeriodIncluded: scheme.coursePeriodIncluded,
          additionalMonths: scheme.additionalMonths,
          status: scheme.status
        });
        this.loadingOptions = false;
      },
      error: error => {
        this.loadingOptions = false;
        this.loadError = error?.error?.message ?? 'Could not load this loan scheme for editing.';
      }
    });
  }

  private setConditionalRequired(
    name: 'minCreditScore' | 'foreclosureCharges',
    required: boolean,
    validators: ValidatorFn[]
  ): void {
    const control = this.form.get(name);
    control?.setValidators(required ? [Validators.required, ...validators] : validators);
    control?.updateValueAndValidity({ emitEvent: false });
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
    if (this.step === 0 && !this.isEditMode && value.effectiveFrom && value.effectiveFrom < this.minDate) {
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
    const request = this.form.getRawValue() as unknown as Record<string, unknown>;
    if (this.isEditMode) {
      this.schemes.updateLoanScheme(this.loanSchemeId, request).subscribe({
        next: () => { this.submitting = false; void this.router.navigate(['/loan-schemes', this.loanSchemeId]); },
        error: error => { this.submitting = false; this.submitError = error?.error?.message || 'Unable to update the loan scheme. Check the details and try again.'; }
      });
      return;
    }
    delete request['status'];
    this.schemes.createLoanScheme(request).subscribe({
      next: () => { this.submitting = false; void this.router.navigate(['/loan-schemes-created-by-me']); },
      error: error => { this.submitting = false; this.submitError = error?.error?.message || 'Unable to create the loan scheme. Check the details and try again.'; }
    });
  }
}
