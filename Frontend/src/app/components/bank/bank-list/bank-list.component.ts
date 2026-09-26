import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { CommonTableAction, CommonTableColumn, CommonTableComponent } from '../../../shared/common-table/common-table.component';
import { BankSummary } from '../../../core/models/summaryResponse.dto';
import { BankService } from '../../../core/services/bank.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { UserRole } from '../../../core/enums/user-role.enum';
import { BankDetails } from '../../../core/models/bank-details.dto';

@Component({
  selector: 'app-bank-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CommonTableComponent],
  templateUrl: './bank-list.component.html',
  styleUrl: './bank-list.component.css',
})
export class BankListComponent implements OnInit {
  private readonly bankService = inject(BankService);
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);
  private readonly fb = inject(FormBuilder);
  rows: BankSummary[] = [];
  loading = true;
  saving = false;
  modalLoading = false;
  modalOpen = false;
  submitted = false;
  errorMessage = '';
  modalError = '';
  modalMode: 'create' | 'view' | 'edit' = 'create';
  selectedBank: BankDetails | null = null;
  private initialEditSignature: string | null = null;
  readonly isAdmin = this.authService.getRole() === UserRole.ADMIN;
  readonly form = this.fb.nonNullable.group({
    firstName: ['', [Validators.required, Validators.pattern(/\S/)]],
    lastName: ['', [Validators.required, Validators.pattern(/\S/)]],
    email: ['', [Validators.required, Validators.email]],
    mobile: ['', [Validators.required, Validators.pattern(/^[6-9][0-9]{9}$/)]],
    bankName: ['', [Validators.required, Validators.pattern(/\S/)]],
    status: [true],
  });
  readonly columns: CommonTableColumn[] = [
    { key: 'bankName', label: 'Bank' },
    { key: 'contactPersonName', label: 'Contact name' },
    { key: 'email', label: 'Email' },
    { key: 'mobile', label: 'Mobile' },
    {
      key: 'active',
      label: 'Status',
      type: 'badge',
      formatter: (value) => (value ? 'Active' : 'Inactive'),
      badgeMap: { true: 'vs-badge-verified', false: 'vs-badge-neutral' },
    },
  ];
  readonly rowActions: CommonTableAction[] = [
    { id: 'update', label: 'Update', variant: 'secondary' },
  ];
  ngOnInit(): void {
    this.loadBanks();
  }

  loadBanks(): void {
    this.loading = true;
    this.bankService.getBanks().pipe(finalize(() => this.loading = false)).subscribe({
      next: (rows) => {
        this.rows = rows.map(row => ({
          ...row,
          contactPersonName: `${row.contactPersonFirstName} ${row.contactPersonLastName}`
        }));
        this.errorMessage = '';
      },
      error: () => {
        this.errorMessage = 'Could not load banks. Please try again.';
      },
    });
  }

  openModal(): void {
    if (!this.isAdmin) return;
    this.modalMode = 'create';
    this.selectedBank = null;
    this.initialEditSignature = null;
    this.modalError = '';
    this.form.reset();
    this.submitted = false;
    this.modalOpen = true;
  }

  closeModal(): void {
    if (!this.saving) this.modalOpen = false;
  }

  onBankRowClick(row: Record<string, unknown>): void {
    this.openBankDetails(String(row['bankId']), 'view');
  }

  onBankAction(event: { actionId: string; row: Record<string, unknown> }): void {
    if (event.actionId === 'update') this.openBankDetails(String(event.row['bankId']), 'edit');
  }

  openBankDetails(bankId: string, mode: 'view' | 'edit'): void {
    if (!this.isAdmin) return;
    this.modalMode = mode;
    this.selectedBank = null;
    this.initialEditSignature = null;
    this.modalError = '';
    this.submitted = false;
    this.form.reset();
    this.modalOpen = true;
    this.modalLoading = true;

    this.bankService.getBankById(bankId).pipe(finalize(() => this.modalLoading = false)).subscribe({
      next: (bank) => {
        this.selectedBank = bank;
        this.form.patchValue({
          firstName: bank.contactPersonFirstName,
          lastName: bank.contactPersonLastName,
          email: bank.email,
          mobile: bank.mobile,
          bankName: bank.bankName,
          status: bank.status,
        });
        if (mode === 'edit') this.initialEditSignature = this.editSignature();
      },
      error: (error: HttpErrorResponse) => {
        this.modalError = this.apiError(error, 'Could not load bank details. Please try again.');
      },
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
      value.status,
    ]);
  }

  createBank(): void {
    this.submitted = true;
    if (!this.isAdmin || this.form.invalid || this.saving) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.saving = true;
    this.bankService.createBank({
      firstName: value.firstName.trim(),
      lastName: value.lastName.trim(),
      email: value.email.trim(),
      mobile: value.mobile.trim(),
      bankName: value.bankName.trim(),
    }).pipe(finalize(() => this.saving = false)).subscribe({
      next: () => {
        this.toast.success('Bank created successfully.');
        this.modalOpen = false;
        this.loadBanks();
      },
      error: (error: HttpErrorResponse) => this.toast.error(this.apiError(error, 'Could not create bank. Please try again.')),
    });
  }

  updateBank(): void {
    this.submitted = true;
    if (!this.isAdmin || !this.selectedBank || !this.hasChanges || this.form.invalid || this.saving) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.saving = true;
    this.bankService.updateBank(this.selectedBank.bankId, {
      firstName: value.firstName.trim(),
      lastName: value.lastName.trim(),
      email: value.email.trim(),
      mobile: value.mobile.trim(),
      status: value.status,
    }).pipe(finalize(() => this.saving = false)).subscribe({
      next: () => {
        this.toast.success('Bank updated successfully.');
        this.modalOpen = false;
        this.loadBanks();
      },
      error: (error: HttpErrorResponse) => this.toast.error(this.apiError(error, 'Could not update bank. Please try again.')),
    });
  }

  private apiError(error: HttpErrorResponse, fallback: string): string {
    const message = error.error?.message ?? error.error?.detail;
    return typeof message === 'string' && message.trim() ? message : fallback;
  }
}
