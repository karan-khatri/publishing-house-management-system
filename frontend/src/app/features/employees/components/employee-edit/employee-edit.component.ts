import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NotificationService } from '@core/services/notification.service';
import {
  InputFormFieldComponent,
  ButtonComponent,
  SelectFormFieldComponent,
} from '@shared/components/ui';
import { RoleService } from '@app/features/login/services/role.service';
import { Employee, Role, SelectOption } from '@app/shared/types';
import { EmployeeService } from '../../services/employee.service';

@Component({
  selector: 'app-employee-edit',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    InputFormFieldComponent,
    ButtonComponent,
    SelectFormFieldComponent,
  ],
  templateUrl: './employee-edit.component.html',
})
export class EmployeeEditComponent implements OnInit {
  isLoading = false;
  isEditMode = false;
  employeeId: string | null = null;
  employees: Employee[] = [];
  roles: Role[] = [];
  employeeForm!: FormGroup;
  roleOptions: SelectOption[] = [];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly formBuilder: FormBuilder,
    private readonly employeeService: EmployeeService,
    private readonly roleService: RoleService,
    private readonly notification: NotificationService
  ) {
    this.initializeForm(route.snapshot.paramMap.get('id') !== null);
  }

  ngOnInit(): void {
    this.loadRoles();

    this.route.paramMap.subscribe((params) => {
      this.employeeId = params.get('id');
      if (this.employeeId) {
        this.isEditMode = true;
        this.loadEmployeeData(this.employeeId);
      }
    });
  }

  private initializeForm(isEditMode: boolean): void {
    this.employeeForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', isEditMode ? [] : [Validators.required, Validators.minLength(8)]],
      position: ['', [Validators.required]],
      department: ['', [Validators.required]],
      phone: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(15)]],
      address: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
      roleId: ['', [Validators.required]],
      employeeId: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(20)]],
    });
  }

  loadRoles(): void {
    this.roleService.getRoles().subscribe({
      next: (response) => {
        this.roles = response?.data;
        this.roleOptions = this.roles.map((role) => ({
          label: role.name,
          value: role.id.toString(),
        }));
      },
      error: (error) => {
        console.error('Error loading roles:', error);
        this.notification.error('Failed to load roles');
        this.roles = []; // Fallback to empty array
      },
    });
  }

  populateForm(employee: Employee): void {
    this.employeeId = employee.id?.toString() || null;
    this.employeeForm.patchValue({
      name: employee.user.name,
      email: employee.user.email,
      roleId: employee.user.role.id,
      employeeId: employee.employeeId,
      position: employee.position,
      department: employee.department,
      phone: employee.phone,
      address: employee.address,
    });
  }

  loadEmployeeData(employeeId: string): void {
    if (!employeeId) return;

    this.isLoading = true;
    this.employeeService.getEmployeeById(+employeeId).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.populateForm(response.data);
        } else {
          this.notification.error(response.message || 'Failed to load employee');
          this.router.navigate(['/employees']);
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error loading employee:', error);
        this.notification.error('Failed to load employee');
        this.router.navigate(['/employees']);
      },
    });
  }

  createEmployee(): void {
    if (this.employeeForm.invalid) {
      this.notification.warning('Please fill in all required fields correctly', 'Validation Error');
      return;
    }

    this.isLoading = true;
    const employeeData = this.employeeForm.value;
    this.employeeService.createEmployee(employeeData).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.notification.success('Employee created successfully');
          this.router.navigate(['/employees']);
        } else {
          this.notification.error(response.message || 'Failed to create employee');
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error creating employee:', error);
        this.notification.error('Failed to create employee');
      },
    });
  }

  updateEmployee(): void {
    if (this.employeeForm.invalid) {
      this.notification.warning('Please fill in all required fields correctly', 'Validation Error');
      return;
    }

    this.isLoading = true;
    const employeeData = this.employeeForm.value;
    this.employeeService.updateEmployee(this.employeeId ?? '', employeeData).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.notification.success('Employee updated successfully');
          this.router.navigate(['/employees']);
        } else {
          this.notification.error(response.message || 'Failed to update employee');
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error updating employee:', error);
        this.notification.error('Failed to update employee');
      },
    });
  }

  onSubmit(): void {
    if (this.employeeForm.invalid) {
      this.notification.warning('Please fill in all required fields correctly', 'Validation Error');
      return;
    }

    if (this.isEditMode) {
      this.updateEmployee();
    } else {
      this.createEmployee();
    }
  }

  getFormErrors(): { field: string; message: string }[] {
    const errors: { field: string; message: string }[] = [];

    if (this.employeeForm.invalid) {
      Object.keys(this.employeeForm.controls).forEach((key) => {
        const control = this.employeeForm.get(key);
        if (control && control.invalid && control.touched) {
          const fieldName = this.getFieldDisplayName(key);
          if (control.errors?.['required']) {
            errors.push({ field: key, message: `${fieldName} is required` });
          }
          if (control.errors?.['email']) {
            errors.push({ field: key, message: `${fieldName} must be a valid email address` });
          }
          if (control.errors?.['minlength']) {
            errors.push({
              field: key,
              message: `${fieldName} must be at least ${control.errors['minlength'].requiredLength} characters`,
            });
          }
        }
      });
    }

    return errors;
  }

  private getFieldDisplayName(fieldName: string): string {
    const fieldNames: { [key: string]: string } = {
      name: 'Name',
      email: 'Email Address',
      password: 'Password',
      position: 'Position',
      department: 'Department',
      phone: 'Phone',
      employeeId: 'Employee ID',
      roleId: 'Role',
      address: 'Address',
    };
    return fieldNames[fieldName] || fieldName;
  }

  // Add the onCancel method if it doesn't exist
  onCancel(): void {
    this.router.navigate(['/employees']);
  }
}
