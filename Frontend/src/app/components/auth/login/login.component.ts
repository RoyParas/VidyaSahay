import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { ToastService } from '../../../core/services/toast.service';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/auth.dtos/login.dtos';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['../auth.component.css', './login.component.css']
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly toastService = inject(ToastService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  logoPath = 'VidyaSahay_Logo.png';
  submitted = false;
  loading = false;

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  get emailControl() {
    return this.loginForm.get('email');
  }

  get passwordControl() {
    return this.loginForm.get('password');
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;

    let loginRequest: LoginRequest  = {
      email: this.emailControl?.value ?? '',
      password: this.passwordControl?.value ??  ''
    };

    this.authService.login(loginRequest).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl(this.authService.getDefaultRoute());
        this.toastService.success('User logged-in successfully.');
      },
      error: (err) => {
        this.loading = false;
        this.toastService.error(err?.error?.message ?? 'Login failed');
      }
    })
  }

  isInvalid(controlName: 'email' | 'password'): boolean {
    const control = this.loginForm.get(controlName);
    return !!control && control.invalid && (control.dirty || control.touched || this.submitted);
  }
}
