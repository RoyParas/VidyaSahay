import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router'
import { CommonTableColumn, CommonTableComponent } from '../../../shared/common-table/common-table.component';
import { StudentService } from '../../../core/services/student.service';
import { StudentSummary } from '../../../core/models/summaryResponse.dto';
import { AuthService } from '../../../core/services/auth.service';
import { InstituteService } from '../../../core/services/institute.service';
import { UserRole } from '../../../core/enums/user-role.enum';

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [CommonTableComponent],
  templateUrl: './student-list.component.html',
  styleUrl: './student-list.component.css',
})
export class StudentListComponent implements OnInit {

  private readonly studentService = inject(StudentService);
  private readonly instituteService = inject(InstituteService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  rows: StudentSummary[] = [];
  loading = true;
  errorMessage = '';
  title = 'Students';
  subtitle = 'Registered student accounts, including profiles awaiting completion.';

  columns: CommonTableColumn[] = [
    { key: 'name', label: 'Name' },
    { key: 'email', label: 'Email' },
    { key: 'mobile', label: 'Mobile' },
    {
      key: 'profileCompleted',
      label: 'Profile Status',
      type: 'badge',
      badgeMap: { true: 'vs-badge-verified', false: 'vs-badge-pending' },
      formatter: value => value ? 'Complete' : 'Incomplete'
    },
    { key: 'verificationStatus',
      label: 'Verification Status',
      type: 'badge',
      badgeMap: {
        pending: 'vs-badge-pending',
        verified: 'vs-badge-verified',
        rejected: 'vs-badge-rejected',
      } },
    { key: 'courseName', label: 'Course' },
    { key: 'instituteName', label: 'Institute' },
  ];

  ngOnInit(): void {
    const isInstitute = this.authService.getRole() === UserRole.INSTITUTE;
    const request = isInstitute ? this.instituteService.getMyStudents() : this.studentService.getStudents();
    if (isInstitute) {
      this.title = 'My Students';
      this.subtitle = 'Students enrolled at your institute.';
      this.columns = this.columns.filter(column => column.key !== 'instituteName' && column.key !== 'profileCompleted');
    }

    request.subscribe({
      next: (rows) => {
        this.rows = rows.map(row => ({
          ...row,
          name: `${row.firstName} ${row.lastName}`
        }));
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load students. Please try again.';
        this.loading = false;
      },
    });
  }

  onRowClick(row: Record<string, unknown>): void {
    const studentId = row['studentId'] as string | null;
    if (!studentId) return;
    const isInstitute = this.authService.getRole() === UserRole.INSTITUTE;
    this.router.navigate(isInstitute
      ? ['student-verification', studentId]
      : ['student', studentId]);
  }
}
