import { Component, OnInit, inject } from '@angular/core';
import { CommonTableColumn, CommonTableComponent } from '../../../shared/common-table/common-table.component';
import { BankSummary } from '../../../core/models/summaryResponse.dto';
import { BankService } from '../../../core/services/bank.service';

@Component({
  selector: 'app-bank-list',
  standalone: true,
  imports: [CommonTableComponent],
  templateUrl: './bank-list.component.html',
  styleUrl: './bank-list.component.css',
})
export class BankListComponent implements OnInit {
  private readonly bankService = inject(BankService);
  rows: BankSummary[] = [];
  loading = true;
  errorMessage = '';
  readonly columns: CommonTableColumn[] = [
    { key: 'bankName', label: 'Bank' },
    { key: 'contactPersonName', label: 'Contact name' },
    { key: 'email', label: 'Email' },
    { key: 'mobile', label: 'Mobile' },
    {
      key: 'active',
      label: 'Status',
      type: 'badge',
      formatter: (value) => (value ? 'Active' : 'Inactive'),
      badgeMap: { true: 'vs-badge-verified', false: 'vs-badge-neutral' },
    },
  ];
  ngOnInit(): void {
    this.bankService.getBanks().subscribe({
      next: (rows) => {
        this.rows = rows.map(row => ({
          ...row,
          contactPersonName: `${row.contactPersonFirstName} ${row.contactPersonLastName}`
        }));
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load banks. Please try again.';
        this.loading = false;
      },
    });
  }
}
