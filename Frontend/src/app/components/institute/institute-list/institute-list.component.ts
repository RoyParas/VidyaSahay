import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import { CommonTableColumn, CommonTableComponent } from '../../../shared/common-table/common-table.component';
import { InstituteService } from '../../../core/services/institute.service';
import { InstituteAccountSummary } from '../../../core/models/summaryResponse.dto';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { UserRole } from '../../../core/enums/user-role.enum';

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

  rows: InstituteAccountSummary[] = [];
  loading = true;
  creating = false;
  modalOpen = false;
  submitted = false;
  errorMessage = '';
  readonly isAdmin = this.authService.getRole() === UserRole.ADMIN;
  readonly form = this.fb.nonNullable.group({
    firstName: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
    mobile: ['', [Validators.required, Validators.pattern(/^[6-9][0-9]{9}$/)]],
    instituteName: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(200)]],
    addressId: ['', [Validators.required, Validators.pattern(/^[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i)]],
    location: ['', [Validators.maxLength(255)]],
    pincode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
    bankName: ['', [Validators.maxLength(150)]],
    branchName: ['', [Validators.maxLength(150)]],
    ifscCode: ['', [Validators.pattern(/^$|^[A-Z]{4}0[A-Z0-9]{6}$/)]],
    accountNumber: ['', [Validators.pattern(/^$|^[0-9]{9,18}$/)]],
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

  ngOnInit(): void { this.loadInstitutes(); }

  loadInstitutes(): void {
    this.loading = true;
    this.instituteService.getInstitutes().pipe(finalize(() => this.loading = false)).subscribe({
      next: (rows) => { this.rows = rows; this.errorMessage = ''; },
      error: () => { this.errorMessage = 'Could not load institutes. Please try again.'; },
    });
  }

  openModal(): void {
    if (!this.isAdmin) return;
    this.form.reset();
    this.submitted = false;
    this.modalOpen = true;
  }

  closeModal(): void {
    if (!this.creating) this.modalOpen = false;
  }

  fieldInvalid(name: keyof typeof this.form.controls): boolean {
    const field = this.form.controls[name];
    return field.invalid && (field.touched || this.submitted);
  }

  submit(): void {
    this.submitted = true;
    if (!this.isAdmin || this.form.invalid || this.creating) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.creating = true;
    this.instituteService.createInstitute({
      ...value,
      email: value.email.trim(),
      firstName: value.firstName.trim(),
      lastName: value.lastName.trim(),
      instituteName: value.instituteName.trim(),
      addressId: value.addressId.trim(),
      mobile: value.mobile.trim(),
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

  private apiError(error: HttpErrorResponse, fallback: string): string {
    const message = error.error?.message ?? error.error?.detail;
    return typeof message === 'string' && message.trim() ? message : fallback;
  }
}
