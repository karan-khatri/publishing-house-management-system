import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseApiService } from '@core/services/base-api.service';
import { Employee, ApiResponse, PagedApiResponse, Pagination } from '@shared/types';

export interface EmployeeSearchFilters {
  query?: string;
  department?: string;
  position?: string;
  sortBy?: string;
  sortDir?: '' | 'asc' | 'desc';
}

export interface CreateEmployeeRequest {
  name: string;
  email: string;
  phone: string;
  address: string;
  position: string;
  department: string;
  roleId: number;
  password: string;
  employeeId: string;
}

export interface UpdateEmployeeRequest extends Omit<ApiResponse<Employee>['data'], 'id' | 'createdAt' | 'updatedAt' | 'user'> {}

@Injectable({
  providedIn: 'root'
})
export class EmployeeService extends BaseApiService<Employee, CreateEmployeeRequest, UpdateEmployeeRequest> {
  protected endpoint = '/employees';

  /**
   * Get employees with filtering
   */
  getEmployees(
    pagination: Pagination,
    filters: EmployeeSearchFilters = {}
  ): Observable<PagedApiResponse<Employee[]>> {
    return this.getList(pagination, filters);
  }

  /**
   * Get employee by ID
   */
  getEmployeeById(id: number): Observable<ApiResponse<Employee>> {
    return this.getById(id);
  }

  /**
   * Create new employee
   */
  createEmployee(employee: CreateEmployeeRequest): Observable<ApiResponse<Employee>> {
    return this.create(employee);
  }

  /**
   * Update existing employee
   */
  updateEmployee(id: string, employee: UpdateEmployeeRequest): Observable<ApiResponse<Employee>> {
    return this.update(id, employee);
  }

  /**
   * Delete employee
   */
  deleteEmployee(id: string): Observable<ApiResponse<Employee>> {
    return this.delete(id);
  }

}