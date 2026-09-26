import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize, switchMap, tap } from 'rxjs';
import { CommonTableAction, CommonTableColumn, CommonTableComponent } from '../../../shared/common-table/common-table.component';
import { InstituteService } from '../../../core/services/institute.service';
import { InstituteAccountSummary } from '../../../core/models/summaryResponse.dto';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { UserRole } from '../../../core/enums/user-role.enum';
import { AddressService } from '../../../core/services/address.service';
import { AddressCityOption } from '../../../core/models/address-location.dto';
import { InstituteDetails } from '../../../core/models/institute-details.dto';

@Component({
  selector: 'app-institute-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CommonTableComponent],
  templateUrl: './institute-list.component.html',
  styleUrl: './institute-list.component.css',
})
export class InstituteListComponent implements OnInit {
  private readonly instituteService = inject(InstituteService);
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);
  private readonly fb = inject(FormBuilder);
  private readonly addressService = inject(AddressService);

  rows: InstituteAccountSummary[] = [];
  loading = true;
  creating = false;
  modalOpen = false;
  submitted = false;
  errorMessage = '';
  states: string[] = [];
  districts: string[] = [];
  cities: AddressCityOption[] = [];
  statesLoading = false;
  districtsLoading = false;
  citiesLoading = false;
  addressOptionsError = '';
  modalMode: 'create' | 'view' | 'edit' = 'create';
  modalLoading = false;
  modalError = '';
  selectedInstitute: InstituteDetails | null = null;
  updating = false;
  private initialEditSignature: string | null = null;
  readonly isAdmin = this.authService.getRole() === UserRole.ADMIN;
  readonly form = this.fb.nonNullable.group({
    firstName: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
    mobile: ['', [Validators.required, Validators.pattern(/^[6-9][0-9]{9}$/)]],
    instituteName: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(200)]],
    state: ['', Validators.required],
    district: ['', Validators.required],
    city: ['', Validators.required],
    addressId: ['', [Validators.required, Validators.pattern(/^[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i)]],
    location: ['', [Validators.maxLength(255)]],
    pincode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
    bankName: ['', [Validators.maxLength(150)]],
    branchName: ['', [Validators.maxLength(150)]],
    ifscCode: ['', [Validators.pattern(/^$|^[A-Z]{4}0[A-Z0-9]{6}$/)]],
    accountNumber: ['', [Validators.pattern(/^$|^[0-9]{9,18}$/)]],
    status: [true],
  });

  readonly columns: CommonTableColumn[] = [
    { key: 'instituteName', label: 'Institute' },
    { key: 'city', label: 'City' },
    { key: 'state', label: 'State' },
    {
      key: 'status', label: 'Status', type: 'badge',
      formatter: (value) => (value ? 'Active' : 'Inactive'),
      badgeMap: { true: 'vs-badge-verified', false: 'vs-badge-neutral' },
    },
  ];
  readonly rowActions: CommonTableAction[] = [
    { id: 'update', label: 'Update', variant: 'secondary' },
  ];

  ngOnInit(): void {
    this.loadInstitutes();
  }

  private loadStates(): void {
    this.statesLoading = true;
    this.addressOptionsError = '';
    this.addressService.getStates().pipe(finalize(() => this.statesLoading = false)).subscribe({
      next: (states) => { this.states = states; },
      error: () => { this.addressOptionsError = 'Could not load address locations. Please try again.'; },
    });
  }

  openModal(): void {
    if (!this.isAdmin) return;
    this.modalMode = 'create';
    this.selectedInstitute = null;
    this.initialEditSignature = null;
    this.modalError = '';
    this.loadStates();
    this.form.reset();
    this.submitted = false;
    this.districts = [];
    this.cities = [];
    this.addressOptionsError = '';
    this.modalOpen = true;
  }

  onInstituteRowClick(row: Record<string, unknown>): void {
    this.openInstituteDetails(String(row['instituteId']), Boolean(row['status']), 'view');
  }

  onInstituteAction(event: { actionId: string; row: Record<string, unknown> }): void {
    if (event.actionId === 'update') {
      this.openInstituteDetails(String(event.row['instituteId']), Boolean(event.row['status']), 'edit');
    }
  }

  editSelectedInstitute(): void {
    if (this.selectedInstitute) {
      this.openInstituteDetails(
        this.selectedInstitute.instituteId,
        Boolean(this.selectedInstitute.status),
        'edit',
      );
    }
  }

  openInstituteDetails(instituteId: string, status: boolean, mode: 'view' | 'edit'): void {
    if (!this.isAdmin) return;
    this.modalMode = mode;
    this.modalLoading = true;
    this.modalError = '';
    this.selectedInstitute = null;
    this.initialEditSignature = null;
    this.submitted = false;
    this.districts = [];
    this.cities = [];
    this.form.reset();
    this.modalOpen = true;

    this.instituteService.getInstituteById(instituteId).subscribe({
      next: (institute) => {
        this.selectedInstitute = { ...institute, status };
        if (mode === 'view') {
          this.modalLoading = false;
          return;
        }

        this.form.patchValue({
          firstName: institute.firstName,
          lastName: institute.lastName,
          email: institute.email,
          mobile: institute.mobile,
          instituteName: institute.instituteName,
          state: institute.address.state,
          addressId: institute.address.id,
          location: institute.location ?? '',
          pincode: String(institute.pincode),
          bankName: institute.bankName ?? '',
          branchName: institute.branchName ?? '',
          ifscCode: institute.ifscCode ?? '',
          accountNumber: institute.accountNumber ?? '',
          status,
        });
        this.addressService.getStates().pipe(
          tap((states) => this.states = states),
          switchMap(() => this.addressService.getDistricts(institute.address.state)),
          tap((districts) => this.districts = districts),
          switchMap(() => this.addressService.getCities(institute.address.state, institute.address.district)),
          finalize(() => this.modalLoading = false),
        ).subscribe({
          next: (cities) => {
            this.cities = cities;
            this.form.patchValue({
              district: institute.address.district,
              city: institute.address.city,
              addressId: institute.address.id,
            });
            this.initialEditSignature = this.editSignature();
          },
          error: () => {
            this.modalError = 'Could not load institute address options. Please try again.';
          },
        });
      },
      error: (error: HttpErrorResponse) => {
        this.modalLoading = false;
        this.modalError = this.apiError(error, 'Could not load institute details. Please try again.');
      },
    });
  }

  closeModal(): void {
    if (!this.creating && !this.updating) this.modalOpen = false;
  }

  onStateChange(): void {
    const state = this.form.controls.state.value;
    this.form.patchValue({ district: '', city: '', addressId: '' });
    this.districts = [];
    this.cities = [];
    this.addressOptionsError = '';
    if (!state) return;

    this.districtsLoading = true;
    this.addressService.getDistricts(state).pipe(finalize(() => this.districtsLoading = false)).subscribe({
      next: (districts) => {
        if (this.form.controls.state.value === state) this.districts = districts;
      },
      error: () => { this.addressOptionsError = 'Could not load districts for this state.'; },
    });
  }

  onDistrictChange(): void {
    const state = this.form.controls.state.value;
    const district = this.form.controls.district.value;
    this.form.patchValue({ city: '', addressId: '' });
    this.cities = [];
    this.addressOptionsError = '';
    if (!state || !district) return;

    this.citiesLoading = true;
    this.addressService.getCities(state, district).pipe(finalize(() => this.citiesLoading = false)).subscribe({
      next: (cities) => {
        if (this.form.controls.state.value === state && this.form.controls.district.value === district) {
          this.cities = cities;
        }
      },
      error: () => { this.addressOptionsError = 'Could not load cities for this district.'; },
    });
  }

  onCityChange(): void {
    const selectedCity = this.cities.find(city => city.city === this.form.controls.city.value);
    this.form.controls.addressId.setValue(selectedCity?.addressId ?? '');
  }

  loadInstitutes(): void {
    this.loading = true;
    this.instituteService.getInstitutes().pipe(finalize(() => this.loading = false)).subscribe({
      next: (rows) => { this.rows = rows; this.errorMessage = ''; },
      error: (err) => { this.errorMessage = err.error.message || 'Could not load institutes. Please try again.'; },
    });
  }

  fieldInvalid(name: keyof typeof this.form.controls): boolean {
    const field = this.form.controls[name];
    return field.invalid && (field.touched || this.submitted);
  }

  toggleStatus(): void {
    this.form.controls.status.setValue(!this.form.controls.status.value);
  }

  get hasChanges(): boolean {
    return this.initialEditSignature !== null && this.editSignature() !== this.initialEditSignature;
  }

  private editSignature(): string {
    const value = this.form.getRawValue();
    return JSON.stringify([
      value.firstName.trim(),
      value.lastName.trim(),
      value.email.trim().toLowerCase(),
      value.mobile.trim(),
      value.instituteName.trim(),
      value.addressId,
      value.location.trim(),
      value.pincode.trim(),
      value.bankName.trim(),
      value.branchName.trim(),
      value.ifscCode.trim().toUpperCase(),
      value.accountNumber.trim(),
      value.status,
    ]);
  }

  createInstitute(): void {
    this.submitted = true;
    if (!this.isAdmin || this.form.invalid || this.creating) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.creating = true;
    this.instituteService.createInstitute({
      firstName: value.firstName.trim(),
      lastName: value.lastName.trim(),
      email: value.email.trim(),
      mobile: value.mobile.trim(),
      instituteName: value.instituteName.trim(),
      addressId: value.addressId.trim(),
      location: value.location.trim() || null,
      pincode: Number(value.pincode),
      bankName: value.bankName.trim() || null,
      branchName: value.branchName.trim() || null,
      ifscCode: value.ifscCode.trim().toUpperCase() || null,
      accountNumber: value.accountNumber.trim() || null,
    }).pipe(finalize(() => this.creating = false)).subscribe({
      next: () => {
        this.toast.success('Institute created successfully.');
        this.modalOpen = false;
        this.loadInstitutes();
      },
      error: (error: HttpErrorResponse) => this.toast.error(this.apiError(error, 'Could not create institute. Please try again.')),
    });
  }

  updateInstitute(): void {
    this.submitted = true;
    if (!this.isAdmin || !this.selectedInstitute || !this.hasChanges || this.form.invalid || this.updating || this.creating) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.updating = true;
    this.instituteService.updateInstitute(this.selectedInstitute.instituteId, {
      firstName: value.firstName.trim(),
      lastName: value.lastName.trim(),
      email: value.email.trim(),
      mobile: value.mobile.trim(),
      instituteName: value.instituteName.trim(),
      addressId: value.addressId.trim(),
      location: value.location.trim() || null,
      pincode: Number(value.pincode),
      bankName: value.bankName.trim() || null,
      branchName: value.branchName.trim() || null,
      ifscCode: value.ifscCode.trim().toUpperCase() || null,
      accountNumber: value.accountNumber.trim() || null,
      status: value.status,
    }).pipe(finalize(() => this.updating = false)).subscribe({
      next: () => {
        this.toast.success('Institute updated successfully.');
        this.modalOpen = false;
        this.loadInstitutes();
      },
      error: (error: HttpErrorResponse) => this.toast.error(this.apiError(error, 'Could not update institute. Please try again.')),
    });
  }

  private apiError(error: HttpErrorResponse, fallback: string): string {
    const message = error.error?.message ?? error.error?.detail;
    return typeof message === 'string' && message.trim() ? message : fallback;
  }
}
