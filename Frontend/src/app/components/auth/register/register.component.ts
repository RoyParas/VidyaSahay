import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { ToastService } from '../../../core/services/toast.service';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest } from '../../../core/models/auth.dtos/register.dtos';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['../auth.component.css', './register.component.css'],
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly toastService = inject(ToastService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  logoPath = 'VidyaSahay_Logo.png';
  submitted = false;
  loading = false;

  registerForm = this.fb.group({
    firstName: this.fb.nonNullable.control('', [Validators.required,]),
    lastName: this.fb.nonNullable.control('', [Validators.required,]),
    email: this.fb.nonNullable.control('', [Validators.required, Validators.email]),
    mobile: this.fb.nonNullable.control('', [Validators.required, Validators.pattern(/^[6-9][0-9]{9}$/)]),
    password: this.fb.nonNullable.control('', [Validators.required, Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!]).{8,15}$/)]),
    confirmPassword: this.fb.nonNullable.control('', [Validators.required]),
    terms: this.fb.nonNullable.control(false, [Validators.requiredTrue]),
  },
  {
    validators: passwordMatchValidator,
  });

  onSubmit(): void {
    this.submitted = true;

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const { firstName, lastName, email, mobile, password} = this.registerForm.getRawValue();

    const registerRequest: RegisterRequest = {
      firstName,
      lastName,
      email,
      mobile,
      password,
    };

    this.loading = true;

    this.authService.register(registerRequest).subscribe({
      next: () => {
        this.loading = false;
        this.toastService.success('Registered successfully.');
        this.router.navigateByUrl('/login');
      },
      error: (err) => {
        this.loading = false;
        this.toastService.error(
          err?.error?.message ?? 'Registration failed.'
        );
      },
    });
  }

  isInvalid(controlName: string): boolean {
    const control = this.registerForm.get(controlName);

    return !!(
      control &&
      control.invalid &&
      (control.dirty || control.touched || this.submitted)
    );
  }

  isConfirmPasswordMismatch(): boolean {
    return !!(
      this.registerForm.errors?.['passwordMismatch'] &&
      (
        this.submitted ||
        this.registerForm.get('confirmPassword')?.dirty ||
        this.registerForm.get('confirmPassword')?.touched
      )
    );
  }

  control(name: string) {
    return this.registerForm.get(name);
  }
}

export const passwordMatchValidator: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
  const password = group.get('password')?.value;
  const confirmPassword =
    group.get('confirmPassword')?.value;

  if (!password || !confirmPassword) {
    return null;
  }

  return password === confirmPassword ? null : { passwordMismatch: true };
};