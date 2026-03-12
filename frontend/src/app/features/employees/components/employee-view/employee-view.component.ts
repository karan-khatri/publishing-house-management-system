import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { NotificationService } from '@core/services/notification.service';
import { ButtonComponent } from '@shared/components/ui';
import { Employee } from '@app/shared/types';
import { EmployeeService } from '../../services/employee.service';
import { PermissionService } from '@app/core/services/permission.service';

@Component({
  selector: 'app-employee-view',
  standalone: true,
  imports: [CommonModule, ButtonComponent],
  templateUrl: './employee-view.component.html',
})
export class EmployeeViewComponent implements OnInit {
  isLoading = false;
  employeeId: string | null = null;
  employee: Employee | null = null;
  error: string | null = null;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly employeeService: EmployeeService,
    private readonly notification: NotificationService,
    private readonly permissionService: PermissionService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.employeeId = params.get('id');
      if (this.employeeId) {
        this.loadEmployeeData(this.employeeId);
      } else {
        this.router.navigate(['/employees']);
      }
    });
  }

  loadEmployeeData(employeeId: string): void {
    if (!employeeId) return;

    this.isLoading = true;
    this.error = null;

    this.employeeService.getEmployeeById(+employeeId).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success && response.data) {
          this.employee = response.data;
        } else {
          this.error = response.message || 'Failed to load employee';
          this.notification.error(this.error);
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.error = 'Failed to load employee';
        console.error('Error loading employee:', error);
        this.notification.error(this.error);
      }
    });
  }

  onEdit(): void {
    if (this.employeeId) {
      this.router.navigate(['/employees', this.employeeId, 'edit']);
    }
  }

  onDelete(): void {
    if (!this.employee || !this.employeeId) return;

    if (!confirm(`Are you sure you want to delete "${this.employee.user.name}"?`)) {
      return;
    }

    this.employeeService.deleteEmployee(this.employeeId).subscribe({
      next: (response) => {
        if (response.success) {
          this.notification.success(`Employee "${this.employee?.user.name}" deleted successfully`);
          this.router.navigate(['/employees']);
        } else {
          this.notification.error(response.message || 'Failed to delete employee');
        }
      },
      error: (error) => {
        console.error('Error deleting employee:', error);
        this.notification.error('Failed to delete employee');
      }
    });
  }

  onBackToList(): void {
    this.router.navigate(['/employees']);
  }

  formatDate(date: string | Date | undefined): string {
    if (!date) return 'Not specified';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  getStatusClass(isActive: boolean): string {
    return isActive 
      ? 'bg-green-100 text-green-800' 
      : 'bg-red-100 text-red-800';
  }

  getStatusDisplayName(isActive: boolean): string {
    return isActive ? 'Active' : 'Inactive';
  }

  retry(): void {
    if (this.employeeId) {
      this.loadEmployeeData(this.employeeId);
    }
  }

  canEditEmployees(): boolean {
    return this.permissionService.hasPermission('canEditEmployees');
  }

  canDeleteEmployees(): boolean {
    return this.permissionService.hasPermission('canDeleteEmployees');
  }
}