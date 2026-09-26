import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css'],
})
export class ChangePasswordComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);

  submitting = false;
  submitted = false;
  form = this.fb.nonNullable.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [ Validators.required, Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!]).{8,15}$/,)]],
    confirmPassword: ['', Validators.required],
  });

  get passwordsMismatch(): boolean {
    return (
      this.form.controls.newPassword.value !== this.form.controls.confirmPassword.value
    );
  }

  submit(): void {
    this.submitted = true;
    this.form.markAllAsTouched();
    if (this.form.invalid || this.passwordsMismatch || this.submitting) return;
    const { currentPassword, newPassword } = this.form.getRawValue();
    this.submitting = true;
    this.authService.changePassword({ currentPassword, newPassword }).subscribe({
      next: () => {
        this.submitting = false;
        this.toastService.success('Password changed successfully. Log-in with new password');
        this.authService.logout();
      },
      error: (err) => {
        this.submitting = false;
        this.toastService.error(err?.error?.message ?? 'Could not change password.');
      },
    });
  }
}
