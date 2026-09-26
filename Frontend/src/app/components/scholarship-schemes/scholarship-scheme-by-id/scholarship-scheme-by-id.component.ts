import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  DestroyRef,
  OnInit,
  inject,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { ScholarshipSchemeDetails } from '../../../core/models/scholarship-scheme.model';
import { ScholarshipSchemeService } from '../../../core/services/scholarship-scheme.service';
import { ToastService } from '../../../core/services/toast.service';
import { AuthService } from '../../../core/services/auth.service';
import { UserRole } from '../../../core/enums/user-role.enum';
import { StudentService } from '../../../core/services/student.service';

@Component({
  selector: 'app-scholarship-scheme-by-id',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
  ],
  templateUrl: './scholarship-scheme-by-id.component.html',
  styleUrls: [
    '../../loan-schemes/loan-scheme-by-id/loan-scheme-by-id.component.css',
    './scholarship-scheme-by-id.component.css',
  ],
})
export class ScholarshipSchemeByIdComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly scholarshipSchemeService = inject(ScholarshipSchemeService);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);
  private readonly studentService = inject(StudentService);

  scholarshipScheme: ScholarshipSchemeDetails | null = null;
  scholarshipSchemeId = '';
  canUpdate = false;
  canApply = false;

  loading = true;
  errorMessage = '';

  get isStudent() : boolean {
    return this.authService.getRole() === UserRole.STUDENT;
  }

  get schemeListRoute(): string {
    return this.authService.getRole() === UserRole.GOVERNMENT
      ? '/scholarship-schemes-created-by-me'
      : this.isStudent ? '/scholarship-schemes/eligible' : '/scholarship-schemes';
  }

  ngOnInit(): void {
    this.scholarshipSchemeId =
      this.route.snapshot.paramMap
        .get('scholarshipSchemeId')
        ?.trim() ?? '';

    if (!this.scholarshipSchemeId) {
      this.loading = false;
      this.errorMessage = 'The scholarship scheme ID is missing from the URL.';

      this.toastService.error(this.errorMessage,'Unable to load scholarship');

      return;
    }

    this.loadScholarshipScheme();
  }

  loadScholarshipScheme(): void {
    if (!this.scholarshipSchemeId) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.scholarshipScheme = null;

    this.scholarshipSchemeService
      .getScholarshipSchemeById(this.scholarshipSchemeId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (scholarshipScheme) => {
          this.scholarshipScheme = scholarshipScheme;
          this.loading = false;
          this.checkApplicationEligibility();
          if (this.authService.getRole() === UserRole.GOVERNMENT) {
            this.scholarshipSchemeService.getScholarshipSchemesCreatedByMe()
              .pipe(takeUntilDestroyed(this.destroyRef))
              .subscribe({
                next: schemes => this.canUpdate = schemes.some(scheme => scheme.scholarshipSchemeId === this.scholarshipSchemeId),
                error: () => this.canUpdate = false
              });
          }
        },
        error: (error: HttpErrorResponse) => {
          this.loading = false;
          this.errorMessage = this.getErrorMessage(error);

          this.toastService.error(this.errorMessage,'Unable to load scholarship');
        },
      });
  }

  private checkApplicationEligibility(): void {
    if (!this.isStudent || this.route.snapshot.queryParamMap.get('eligible') !== 'true') return;
    const annualFamilyIncome = Number(this.route.snapshot.queryParamMap.get('annualFamilyIncome'));
    const academicPercentage = Number(this.route.snapshot.queryParamMap.get('academicPercentage'));
    if (!Number.isFinite(annualFamilyIncome) || !Number.isFinite(academicPercentage)) return;
    forkJoin({
      profile: this.studentService.getMyProfile(),
      eligibleSchemes: this.scholarshipSchemeService.getEligibleScholarshipSchemes(annualFamilyIncome, academicPercentage)
    }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: ({ profile, eligibleSchemes }) => {
        this.canApply = profile.verificationStatus === 'VERIFIED'
          && eligibleSchemes.some(scheme => scheme.scholarshipSchemeId === this.scholarshipSchemeId);
      },
      error: () => this.canApply = false
    });
  }

  applyForScheme(): void {
    if (!this.canApply) return;
    this.router.navigate(['/applications/apply/SCHOLARSHIP', this.scholarshipSchemeId], {
      queryParams: {
        annualFamilyIncome: this.route.snapshot.queryParamMap.get('annualFamilyIncome'),
        academicPercentage: this.route.snapshot.queryParamMap.get('academicPercentage')
      }
    });
  }

  formatEnumValue(value: string | null | undefined): string {
    if (!value) {
      return 'Not available';
    }

    return value
      .trim()
      .toLowerCase()
      .split('_')
      .filter(Boolean)
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }

  getStatusClass(status: string | null | undefined): string {
    switch (status?.toUpperCase()) {
      case 'ACTIVE':
      case 'APPROVED':
        return 'badge badge-approved';

      case 'PENDING':
      case 'UNDER_REVIEW':
        return 'badge badge-pending';

      case 'REJECTED':
        return 'badge badge-rejected';

      case 'INACTIVE':
      case 'CLOSED':
        return 'badge badge-neutral';

      default:
        return 'badge badge-info';
    }
  }

  trackByValue(index: number, value: string): string {
    return value || String(index);
  }

  private getErrorMessage(error: HttpErrorResponse): string {
    if (error.status === 404) {
      return 'The requested scholarship scheme could not be found.';
    }

    if (error.status === 401) {
      return 'Your session is no longer valid. Please log in again.';
    }

    if (error.status === 403) {
      return 'You do not have permission to view this scholarship scheme.';
    }

    if (error.status === 0) {
      return 'Unable to connect to the server. Please check whether the backend is running.';
    }

    const backendMessage = error.error?.message;

    if (
      typeof backendMessage === 'string' &&
      backendMessage.trim()
    ) {
      return backendMessage;
    }

    return 'Could not load the scholarship scheme. Please try again.';
  }
}
