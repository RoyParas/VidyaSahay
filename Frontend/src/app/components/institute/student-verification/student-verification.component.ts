import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { VerificationStatus } from '../../../core/enums/verification-status.enum';
import { StudentDetailedResponse, UpdateStudentVerificationRequest } from '../../../core/models/student-verification.dto/student-verification.dto';
import { StudentVerificationService } from '../../../core/services/student-verification.service';
import { ToastService } from '../../../core/services/toast.service';
import { StudentByIdComponent } from '../../student/student-by-id/student-by-id.component';

@Component({
  selector: 'app-student-verification',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, StudentByIdComponent],
  templateUrl: './student-verification.component.html',
  styleUrls: ['./student-verification.component.css']
})
export class StudentVerificationComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);
  private readonly studentVerificationService = inject(StudentVerificationService);

  @ViewChild(StudentByIdComponent) studentProfile?: StudentByIdComponent;

  readonly VerificationStatus = VerificationStatus;
  studentId = '';
  student: StudentDetailedResponse | null = null;
  updatingStatus = false;
  submitted = false;
  selectedStatus: VerificationStatus | null = null;

  verificationForm = this.fb.group({
    remark: ['', [Validators.required, Validators.maxLength(1000)]]
  });

  ngOnInit(): void {
    this.studentId = this.route.snapshot.paramMap.get('studentId') ?? '';
    if (!this.studentId) {
      this.toastService.error('Student ID is required.');
      this.router.navigateByUrl('my-students');
    }
  }

  onStudentLoaded(student: StudentDetailedResponse): void { this.student = student; }

  get remarkControl() { return this.verificationForm.get('remark'); }
  get remarkLength(): number { return this.remarkControl?.value?.length ?? 0; }
  get currentStatus(): VerificationStatus { return this.student?.verificationStatus ?? VerificationStatus.PENDING; }
  get isVerified(): boolean { return this.currentStatus === VerificationStatus.VERIFIED; }

  updateVerificationStatus(status: VerificationStatus): void {
    if (this.currentStatus === VerificationStatus.VERIFIED && status === VerificationStatus.REJECTED) {
      this.toastService.error('A verified student cannot be changed to rejected.');
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
      this.remarkControl?.setErrors({ required: true });
      this.remarkControl?.markAsTouched();
      this.selectedStatus = null;
      return;
    }

    const request: UpdateStudentVerificationRequest = { status, remark };
    this.updatingStatus = true;
    this.studentVerificationService.updateStatus(this.studentId, request).subscribe({
      next: () => {
        this.updatingStatus = false;
        this.submitted = false;
        this.selectedStatus = null;
        this.verificationForm.reset({ remark: '' });
        this.toastService.success('Student verification status updated successfully.');
        this.studentProfile?.loadStudent();
      },
      error: err => {
        this.updatingStatus = false;
        this.selectedStatus = null;
        this.toastService.error(err?.error?.message ?? 'Failed to update student verification status.');
      }
    });
  }

  verifyStudent(): void { this.updateVerificationStatus(VerificationStatus.VERIFIED); }
  rejectStudent(): void {
    if (this.isVerified) {
      this.toastService.error('A verified student cannot be changed to rejected.');
      return;
    }
    this.updateVerificationStatus(VerificationStatus.REJECTED);
  }
  isUpdating(status: VerificationStatus): boolean { return this.updatingStatus && this.selectedStatus === status; }
}
