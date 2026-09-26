import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AddressService } from '../../../core/services/address.service';
import { InstituteService } from '../../../core/services/institute.service';
import { CategoryOption, SchemeCatalogService } from '../../../core/services/scheme-catalog.service';
import { CourseOption, StudentService } from '../../../core/services/student.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { AddressCityOption } from '../../../core/models/address-location.dto';
import { InstituteAccountSummary } from '../../../core/models/summaryResponse.dto';
import { StudentDetailedResponse } from '../../../core/models/student-verification.dto/student-verification.dto';
import { Observable, forkJoin, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

const dateMustBeInPast: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  if (!control.value) return null;
  const [year, month, day] = String(control.value).split('-').map(Number);
  if (!year || !month || !day) return { invalidDate: true };
  const selectedDate = new Date(year, month - 1, day);
  if (selectedDate.getFullYear() !== year || selectedDate.getMonth() !== month - 1 || selectedDate.getDate() !== day) {
    return { invalidDate: true };
  }
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return selectedDate < today ? null : { dateMustBeInPast: true };
};

const requiredFields = [
  'instituteId', 'courseId', 'categoryId', 'state', 'district', 'addressId', 'pincode',
  'aadharNumber', 'gender', 'dateOfBirth', 'fatherName', 'motherName', 'annualFamilyIncome'
];

@Component({
  selector: 'app-complete-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './complete-profile.component.html',
  styleUrls: ['./complete-profile.component.css']
})
export class CompleteProfileComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly addresses = inject(AddressService);
  private readonly institutesApi = inject(InstituteService);
  private readonly catalog = inject(SchemeCatalogService);
  private readonly students = inject(StudentService);
  private readonly auth = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);

  institutes: InstituteAccountSummary[] = [];
  courses: CourseOption[] = [];
  categories: CategoryOption[] = [];
  states: string[] = [];
  districts: string[] = [];
  cities: AddressCityOption[] = [];
  loading = true;
  loadingOptions = 0;
  submitting = false;
  submitted = false;
  private initialEditFormValue: string | null = null;
  readonly editMode: boolean;
  readonly lastValidDateOfBirth = this.getYesterdayDate();

  form = this.fb.nonNullable.group({
    instituteId: ['', Validators.required],
    courseId: [{ value: '', disabled: true }, Validators.required],
    categoryId: ['', Validators.required],
    state: ['', Validators.required],
    district: [{ value: '', disabled: true }, Validators.required],
    addressId: [{ value: '', disabled: true }, Validators.required],
    location: ['', Validators.maxLength(255)],
    pincode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
    aadharNumber: ['', [Validators.required, Validators.pattern(/^\d{12}$/)]],
    gender: ['', Validators.required],
    dateOfBirth: ['', [Validators.required, dateMustBeInPast]],
    fatherName: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(150)]],
    motherName: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(150)]],
    annualFamilyIncome: ['', [Validators.required, Validators.min(0), Validators.max(9999999999999.99), Validators.pattern(/^\d{1,13}(\.\d{1,2})?$/)]]
  });

  constructor() {
    this.editMode = this.route.snapshot.data['editProfile'] === true;
    if (this.editMode) {
      this.form.controls.aadharNumber.setValidators([Validators.pattern(/^\d{12}$/)]);
      this.form.controls.aadharNumber.updateValueAndValidity();
    }
  }

  ngOnInit(): void {
    forkJoin({
      institutes: this.institutesApi.getInstitutes().pipe(catchError(err => {
        this.toast.error(err?.error?.message ?? 'Could not load institutes.');
        return of([] as InstituteAccountSummary[]);
      })),
      categories: this.catalog.getCategories().pipe(catchError(err => {
        this.toast.error(err?.error?.message ?? 'Could not load categories.');
        return of([] as CategoryOption[]);
      })),
      states: this.addresses.getStates().pipe(catchError(err => {
        this.toast.error(err?.error?.message ?? 'Could not load locations.');
        return of([] as string[]);
      })),
      profile: this.editMode
        ? this.students.getMyProfile().pipe(catchError(err => {
          this.toast.error(err?.error?.message ?? 'Could not load your profile.');
          return of(null);
        }))
        : of(null)
    }).subscribe(({ institutes, categories, states, profile }) => {
      this.institutes = institutes.filter(row => row.status || row.instituteId === profile?.instituteId);
      this.categories = categories;
      this.states = states;
      this.loading = false;
      if (profile) this.populateProfile(profile);
    });
  }

  private populateProfile(profile: StudentDetailedResponse): void {
    this.form.patchValue({
      instituteId: profile.instituteId,
      courseId: profile.courseId,
      categoryId: profile.categoryId,
      state: profile.address?.state ?? '',
      district: profile.address?.district ?? '',
      addressId: profile.address?.id ?? '',
      location: profile.location ?? '',
      pincode: String(profile.pincode ?? ''),
      gender: profile.gender ?? '',
      dateOfBirth: profile.dateOfBirth ?? '',
      fatherName: profile.fatherName ?? '',
      motherName: profile.motherName ?? '',
      annualFamilyIncome: String(profile.annualFamilyIncome ?? '')
    });
    this.initialEditFormValue = this.getComparableFormValue();
    this.onInstituteChange(profile.instituteId, profile.courseId);
    this.onStateChange(profile.address?.state ?? '', profile.address?.district ?? '', profile.address?.id ?? '');
  }

  private get activeRequiredFields(): string[] {
    return this.editMode ? requiredFields.filter(field => field !== 'aadharNumber') : requiredFields;
  }

  isInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    const missingRequiredValue = this.activeRequiredFields.includes(controlName) && this.isEmptyValue(control?.value);
    return !!control && (control.invalid || missingRequiredValue) && (control.touched || this.submitted);
  }

  fieldError(controlName: string): string {
    const control = this.form.get(controlName);
    const errors = control?.errors;
    const labels: Record<string, string> = {
      instituteId: 'Institute', courseId: 'Course', categoryId: 'Category', state: 'State',
      district: 'District', addressId: 'City', gender: 'Gender', dateOfBirth: 'Date of birth',
      aadharNumber: 'Aadhaar number', fatherName: "Father's name", motherName: "Mother's name",
      pincode: 'PIN code', annualFamilyIncome: 'Annual family income', location: 'Area / street'
    };
    const label = labels[controlName] ?? 'This field';
    if (this.activeRequiredFields.includes(controlName) && this.isEmptyValue(control?.value)) return `${label} is required.`;
    if (!errors) return '';
    if (errors['required']) return `${label} is required.`;
    if (errors['dateMustBeInPast']) return 'Date of birth must be before today.';
    if (errors['invalidDate']) return 'Enter a valid date of birth.';
    if (errors['maxlength']) return `${label} cannot exceed ${errors['maxlength'].requiredLength} characters.`;
    if (errors['min']) return `${label} cannot be negative.`;
    if (errors['max']) return `${label} cannot exceed 9,999,999,999,999.99.`;
    if (errors['pattern']) {
      if (controlName === 'pincode') return 'Enter a 6-digit PIN code.';
      if (controlName === 'aadharNumber') return 'Enter a 12-digit Aadhaar number.';
      if (controlName === 'fatherName' || controlName === 'motherName') return `${label} cannot contain only spaces.`;
      if (controlName === 'annualFamilyIncome') return 'Use up to 13 whole digits and 2 decimal places.';
    }
    return `${label} is invalid.`;
  }

  private getYesterdayDate(): string {
    const date = new Date();
    date.setDate(date.getDate() - 1);
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${date.getFullYear()}-${month}-${day}`;
  }

  private isEmptyValue(value: unknown): boolean {
    return value === null || value === undefined || value === '';
  }

  private trackOptions<T>(request: Observable<T>, onSuccess: (rows: T) => void, failureMessage: string, onFailure: () => void): void {
    this.loadingOptions++;
    request.pipe(finalize(() => this.loadingOptions--)).subscribe({
      next: onSuccess,
      error: err => {
        onFailure();
        this.toast.error(err?.error?.message ?? failureMessage);
      }
    });
  }

  get hasMissingRequiredFields(): boolean {
    return this.activeRequiredFields.some(field => this.isEmptyValue(this.form.get(field)?.value));
  }

  get hasChanges(): boolean {
    if (!this.editMode) return true;
    if (this.initialEditFormValue === null) return false;
    return this.getComparableFormValue() !== this.initialEditFormValue;
  }

  private getComparableFormValue(): string {
    const value = this.form.getRawValue();
    return JSON.stringify({
      ...value,
      location: value.location.trim(),
      pincode: value.pincode.trim(),
      aadharNumber: value.aadharNumber.trim(),
      fatherName: value.fatherName.trim(),
      motherName: value.motherName.trim(),
      annualFamilyIncome: value.annualFamilyIncome.trim()
    });
  }

  onInstituteChange(instituteId: string, selectedCourseId = ''): void {
    if (selectedCourseId) this.form.controls.courseId.setValue(selectedCourseId);
    else this.form.controls.courseId.reset('');
    this.form.controls.courseId.disable();
    this.courses = [];
    if (!instituteId) return;
    this.trackOptions(this.students.getCoursesByInstitute(instituteId), rows => {
      this.courses = rows;
      this.form.controls.courseId.enable();
      if (selectedCourseId && rows.some(course => course.id === selectedCourseId)) {
        this.form.controls.courseId.setValue(selectedCourseId);
      }
    }, 'Could not load courses.', () => this.form.controls.courseId.enable());
  }

  onStateChange(state: string, selectedDistrict = '', selectedCityId = ''): void {
    if (selectedDistrict) this.form.controls.district.setValue(selectedDistrict);
    else this.form.controls.district.reset('');
    this.form.controls.district.disable();
    if (selectedCityId) this.form.controls.addressId.setValue(selectedCityId);
    else this.form.controls.addressId.reset('');
    this.form.controls.addressId.disable();
    this.districts = [];
    this.cities = [];
    if (!state) return;
    this.trackOptions(this.addresses.getDistricts(state), rows => {
      this.districts = rows;
      this.form.controls.district.enable();
      if (selectedDistrict && rows.includes(selectedDistrict)) {
        this.form.controls.district.setValue(selectedDistrict);
        this.onDistrictChange(selectedDistrict, selectedCityId);
      }
    }, 'Could not load districts.', () => this.form.controls.district.enable());
  }

  onDistrictChange(district: string, selectedCityId = ''): void {
    if (selectedCityId) this.form.controls.addressId.setValue(selectedCityId);
    else this.form.controls.addressId.reset('');
    this.form.controls.addressId.disable();
    this.cities = [];
    const state = this.form.controls.state.value;
    if (!state || !district) return;
    this.trackOptions(this.addresses.getCities(state, district), rows => {
      this.cities = rows;
      this.form.controls.addressId.enable();
      if (selectedCityId && rows.some(city => city.addressId === selectedCityId)) {
        this.form.controls.addressId.setValue(selectedCityId);
      }
    }, 'Could not load cities.', () => this.form.controls.addressId.enable());
  }

  submit(): void {
    this.submitted = true;
    this.form.markAllAsTouched();
    if (this.form.invalid || this.hasMissingRequiredFields || this.loadingOptions > 0 || this.submitting) return;
    const value = this.form.getRawValue();
    this.submitting = true;
    const request = {
      instituteId: value.instituteId,
      courseId: value.courseId,
      categoryId: value.categoryId,
      addressId: value.addressId,
      location: value.location.trim() || null,
      pincode: Number(value.pincode),
      aadharNumber: value.aadharNumber.trim() || null,
      gender: value.gender,
      dateOfBirth: value.dateOfBirth,
      fatherName: value.fatherName.trim(),
      motherName: value.motherName.trim(),
      annualFamilyIncome: Number(value.annualFamilyIncome)
    };
    const saveRequest = this.editMode
      ? this.students.updateMyProfile(request)
      : this.students.completeProfile({ ...request, aadharNumber: value.aadharNumber });
    saveRequest.subscribe({
      next: () => {
        this.submitting = false;
        if (this.editMode) {
          this.toast.success('Profile updated successfully.');
          this.router.navigateByUrl('/my-profile');
        } else {
          this.auth.setStudentProfileCompleted(true);
          this.toast.success('Profile completed successfully.');
          this.router.navigateByUrl(this.auth.getDefaultRoute());
        }
      },
      error: err => {
        this.submitting = false;
        this.toast.error(err?.error?.message ?? (this.editMode ? 'Could not update your profile.' : 'Could not complete your profile.'));
      }
    });
  }
}
