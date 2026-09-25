import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ScholarshipSchemeService } from '../../../core/services/scholarship-scheme.service';
import { CategoryOption, DocumentTypeOption, ProfessionOption, SchemeCatalogService } from '../../../core/services/scheme-catalog.service';

@Component({
  selector: 'app-create-scholarship-scheme',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-scholarship-scheme.component.html',
  styleUrl: './create-scholarship-scheme.component.css'
})
export class CreateScholarshipSchemeComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  readonly steps = ['Scheme details', 'Eligibility', 'Benefits & budget'];
  step = 0;
  documents: DocumentTypeOption[] = [];
  professions: ProfessionOption[] = [];
  categories: CategoryOption[] = [];
  loadingOptions = true;
  loadError = '';
  submitting = false;
  submitError = '';
  readonly scholarshipTypes = ['MERIT_BASED', 'CATEGORY_BASED'];
  readonly amountTypes = ['FIXED_AMOUNT', 'PERCENTAGE_OF_FEES'];
  readonly paymentFrequencies = ['ONE_TIME', 'MONTHLY', 'QUARTERLY', 'YEARLY'];

  readonly form = this.fb.group({
    schemeName: ['', [Validators.required, Validators.maxLength(200)]],
    scholarshipType: ['', Validators.required],
    academicYear: ['', [Validators.required, Validators.pattern(/^\d{4}-\d{4}$/)]],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    applierMinAge: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
    applierMaxAge: [100, [Validators.required, Validators.min(0), Validators.max(100)]],
    maxFamilyAnnualIncome: [0, [Validators.required, Validators.min(0)]],
    minPercentageCriteria: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
    requiredDocumentIds: [[] as string[], Validators.required],
    eligibleProfessionIds: [[] as string[], Validators.required],
    eligibleCategoriesIds: [[] as string[]],
    scholarshipAmount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    amountType: ['', Validators.required],
    paymentFrequency: ['', Validators.required],
    totalSchemeBudget: [null as number | null, [Validators.required, Validators.min(0.01)]]
  });

  private readonly stepControls = [
    ['schemeName', 'scholarshipType', 'academicYear', 'startDate', 'endDate'],
    ['applierMinAge', 'applierMaxAge', 'maxFamilyAnnualIncome', 'minPercentageCriteria', 'requiredDocumentIds', 'eligibleProfessionIds', 'eligibleCategoriesIds'],
    ['scholarshipAmount', 'amountType', 'paymentFrequency', 'totalSchemeBudget']
  ];

  constructor(private catalog: SchemeCatalogService, private schemes: ScholarshipSchemeService, private router: Router) {}

  ngOnInit(): void {
    this.form.controls.eligibleCategoriesIds.valueChanges.subscribe(() => {
      if (this.form.controls.eligibleCategoriesIds.value?.length) this.form.controls.eligibleCategoriesIds.setErrors(null);
    });
    this.form.controls.scholarshipType.valueChanges.subscribe(type => {
      if (type !== 'CATEGORY_BASED' && this.form.controls.eligibleCategoriesIds.value?.length) {
        this.form.controls.eligibleCategoriesIds.setValue([]);
      }
      if (type !== 'CATEGORY_BASED' || this.form.controls.eligibleCategoriesIds.value?.length) {
        this.form.controls.eligibleCategoriesIds.setErrors(null);
      }
    });
    forkJoin({ documents: this.catalog.getDocuments(), professions: this.catalog.getProfessions(), categories: this.catalog.getCategories() }).subscribe({
      next: options => { this.documents = options.documents; this.professions = options.professions; this.categories = options.categories; this.loadingOptions = false; },
      error: () => { this.loadingOptions = false; this.loadError = 'Could not load document, profession and category options. Please refresh and try again.'; }
    });
  }

  next(): void { if (this.validateStep() && this.step < this.steps.length - 1) this.step++; }
  back(): void { if (this.step > 0) this.step--; }

  isInvalid(name: string): boolean {
    const control = this.form.get(name);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  validateStep(): boolean {
    const names = this.stepControls[this.step];
    names.forEach(name => this.form.get(name)?.markAsTouched());
    const value = this.form.getRawValue();
    let valid = names.every(name => this.form.get(name)?.valid);
    if (this.step === 0 && value.startDate && value.endDate && value.endDate < value.startDate) {
      this.form.get('endDate')?.setErrors({ dateOrder: true }); valid = false;
    }
    if (this.step === 1 && value.applierMaxAge != null && value.applierMinAge != null && value.applierMaxAge < value.applierMinAge) {
      this.form.get('applierMaxAge')?.setErrors({ range: true }); valid = false;
    }
    if (this.step === 1 && value.scholarshipType === 'CATEGORY_BASED' && !value.eligibleCategoriesIds?.length) {
      this.form.get('eligibleCategoriesIds')?.markAsTouched();
      this.form.get('eligibleCategoriesIds')?.setErrors({ required: true }); valid = false;
    }
    if (this.step === 2 && value.amountType === 'PERCENTAGE_OF_FEES' && value.scholarshipAmount != null && value.scholarshipAmount > 100) {
      this.form.get('scholarshipAmount')?.setErrors({ percentage: true }); valid = false;
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
    this.schemes.createScholarshipScheme(this.form.getRawValue() as unknown as Record<string, unknown>).subscribe({
      next: () => { this.submitting = false; void this.router.navigate(['/scholarship-schemes-created-by-me']); },
      error: error => { this.submitting = false; this.submitError = error?.error?.message || 'Unable to create the scholarship scheme. Check the details and try again.'; }
    });
  }
}
