import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { PublicationSearchFilters, PublicationService } from '../../services/publication.service';
import { NotificationService } from '@core/services/notification.service';
import {
  InputFieldComponent,
  SelectFieldComponent,
  PaginationComponent,
  PageChangeEvent,
} from '@shared/components/ui';

import {
  PUBLICATION_TYPE_FILTER_OPTIONS,
  PUBLICATION_STATUS_FILTER_OPTIONS,
  PUBLICATION_SORT_OPTIONS,
  SORT_DIRECTION_OPTIONS,
} from '../../constants';
import { Pagination, Publication } from '@app/shared/types';
import { AuthService } from '@app/core/services/auth.service';
import { PermissionService } from '@app/core/services/permission.service';

@Component({
  selector: 'app-publication-list',
  standalone: true,
  imports: [FormsModule, SelectFieldComponent, InputFieldComponent, PaginationComponent],
  templateUrl: './publication-list.component.html',
})
export class PublicationListComponent implements OnInit {
  private readonly searchSubject = new Subject<PublicationSearchFilters>();

  publications: Publication[] = [];

  pagination: Pagination = {
    page: 0,
    limit: 10,
    totalItems: 0,
  };

  // UI State
  loading: boolean = false;
  error: string = '';

  searchFilters: PublicationSearchFilters = {
    title: '',
    type: undefined,
    status: undefined,
    sortBy: 'title',
    sortDir: 'desc',
  };

  // Dropdown options
  typeOptions = PUBLICATION_TYPE_FILTER_OPTIONS;
  statusOptions = PUBLICATION_STATUS_FILTER_OPTIONS;
  sortByOptions = PUBLICATION_SORT_OPTIONS;
  sortDirectionOptions = SORT_DIRECTION_OPTIONS;

  constructor(
    private readonly publicationService: PublicationService,
    private readonly permissionService: PermissionService,
    private readonly notificationService: NotificationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {
    this.setupSearchDebounce();
  }

  ngOnInit(): void {
    // Initialize filters from query parameters
    this.searchFilters = {
      title: this.route.snapshot.queryParams['title'] || this.searchFilters.title,
      type: this.route.snapshot.queryParams['type'] || this.searchFilters.type,
      status: this.route.snapshot.queryParams['status'] || this.searchFilters.status,
      sortBy: this.route.snapshot.queryParams['sortBy'] || this.searchFilters.sortBy,
      sortDir: this.route.snapshot.queryParams['sortDir'] || this.searchFilters.sortDir,
    };

    this.pagination = {
      page: 0,
      limit: 10,
      totalItems: 0,
    };

    this.loadPublications(this.pagination, this.searchFilters);
  }

  private setupSearchDebounce(): void {
    this.searchSubject
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe((searchFilters: PublicationSearchFilters) => {
        this.searchFilters = { ...searchFilters };
        this.pagination = { ...this.pagination, page: 0 };
        this.updateQueryParams(this.pagination, this.searchFilters);
      });
  }

  loadPublications(pagination: Pagination, filters: PublicationSearchFilters): void {
    this.loading = true;
    this.error = '';

    // Prepare filters for API call
    const searchFilters = {
      ...filters,
      page: pagination.page,
      size: pagination.limit,
    };

    // Remove empty filters
    if (!searchFilters.title) delete searchFilters.title;
    if (!searchFilters.type) delete searchFilters.type;
    if (!searchFilters.status) delete searchFilters.status;

    this.publicationService.getPublications(pagination, searchFilters).subscribe({
      next: (response) => {

        if (response.success) {
          this.publications = response.data.content;
          this.pagination = {
            page: +response.data.page,
            limit: +response.data.limit,
            totalItems: +response.data.totalItems,
          };
        } else {
          this.error = response.message || 'Failed to load publications';
          this.notificationService.error(this.error?.toString());
        }
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load publications';
        this.loading = false;
        console.error('Error loading publications:', error);
        this.notificationService.error(this.error);
      },
    });
  }

  onSearchFilterChange(key: keyof PublicationSearchFilters, value: string): void {
    this.searchFilters = { ...this.searchFilters, [key]: value };
    this.searchSubject.next(this.searchFilters);
  }

  updateQueryParams(pagination: Pagination, filters: PublicationSearchFilters): void {
    const queryParams: any = {
      page: pagination.page,
      size: pagination.limit,
    };

    // Add non-empty filters to query params
    if (filters.title) queryParams.title = filters.title;
    if (filters.type) queryParams.type = filters.type;
    if (filters.status) queryParams.status = filters.status;
    if (filters.sortBy) queryParams.sortBy = filters.sortBy;
    if (filters.sortDir) queryParams.sortDir = filters.sortDir;

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'replace',
    });

    this.loadPublications(pagination, filters);
  }

  onPaginationChange(event: PageChangeEvent): void {
    this.pagination = { totalItems: this.pagination.totalItems, ...event };
    this.updateQueryParams(this.pagination, this.searchFilters);
  }

  onPageSizeChange(newPageSize: number): void {
    this.pagination = { ...this.pagination, page: 0, limit: newPageSize };
    this.updateQueryParams(this.pagination, this.searchFilters);
  }

  onCreateNew(): void {
    this.router.navigate(['/publications/new']);
  }

  onPublicationView(id: number): void {
    this.router.navigate(['/publications', id]);
  }

  onEditPublication(publication: Publication): void {
    this.router.navigate(['/publications', publication.id, 'edit']);
  }

  onPublicationDelete(id: number, event: MouseEvent): void {
    event.stopPropagation();

    const publication = this.publications.find((p) => p.id === id);
    if (!publication) return;

    if (!confirm(`Are you sure you want to delete "${publication.title}"?`)) {
      return;
    }

    this.publicationService.deletePublication(id).subscribe({
      next: (response) => {
        if (response.success) {
          this.publications = this.publications.filter((pub) => pub.id !== id);
          this.notificationService.success(
            `Publication "${publication.title}" deleted successfully`
          );
        } else {
          this.notificationService.error(response.message || 'Failed to delete publication');
        }
      },
      error: (error) => {
        console.error('Error deleting publication:', error);
        this.notificationService.error('Failed to delete publication');
      },
    });
  }

  // Utility methods
  canEditPublications(): boolean {
    return this.permissionService.hasPermission('canEditPublications');
  }

  canDeletePublications(): boolean {
    return this.permissionService.hasPermission('canDeletePublications');
  }

  getAuthorNames(publication: Publication): string {
    return publication.authors?.map((author) => author.name).join(', ') || 'No authors';
  }

  formatDate(date: string | Date | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString();
  }

  getFormattedPrice(price: number | undefined): string {
    if (price === undefined || price === null) return 'N/A';
    return `$${price.toFixed(2)}`;
  }

  getStatusDisplayName(status: string): string {
    const statusMap: { [key: string]: string } = {
      DRAFT: 'Draft',
      PUBLISHED: 'Published',
      ARCHIVED: 'Archived',
    };
    return statusMap[status] || status;
  }

  getStatusClass(status: string): string {
    const statusClasses: { [key: string]: string } = {
      DRAFT: 'bg-yellow-100 text-yellow-800',
      PUBLISHED: 'bg-green-100 text-green-800',
      ARCHIVED: 'bg-gray-100 text-gray-800',
    };
    return statusClasses[status] || 'bg-gray-100 text-gray-800';
  }
}
