import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CommonTableColumn, CommonTableComponent } from '../../../shared/common-table/common-table.component';
import { ApplicationService } from '../../../core/services/application.service';
import { ApplicationSummary } from '../../../core/models/summaryResponse.dto';
import { AuthService } from '../../../core/services/auth.service';
import { UserRole } from '../../../core/enums/user-role.enum';

@Component({
  selector: 'app-application-list',
  standalone: true,
  imports: [CommonTableComponent],
  templateUrl: './application-list.component.html',
  styleUrl: './application-list.component.css'
})
export class ApplicationListComponent implements OnInit {
  private readonly applicationService = inject(ApplicationService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  rows: ApplicationSummary[] = [];
  loading = true;
  errorMessage = '';

  get tableTitle(): string {
    return this.authService.getRole() === UserRole.INSTITUTE
      ? 'Student applications'
      : 'My applications';
  }

  readonly columns: CommonTableColumn[] = [
    { key: 'id', label: 'Application ID', formatter: value => String(value ?? '').slice(0, 8).toUpperCase() },
    { key: 'schemeName', label: 'Scheme' },
    { key: 'applicationType', label: 'Type', type: 'badge', badgeMap: { loan: 'vs-badge-reverted', scholarship: 'vs-badge-verified' } },
    {
      key: 'studentFirstName',
      label: 'Student',
      formatter: (_value, row) => `${String(row['studentFirstName'] ?? '')} ${String(row['studentLastName'] ?? '')}`.trim() || '-'
    },
    {
      key: 'status',
      label: 'Status',
      type: 'badge',
      formatter: value => String(value ?? '').replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, letter => letter.toUpperCase()),
      badgeMap: {
        submitted: 'vs-badge-pending',
        under_review: 'vs-badge-pending',
        approved: 'vs-badge-verified',
        rejected: 'vs-badge-rejected',
        reverted: 'vs-badge-reverted'
      }
    },
    { key: 'approvedAmount', label: 'Approved amount', type: 'currency' }
  ];

  ngOnInit(): void {
    this.applicationService.getMyApplications().subscribe({
      next: rows => {
        this.rows = rows;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load applications. Please try again.';
        this.loading = false;
      }
    });
  }

  onRowClick(row: Record<string, unknown>): void {
    const applicationId = row['id'] as string;
    this.router.navigate(['/application', applicationId]);
  }
}
