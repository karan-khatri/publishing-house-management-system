import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { SelectOption, Pagination } from '@app/shared/types';
import { FormsModule } from '@angular/forms';

export interface PageChangeEvent {
  page: number; // new page (0-based index)
  limit: number; // page size
}

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './pagination.component.html',
})
export class PaginationComponent implements OnChanges {
  @Input() config: Pagination = {
    page: 0,
    totalItems: 0,
    limit: 10,
  };

  @Input() showPageSizeSelector = true;
  @Input() pageSizeOptions: number[] = [10, 15, 20, 25, 50];
  @Input() showInfo = true;
  @Input() disabled = false;

  @Output() pageChange = new EventEmitter<PageChangeEvent>();
  @Output() pageSizeChange = new EventEmitter<number>();

  // Derived values (no signals)
  totalPages = 0;
  maxPagesToShow = 5;
  visiblePages: number[] = [];

  get pageSizeSelectOptions(): SelectOption[] {
    return this.pageSizeOptions.map((size) => ({
      label: size.toString(),
      value: size.toString(),
    }));
  }

  ngOnChanges(_: SimpleChanges): void {
    this.recomputeDerived();
  }

  private recomputeDerived(): void {
    // guard against 0 limit
    const limit = Math.max(1, Number(this.config.limit) || 1);
    const total = Math.max(0, Number(this.config.totalItems) || 0);

    this.totalPages = Math.ceil(total / limit);

    // clamp current page into range
    if (this.totalPages > 0 && this.config.page > this.totalPages - 1) {
      this.config = { ...this.config, page: this.totalPages - 1 };
    }

    this.calculateVisiblePages();
  }

  private calculateVisiblePages(): void {
    const total = this.totalPages;
    const current = this.config.page;

    if (total <= this.maxPagesToShow) {
      this.visiblePages = Array.from({ length: total }, (_, i) => i);
      return;
    }

    let start = Math.max(0, current - Math.floor(this.maxPagesToShow / 2));
    let end = Math.min(total - 1, start + this.maxPagesToShow - 1);

    if (end - start < this.maxPagesToShow - 1) {
      start = Math.max(0, end - this.maxPagesToShow + 1);
    }

    this.visiblePages = Array.from({ length: end - start + 1 }, (_, i) => start + i);
  }

  private goToPage(page: number): void {
    const total = this.totalPages;
    if (!this.disabled && page >= 0 && page < total && page !== this.config.page) {
      this.pageChange.emit({ page, limit: this.config.limit });
    }
  }

  onPageClick(page: number): void {
    this.goToPage(page);
  }

  onPreviousClick(): void {
    this.goToPage(this.config.page - 1);
  }

  onNextClick(): void {
    this.goToPage(this.config.page + 1);
  }

  onFirstClick(): void {
    this.goToPage(0);
  }

  onLastClick(): void {
    this.goToPage(this.totalPages - 1);
  }

  onPageSizeChange(newPageSize: string): void {
    const size = Number(newPageSize);
    if (!this.disabled && size > 0 && size !== this.config.limit) {
      this.pageSizeChange.emit(size);
    }
  }

  // Getters for template
  get isPreviousDisabled(): boolean {
    return this.disabled || this.config.page <= 0;
  }

  get isNextDisabled(): boolean {
    return this.disabled || this.config.page >= this.totalPages - 1;
  }

  get startIndex(): number {
    return this.config.totalItems === 0 ? 0 : this.config.page * this.config.limit + 1;
  }

  get endIndex(): number {
    return Math.min((this.config.page + 1) * this.config.limit, this.config.totalItems);
  }
}
