import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ScholarshipSchemeService } from '../../../core/services/scholarship-scheme.service';
import { CategoryOption, DocumentTypeOption, ProfessionOption, SchemeCatalogService } from '../../../core/services/scheme-catalog.service';
import { ScholarshipSchemeDetails } from '../../../core/models/scholarship-scheme.model';

@Component({
  selector: 'app-create-scholarship-scheme',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-scholarship-scheme.component.html',
  styleUrl: './create-scholarship-scheme.component.css'
})
export class CreateScholarshipSchemeComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  readonly steps = ['Scheme details', 'Eligibility', 'Benefits & budget'];
  readonly minDate = this.toLocalDate(new Date());
  step = 0;
  documents: DocumentTypeOption[] = [];
  professions: ProfessionOption[] = [];
  categories: CategoryOption[] = [];
  loadingOptions = true;
  loadError = '';
  submitting = false;
  submitError = '';
  scholarshipSchemeId = '';
  readonly schemeStatuses = ['ACTIVE', 'INACTIVE'];
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
    totalSchemeBudget: [null as number | null, [Validators.required, Validators.min(0.01)]],
    status: ['ACTIVE', Validators.required]
  });

  private readonly stepControls = [
    ['schemeName', 'scholarshipType', 'academicYear', 'startDate', 'endDate'],
    ['applierMinAge', 'applierMaxAge', 'maxFamilyAnnualIncome', 'minPercentageCriteria', 'requiredDocumentIds', 'eligibleProfessionIds', 'eligibleCategoriesIds'],
    ['scholarshipAmount', 'amountType', 'paymentFrequency', 'totalSchemeBudget', 'status']
  ];

  constructor(private catalog: SchemeCatalogService, private schemes: ScholarshipSchemeService, private router: Router) {}

  get isEditMode(): boolean { return !!this.scholarshipSchemeId; }

  private toLocalDate(date: Date): string {
    const month = `${date.getMonth() + 1}`.padStart(2, '0');
    const day = `${date.getDate()}`.padStart(2, '0');
    return `${date.getFullYear()}-${month}-${day}`;
  }

  get endDateMin(): string {
    const startDate = this.form.controls.startDate.value;
    if (!startDate) return this.minDate;
    const minimumEndDate = new Date(`${startDate}T00:00:00`);
    minimumEndDate.setDate(minimumEndDate.getDate() + 1);
    return this.isEditMode || this.toLocalDate(minimumEndDate) > this.minDate
      ? this.toLocalDate(minimumEndDate)
      : this.minDate;
  }

  ngOnInit(): void {
    this.scholarshipSchemeId = this.route.snapshot.paramMap.get('scholarshipSchemeId')?.trim() ?? '';
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
      next: options => {
        this.documents = options.documents;
        this.professions = options.professions;
        this.categories = options.categories;
        if (this.isEditMode) this.loadScholarshipForEdit();
        else this.loadingOptions = false;
      },
      error: () => { this.loadingOptions = false; this.loadError = 'Could not load document, profession and category options. Please refresh and try again.'; }
    });
  }

  private loadScholarshipForEdit(): void {
    this.schemes.getScholarshipSchemeById(this.scholarshipSchemeId).subscribe({
      next: scheme => {
        const requiredDocumentIds = this.documents
          .filter(option => scheme.requiredDocuments.includes(option.name))
          .map(option => option.documentTypeId);
        const eligibleProfessionIds = this.professions
          .filter(option => scheme.eligibleProfessions.includes(option.name))
          .map(option => option.id);
        const eligibleCategoriesIds = this.categories
          .filter(option => scheme.eligibleCategories.includes(option.code))
          .map(option => option.id);
        if (requiredDocumentIds.length !== scheme.requiredDocuments.length ||
            eligibleProfessionIds.length !== scheme.eligibleProfessions.length ||
            eligibleCategoriesIds.length !== scheme.eligibleCategories.length) {
          this.loadError = 'Some saved documents, professions, or categories are no longer available. Update options before editing this scheme.';
          this.loadingOptions = false;
          return;
        }

        this.form.patchValue({
          schemeName: scheme.scholarshipName,
          scholarshipType: scheme.scholarshipType,
          academicYear: scheme.academicYear,
          startDate: scheme.startDate,
          endDate: scheme.endDate,
          applierMinAge: scheme.applierMinAge,
          applierMaxAge: scheme.applierMaxAge,
          maxFamilyAnnualIncome: scheme.maxFamilyAnnualIncome,
          minPercentageCriteria: scheme.minPercentageCriteria,
          requiredDocumentIds,
          eligibleProfessionIds,
          eligibleCategoriesIds,
          scholarshipAmount: scheme.scholarshipAmount,
          amountType: scheme.amountType,
          paymentFrequency: scheme.paymentFrequency,
          totalSchemeBudget: scheme.totalSchemeBudget,
          status: scheme.status
        });
        this.loadingOptions = false;
      },
      error: error => {
        this.loadingOptions = false;
        this.loadError = error?.error?.message ?? 'Could not load this scholarship scheme for editing.';
      }
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
    if (this.step === 0 && !this.isEditMode && value.startDate && value.startDate < this.minDate) {
      this.form.get('startDate')?.setErrors({ pastDate: true }); valid = false;
    }
    if (this.step === 0 && value.startDate && value.endDate && value.endDate <= value.startDate) {
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
    const request = this.form.getRawValue() as unknown as Record<string, unknown>;
    if (this.isEditMode) {
      this.schemes.updateScholarshipScheme(this.scholarshipSchemeId, request).subscribe({
        next: () => { this.submitting = false; void this.router.navigate(['/scholarship-schemes', this.scholarshipSchemeId]); },
        error: error => { this.submitting = false; this.submitError = error?.error?.message || 'Unable to update the scholarship scheme. Check the details and try again.'; }
      });
      return;
    }
    delete request['status'];
    this.schemes.createScholarshipScheme(request).subscribe({
      next: () => { this.submitting = false; void this.router.navigate(['/scholarship-schemes-created-by-me']); },
      error: error => { this.submitting = false; this.submitError = error?.error?.message || 'Unable to create the scholarship scheme. Check the details and try again.'; }
    });
  }
}
