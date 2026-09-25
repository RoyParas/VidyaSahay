import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { VerificationStatus } from '../../../core/enums/verification-status.enum';
import { StudentDetailedResponse, UpdateStudentVerificationRequest} from '../../../core/models/student-verification.dto/student-verification.dto';
import { StudentService } from '../../../core/services/student.service';
import { StudentVerificationService } from '../../../core/services/student-verification.service';
import { ToastService } from '../../../core/services/toast.service';
import { AuthService } from '../../../core/services/auth.service';
import { UserRole } from '../../../core/enums/user-role.enum';

@Component({
  selector: 'app-student-verification',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './student-verification.component.html',
  styleUrls: ['./student-verification.component.css']
})
export class StudentVerificationComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);
  private readonly studentService = inject(StudentService);
  private readonly studentVerificationService = inject(StudentVerificationService);
  private readonly authService = inject(AuthService);

  readonly VerificationStatus = VerificationStatus;

  studentId = '';
  student: StudentDetailedResponse | null = null;

  loading = false;
  updatingStatus = false;
  submitted = false;

  selectedStatus: VerificationStatus | null = null;

  verificationForm = this.fb.group({
    remark: [
      '',
      [
        Validators.required,
        Validators.maxLength(1000)
      ]
    ]
  });

  ngOnInit(): void {
    this.studentId = this.route.snapshot.paramMap.get('studentId') ?? '';

    if (!this.studentId) {
      this.toastService.error('Student ID is required.');
      if(this.isAdmin) {
        this.router.navigateByUrl('students');
      }
      else {
        this.router.navigateByUrl('my-students');
      }
      return;
    }

    this.loadStudent();
  }

  get isAdmin() {
    return this.authService.getRole() === UserRole.ADMIN;
  }

  get remarkControl() {
    return this.verificationForm.get('remark');
  }

  get remarkLength(): number {
    return this.remarkControl?.value?.length ?? 0;
  }

  get fullName(): string {
    if (!this.student) {
      return '-';
    }

    const firstName = this.student.firstName ?? '';
    const lastName = this.student.lastName ?? '';

    return `${firstName} ${lastName}`.trim() || '-';
  }

  get completeAddress(): string {
    if (!this.student) {
      return '-';
    }

    const addressParts = [
      this.student.location,
      this.student.address?.city,
      this.student.address?.district,
      this.student.address?.state,
      this.student.address?.country,
      this.student.pincode
    ];

    const completeAddress = addressParts
      .filter(value =>
        value !== null &&
        value !== undefined &&
        value !== ''
      )
      .join(', ');

    return completeAddress || '-';
  }

  get currentStatus(): VerificationStatus {
    return this.student?.verificationStatus ??
      VerificationStatus.PENDING;
  }

  get isVerified(): boolean {
    return this.currentStatus === VerificationStatus.VERIFIED;
  }

  loadStudent(): void {
    this.loading = true;

    this.studentService
      .getStudentById(this.studentId)
      .subscribe({
        next: (data: StudentDetailedResponse) => {
          this.student = data;
          this.loading = false;
        },
        error: (err) => {
          this.loading = false;

          this.toastService.error(
            err?.error?.message ??
            'Failed to load student details.'
          );
        }
      });
  }

  updateVerificationStatus(
    status: VerificationStatus
  ): void {
    if (
      this.currentStatus === VerificationStatus.VERIFIED &&
      status === VerificationStatus.REJECTED
    ) {
      this.toastService.error(
        'A verified student cannot be changed to rejected.'
      );
      return;
    }

    this.submitted = true;
    this.selectedStatus = status;

    if (this.verificationForm.invalid) {
      this.verificationForm.markAllAsTouched();
      this.selectedStatus = null;
      return;
    }

    const remark = this.remarkControl?.value?.trim() ?? '';

    if (!remark) {
      this.remarkControl?.setErrors({
        required: true
      });

      this.remarkControl?.markAsTouched();
      this.selectedStatus = null;
      return;
    }

    const request: UpdateStudentVerificationRequest = {
      status: status,
      remark: remark
    };

    this.updatingStatus = true;

    this.studentVerificationService
      .updateStatus(this.studentId, request)
      .subscribe({
        next: () => {
          this.updatingStatus = false;
          this.submitted = false;
          this.selectedStatus = null;

          this.verificationForm.reset({
            remark: ''
          });

          this.toastService.success(
            'Student verification status updated successfully.'
          );

          this.loadStudent();
        },
        error: (err) => {
          this.updatingStatus = false;
          this.selectedStatus = null;

          this.toastService.error(
            err?.error?.message ??
            'Failed to update student verification status.'
          );
        }
      });
  }

  verifyStudent(): void {
    this.updateVerificationStatus(
      VerificationStatus.VERIFIED
    );
  }

  rejectStudent(): void {
    if (this.isVerified) {
      this.toastService.error(
        'A verified student cannot be changed to rejected.'
      );
      return;
    }

    this.updateVerificationStatus(
      VerificationStatus.REJECTED
    );
  }

  isUpdating(
    status: VerificationStatus
  ): boolean {
    return this.updatingStatus &&
      this.selectedStatus === status;
  }

  getStatusLabel(
    status: VerificationStatus | null | undefined
  ): string {
    switch (status) {
      case VerificationStatus.VERIFIED:
        return 'Verified';

      case VerificationStatus.REJECTED:
        return 'Rejected';

      case VerificationStatus.PENDING:
        return 'Pending';

      default:
        return 'Pending';
    }
  }

  getStatusBadgeClass(
    status: VerificationStatus | null | undefined
  ): string {
    switch (status) {
      case VerificationStatus.VERIFIED:
        return 'badge badge-approved';

      case VerificationStatus.REJECTED:
        return 'badge badge-rejected';

      case VerificationStatus.PENDING:
        return 'badge badge-pending';

      default:
        return 'badge badge-pending';
    }
  }
}