import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CommonTableColumn, CommonTableComponent } from '../../../shared/common-table/common-table.component';
import { ScholarshipSchemeSummary } from '../../../core/models/summaryResponse.dto';
import { ScholarshipSchemeService } from '../../../core/services/scholarship-scheme.service';
import { AuthService } from '../../../core/services/auth.service';
import { UserRole } from '../../../core/enums/user-role.enum';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-scholarship-scheme-list',
  standalone: true,
  imports: [CommonTableComponent],
  templateUrl: './scholarship-scheme-list.component.html',
  styleUrl: './scholarship-scheme-list.component.css',
})
export class ScholarshipSchemeListComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly scholarshipSchemeService = inject(ScholarshipSchemeService);
  private readonly router = inject(Router);

  role!: UserRole | null;
  rows: ScholarshipSchemeSummary[] = [];
  loading = true;
  errorMessage = '';
  readonly columns: CommonTableColumn[] = [
    { key: 'scholarshipName', label: 'Scholarship' },
    { key: 'scholarshipType', label: 'Type' },
    { key: 'academicYear', label: 'Academic year' },
    {
      key: 'status',
      label: 'Status',
      type: 'badge',
      badgeMap: {
        active: 'vs-badge-verified',
        approved: 'vs-badge-verified',
        draft: 'vs-badge-draft',
        inactive: 'vs-badge-neutral',
        pending: 'vs-badge-pending',
        rejected: 'vs-badge-rejected',
      },
    },
  ];
  ngOnInit(): void {
    this.role = this.authService.getRole();

    const request$ =
      this.role === UserRole.ADMIN
        ? this.scholarshipSchemeService.getScholarshipSchemes()
        : this.role === UserRole.GOVERNMENT
          ? this.scholarshipSchemeService.getScholarshipSchemesCreatedByMe()
          : this.scholarshipSchemeService.getScholarshipSchemesCreatedByMe();

    request$
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (rows) => {
          this.rows = rows;
        },
        error: () => {
          this.errorMessage = 'Could not load scholarship schemes. Please try again.';
        },
      });
  }

  onRowClick(row: Record<string, unknown>): void {
    const scholarshipSchemeId = row['scholarshipSchemeId'] as string;
    this.router.navigate(['/scholarship-schemes',scholarshipSchemeId]);
  }
}
