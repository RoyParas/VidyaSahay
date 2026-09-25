import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';

export interface CommonTableColumn {
  key: string;
  label: string;
  align?: 'left' | 'center' | 'right';
  width?: string;
  type?: 'text' | 'badge' | 'date' | 'number' | 'currency';
  badgeMap?: Record<string, string>;
  formatter?: (value: unknown, row: Record<string, unknown>) => string;
}

export interface CommonTableAction {
  id: string;
  label: string;
  variant?: 'primary' | 'secondary' | 'ghost' | 'success' | 'danger';
}

@Component({
  selector: 'app-common-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './common-table.component.html',
  styleUrl: './common-table.component.css'
})
export class CommonTableComponent implements OnChanges {
  @Input() title = '';
  @Input() subtitle = '';
  @Input() columns: CommonTableColumn[] = [];
  @Input() rows: Record<string, unknown>[] = [];
  @Input() loading = false;
  @Input() searchable = true;
  @Input() searchPlaceholder = 'Search records';
  @Input() pageSize = 5;
  @Input() showIndex = false;
  @Input() emptyTitle = 'No records found';
  @Input() emptyMessage = 'There is nothing to display right now.';
  @Input() actions: CommonTableAction[] = [];
  @Input() showActions = false;

  @Output() actionClick = new EventEmitter<{ actionId: string; row: Record<string, unknown> }>();
  @Output() rowClick = new EventEmitter<Record<string, unknown>>();

  searchText = '';
  currentPage = 1;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['rows'] || changes['pageSize']) {
      this.currentPage = 1;
    }
  }

  get filteredRows(): Record<string, unknown>[] {
    const query = this.searchText.trim().toLowerCase();
    if (!query) {
      return this.rows;
    }

    return this.rows.filter(row =>
      this.columns.some(column => {
        const value = this.getValue(row, column.key);
        return String(value ?? '')
          .toLowerCase()
          .includes(query);
      })
    );
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredRows.length / this.effectivePageSize));
  }

  get pagedRows(): Record<string, unknown>[] {
    const start = (this.currentPage - 1) * this.effectivePageSize;
    return this.filteredRows.slice(start, start + this.effectivePageSize);
  }

  get startItem(): number {
    return this.filteredRows.length ? (this.currentPage - 1) * this.effectivePageSize + 1 : 0;
  }

  get endItem(): number {
    return Math.min(this.currentPage * this.effectivePageSize, this.filteredRows.length);
  }

  get showPagination(): boolean {
    return this.filteredRows.length > this.effectivePageSize;
  }

  get colspan(): number {
    return this.columns.length + (this.showIndex ? 1 : 0) + (this.showActions && this.actions.length ? 1 : 0);
  }

  onSearchChange(): void {
    this.currentPage = 1;
  }

  get effectivePageSize(): number {
    return Math.max(1, this.pageSize || 1);
  }

  goToPage(page: number): void {
    this.currentPage = Math.min(Math.max(page, 1), this.totalPages);
  }

  onAction(actionId: string, row: Record<string, unknown>): void {
    this.actionClick.emit({ actionId, row });
  }

  onRowClick(row: Record<string, unknown>): void {
    this.rowClick.emit(row);
  }

  getValue(row: Record<string, unknown>, key: string): unknown {
    return key.split('.').reduce<unknown>((value, part) => {
      if (value && typeof value === 'object' && part in (value as Record<string, unknown>)) {
        return (value as Record<string, unknown>)[part];
      }

      return undefined;
    }, row);
  }

  formatValue(column: CommonTableColumn, row: Record<string, unknown>): string {
    const value = this.getValue(row, column.key);

    if (column.formatter) {
      return column.formatter(value, row);
    }

    if (value === null || value === undefined || value === '') {
      return '-';
    }

    if (column.type === 'date') {
      const dateValue = new Date(String(value));
      return Number.isNaN(dateValue.getTime()) ? String(value) : dateValue.toLocaleDateString();
    }

    if (column.type === 'currency') {
      const numericValue = Number(value);
      return Number.isNaN(numericValue) ? String(value) : numericValue.toLocaleString('en-IN', {
        style: 'currency',
        currency: 'INR',
        maximumFractionDigits: 2
      });
    }

    if (column.type === 'number') {
      const numericValue = Number(value);
      return Number.isNaN(numericValue) ? String(value) : numericValue.toLocaleString('en-IN');
    }

    return String(value);
  }

  getBadgeClass(column: CommonTableColumn, row: Record<string, unknown>): string {
    const value = String(this.getValue(row, column.key) ?? '').toLowerCase();
    return column.badgeMap?.[value] ?? 'vs-badge-neutral';
  }

  getActionVariant(variant?: CommonTableAction['variant']): string {
    return `vs-btn-${variant ?? 'ghost'}`;
  }

  getPages(): number[] {
    const pageCount = Math.min(this.totalPages, 5);
    const firstPage = Math.min(
      Math.max(this.currentPage - Math.floor(pageCount / 2), 1),
      this.totalPages - pageCount + 1
    );
    return Array.from({ length: pageCount }, (_, index) => firstPage + index);
  }

  trackByIndex(index: number, _row?: Record<string, unknown>): number {
    return index;
  }
}
