import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

import { AuthorSearchFilters, AuthorService } from '../../services/author.service';
import { NotificationService } from '@core/services/notification.service';
import { Author, Pagination } from '@app/shared/types';

// Shared UI
import {
  PageChangeEvent,
  PaginationComponent,
  InputFieldComponent,
  ButtonComponent,
  SelectFieldComponent,
} from '@app/shared/components/ui';

// Constants
import { AUTHOR_SORT_DIRECTION_FILTER_OPTIONS, AUTHOR_SORT_FILTER_OPTIONS } from '../../constants';
import { PermissionService } from '@app/core/services/permission.service';

@Component({
  selector: 'app-author-list',
  standalone: true,
  templateUrl: './author-list.component.html',
  imports: [
    FormsModule,
    PaginationComponent,
    ButtonComponent,
    SelectFieldComponent,
    InputFieldComponent,
  ],
})
export class AuthorListComponent implements OnInit {
  private readonly searchSubject = new Subject<AuthorSearchFilters>();
  // UI state
  loading = false;
  error: string | null = null;

  authors: Author[] = [];

  pagination: Pagination = {
    page: 0,
    limit: 10,
    totalItems: 0,
  };

  // Filters
  searchFilters: AuthorSearchFilters = {
    query: '',
    sortBy: '',
    sortDir: '',
  };

  // Dropdowns
  sortByOptions = AUTHOR_SORT_FILTER_OPTIONS;
  sortDirectionOptions = AUTHOR_SORT_DIRECTION_FILTER_OPTIONS;

  constructor(
    private readonly authorService: AuthorService,
    private readonly permissionService: PermissionService,
    private readonly notificationService: NotificationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {
    this.setupSearchDebounce();
  }

  ngOnInit(): void {
    const queryParams = this.route.snapshot.queryParams;
    this.searchFilters = {
      query: queryParams['query'] || this.searchFilters.query,
      sortBy: queryParams['sortBy'] || this.searchFilters.sortBy,
      sortDir: queryParams['sortDir'] || this.searchFilters.sortDir,
    };

    this.pagination = {
      page: +(queryParams['page'] || this.pagination.page),
      limit: +(queryParams['limit'] || this.pagination.limit),
      totalItems: 0,
    };



    this.loadAuthors(this.pagination, this.searchFilters);
  }

  private setupSearchDebounce(): void {
    this.searchSubject
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe((filters: AuthorSearchFilters) => {
        this.searchFilters = filters;
        this.updateQueryParams(this.pagination, this.searchFilters);
      });
  }

  loadAuthors(pagination: Pagination, filters: AuthorSearchFilters): void {
    this.loading = true;
    this.error = null;

    this.authorService
      .getAuthors(pagination, filters)
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.authors = response.data.content;
            this.pagination = {
              page: +response.data.page,
              limit: +response.data.limit,
              totalItems: +response.data.totalItems,
            };
          }
          this.loading = false;
        },
        error: (error) => {
          this.error = error;
          this.loading = false;
          this.notificationService.error('Failed to load authors');
        },
      });
  }

  onSearchFilterChange(key: keyof AuthorSearchFilters, value: string): void {
    this.searchFilters = { ...this.searchFilters, [key]: value };
    this.searchSubject.next(this.searchFilters);
  }

  clearFilters(): void {
    this.searchFilters = {
      query: '',
      sortBy: '',
      sortDir: '',
    };
    this.updateQueryParams(this.pagination, this.searchFilters);
  }

  updateQueryParams(pagination: Pagination, filters: AuthorSearchFilters): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        limit: pagination.limit,
        page: pagination.page,
        ...filters,
      },
    });

    this.loadAuthors(pagination, filters);
  }

  onPaginationChange(event: PageChangeEvent): void {
    this.pagination = { totalItems: this.pagination.totalItems, ...event };
    this.updateQueryParams(this.pagination, this.searchFilters);
  }

  onPageSizeChange(newPageSize: number): void {
    this.pagination = { ...this.pagination, page: 0, limit: newPageSize };
    this.updateQueryParams(this.pagination, this.searchFilters);
  }

  onAuthorDelete(id: number, event: MouseEvent): void {
    event.stopPropagation();
    if (!confirm('Are you sure you want to delete this author?')) {
      return;
    }

    this.authorService.deleteAuthor(id).subscribe({
      next: () => {
        this.authors = this.authors.filter((author) => author.id !== id);
        this.notificationService.success(`Author deleted successfully`);
      },
      error: () => {
        this.notificationService.error('Failed to delete author');
      },
    });
  }

  onAuthorView(id: number, event: MouseEvent): void {
    event.stopPropagation();
    this.router.navigate(['/authors', id]);
  }

  canDeleteAuthor(): boolean {
    return this.permissionService.hasPermission('canDeleteAuthors');
  }

  canEditAuthor(): boolean {
    return this.permissionService.hasPermission('canEditAuthors');
  }
}
