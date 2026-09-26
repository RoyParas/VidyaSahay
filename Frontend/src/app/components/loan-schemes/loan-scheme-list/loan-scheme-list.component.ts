import { Component, OnInit, inject } from '@angular/core';
import { finalize } from 'rxjs';
import { CommonTableAction, CommonTableColumn, CommonTableComponent } from '../../../shared/common-table/common-table.component';
import { AuthService } from '../../../core/services/auth.service';
import { UserRole } from '../../../core/enums/user-role.enum';
import { LoanSchemeSummary } from '../../../core/models/summaryResponse.dto';
import { LoanSchemeService } from '../../../core/services/loan-scheme.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-loan-scheme-list',
  standalone: true,
  imports: [CommonTableComponent],
  templateUrl: './loan-scheme-list.component.html',
  styleUrl: './loan-scheme-list.component.css',
})
export class LoanSchemeListComponent implements OnInit {

  private readonly authService = inject(AuthService);
  private readonly loanSchemeService = inject(LoanSchemeService);
  private readonly router = inject(Router);

  rows: LoanSchemeSummary[] = [];
  loading = true;
  role!: UserRole | null;
  errorMessage = '';

  readonly columns: CommonTableColumn[] = [
    { key: 'schemeName', label: 'Scheme' },
    { key: 'interestType', label: 'Interest type' },
    { key: 'minAmount', label: 'Minimum amount', type: 'currency' },
    { key: 'maxAmount', label: 'Maximum amount', type: 'currency' },
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
  readonly actions: CommonTableAction[] = [{ id: 'update', label: 'Update', variant: 'secondary' }];

  get canUpdate(): boolean { return this.role === UserRole.BANK; }

  ngOnInit(): void {
    this.role = this.authService.getRole();

    const request$ =
      this.role === UserRole.ADMIN
        ? this.loanSchemeService.getLoanSchemes()
        : this.role === UserRole.BANK
          ? this.loanSchemeService.getLoanSchemesCreatedByMe()
          : this.loanSchemeService.getLoanSchemesCreatedByMe();

      request$
        .pipe(finalize(() => (this.loading = false)))
        .subscribe({
          next: (rows) => {
            this.rows = rows;
          },
          error: (err) => {
            this.errorMessage = err.error.message || 'Could not load loan schemes. Please try again.';
          },
        });
  }

  onRowClick(row: Record<string, unknown>): void {
    const loanSchemeId = row['loanSchemeId'] as string;
    this.router.navigate(['/loan-schemes', loanSchemeId]);
  }

  onActionClick(event: { actionId: string; row: Record<string, unknown> }): void {
    if (event.actionId !== 'update' || !this.canUpdate) return;
    const loanSchemeId = event.row['loanSchemeId'] as string;
    this.router.navigate(['/loan-scheme', loanSchemeId, 'edit']);
  }
}
