import { Component, OnInit } from '@angular/core';
import { NotificationService } from '@app/core/services/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Employee, Pagination } from '@app/shared/types';
import {
  PageChangeEvent,
  PaginationComponent,
  SelectFieldComponent,
  InputFieldComponent,
} from '@app/shared/components/ui';
import { FormsModule } from '@angular/forms';
import {
  EMPLOYEE_SORT_DIRECTION_FILTER_OPTIONS,
  EMPLOYEE_SORT_FILTER_OPTIONS,
} from '../../constants';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import { EmployeeSearchFilters, EmployeeService } from '../../services/employee.service';
import { PermissionService } from '@app/core/services/permission.service';

@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html',
  imports: [PaginationComponent, SelectFieldComponent, InputFieldComponent, FormsModule],
})
export class EmployeeListComponent implements OnInit {
  private readonly searchSubject = new Subject<EmployeeSearchFilters>();

  employees: Employee[] = [];

  pagination: Pagination = {
    page: 0,
    limit: 10,
    totalItems: 0,
  };

  // UI State
  loading: boolean = false;
  error: string | null = null;

  searchFilters: EmployeeSearchFilters = {
    sortBy: '',
    sortDir: '',
    query: '',
  };

  sortByOptions = EMPLOYEE_SORT_FILTER_OPTIONS;
  sortDirectionOptions = EMPLOYEE_SORT_DIRECTION_FILTER_OPTIONS;

  constructor(
    private readonly employeeService: EmployeeService  ,
    private readonly notificationService: NotificationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly permissionService: PermissionService
  ) {
    this.setupSearchDebounce();
  }

  ngOnInit(): void {
    this.searchFilters = {
      query: this.route.snapshot.queryParams['query'] || this.searchFilters.query,
      sortBy: this.route.snapshot.queryParams['sortBy'] || this.searchFilters.sortBy,
      sortDir: this.route.snapshot.queryParams['sortDir'] || this.searchFilters.sortDir,
    };

    this.loadEmployees(this.pagination, this.searchFilters);
  }

  private setupSearchDebounce(): void {
    this.searchSubject
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe((searchFilters: EmployeeSearchFilters) => {
        this.searchFilters = searchFilters;
        this.updateQueryParams(this.pagination, this.searchFilters);
      });
  }

  loadEmployees(pagination: Pagination, filters: EmployeeSearchFilters): void {
    this.loading = true;

    this.employeeService.getEmployees(pagination, filters).subscribe({
      next: (response) => {
        this.employees = response.data.content;
        this.pagination = {
          page: +response.data.page,
          limit: +response.data.limit,
          totalItems: +response.data.totalItems,
        };
        this.loading = false;
      },
      error: (error) => {
        this.error = error;
        this.loading = false;
        this.notificationService.error('Failed to load employees');
      },
    });
  }

  onSearchFilterChange(key: keyof EmployeeSearchFilters, value: string): void {
    this.searchFilters = { ...this.searchFilters, [key]: value };
    this.searchSubject.next(this.searchFilters);
  }

  updateQueryParams(pagination: Pagination, filters: EmployeeSearchFilters): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        limit: pagination.limit,
        page: pagination.page,
        ...filters,
      },
    });
    this.loadEmployees(pagination, filters);
  }

  onPaginationChange(event: PageChangeEvent): void {
    this.pagination = { totalItems: this.pagination.totalItems, ...event };

    this.updateQueryParams(this.pagination, this.searchFilters);
  }

  onPageSizeChange(newPageSize: number): void {
    this.pagination = { ...this.pagination, page: 0, limit: newPageSize };

    this.updateQueryParams(this.pagination, this.searchFilters);
  }

  canDeleteEmployee(): boolean {
    return this.permissionService.hasPermission('canDeleteEmployees');
  }

  canEditEmployee(): boolean {
    return this.permissionService.hasPermission('canEditEmployees');
  }

  onEmployeeDelete(id: number, event: MouseEvent): void {
    event.stopPropagation();

    if (!confirm('Are you sure you want to delete this employee?')) {
      return;
    }

    this.employeeService.deleteEmployee(id.toString()).subscribe({
      next: (response) => {
        this.employees = this.employees.filter((employee) => employee.id !== id);
        this.notificationService.success(
          `Employee ${response.data.user.name} deleted successfully`
        );
      },
      error: () => {
        this.notificationService.error('Failed to delete employee');
      },
    });
  }

  onEmployeeView(id: number): void {
    this.router.navigate(['/employees', id]);
  }
}
