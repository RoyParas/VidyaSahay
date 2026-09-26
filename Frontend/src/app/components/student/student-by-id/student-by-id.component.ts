import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RouterLink } from '@angular/router';
import { StudentDetailedResponse } from '../../../core/models/student-verification.dto/student-verification.dto';
import { StudentService } from '../../../core/services/student.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-student-by-id',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './student-by-id.component.html',
  styleUrls: ['./student-by-id.component.css']
})
export class StudentByIdComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly studentService = inject(StudentService);
  private readonly toastService = inject(ToastService);

  @Input() studentId = '';
  @Input() selfProfile = false;
  @Output() studentLoaded = new EventEmitter<StudentDetailedResponse>();

  student: StudentDetailedResponse | null = null;
  loading = false;

  ngOnInit(): void {
    this.selfProfile = this.selfProfile || this.route.snapshot.data['selfProfile'] === true;
    if (!this.studentId) {
      this.studentId = this.route.snapshot.paramMap.get('studentId') ?? '';
    }
    this.loadStudent();
  }

  get fullName(): string {
    const name = `${this.student?.firstName ?? ''} ${this.student?.lastName ?? ''}`.trim();
    return name || '-';
  }

  get completeAddress(): string {
    if (!this.student) return '-';
    return [
      this.student.location,
      this.student.address?.city,
      this.student.address?.district,
      this.student.address?.state,
      this.student.address?.country,
      this.student.pincode
    ].filter(value => value !== null && value !== undefined && value !== '').join(', ') || '-';
  }

  loadStudent(): void {
    if (!this.selfProfile && !this.studentId) {
      this.toastService.error('Student ID is required.');
      return;
    }

    this.loading = true;
    const request = this.selfProfile
      ? this.studentService.getMyProfile()
      : this.studentService.getStudentById(this.studentId);

    request.subscribe({
      next: student => {
        this.student = student;
        this.loading = false;
        this.studentLoaded.emit(student);
      },
      error: err => {
        this.loading = false;
        this.student = null;
        this.toastService.error(err?.error?.message ?? 'Failed to load student details.');
      }
    });
  }
}
