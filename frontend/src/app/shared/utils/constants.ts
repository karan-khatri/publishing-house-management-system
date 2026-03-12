/**
 * =============================================================================
 * CONSTANTS
 * =============================================================================
 * Extracted from author constants/index.ts
 */

import { SelectOption } from "@app/shared/types";

// Author sort options (from constants/index.ts)
export const AUTHOR_SORT_OPTIONS: SelectOption[] = [
  { value: 'name', label: 'Name' },
  { value: 'email', label: 'Email' },
  { value: 'nationality', label: 'Nationality' },
  { value: 'createdAt', label: 'Created Date' }
];

// Sort direction options (from constants/index.ts)
export const SORT_DIRECTION_OPTIONS: SelectOption[] = [
  { value: 'asc', label: 'Ascending' },
  { value: 'desc', label: 'Descending' }
];

export const AUTHOR_SORT_FILTER_OPTIONS: SelectOption[] = [
  { value: '', label: 'Select a filter', disabled: true },
  ...AUTHOR_SORT_OPTIONS,
];

export const AUTHOR_SORT_DIRECTION_FILTER_OPTIONS: SelectOption[] = [
  { value: '', label: 'Select a sort direction', disabled: true },
  ...SORT_DIRECTION_OPTIONS,
];

/**
 * Dashboard feature cards configuration (from dashboard.component.ts)
 */
export const DASHBOARD_FEATURE_CARDS = [
  {
    title: 'Publications',
    description: 'Add, edit, and remove publications.',
    buttonText: 'Go to Publications',
    link: '/publications',
  },
  {
    title: 'Authors',
    description: 'Add, edit, and remove authors.',
    buttonText: 'Go to Authors',
    link: '/authors',
  },
  {
    title: 'Employees',
    description: 'Add, edit, and remove employee accounts.',
    buttonText: 'Go to Employees',
    link: '/employees',
  },
];

/**
 * Application routes constants
 */
export const APP_ROUTES = {
  LOGIN: '/login',
  DASHBOARD: '/dashboard',
  PUBLICATIONS: '/publications',
  AUTHORS: '/authors',
  EMPLOYEES: '/employees',
} as const;

/**
 * Employee form field display names (from employee-edit.component.ts)
 */
export const EMPLOYEE_FIELD_NAMES: { [key: string]: string } = {
  name: 'Name',
  email: 'Email Address',
  password: 'Password',
  position: 'Position',
  department: 'Department',
  phone: 'Phone',
  employeeId: 'Employee ID',
  roleId: 'Role',
  address: 'Address'
};

/**
 * Form validation constraints
 */
export const VALIDATION_CONSTRAINTS = {
  NAME_MIN_LENGTH: 2,
  NAME_MAX_LENGTH: 100,
  PASSWORD_MIN_LENGTH: 8,
  PHONE_MIN_LENGTH: 10,
  PHONE_MAX_LENGTH: 15,
  ADDRESS_MIN_LENGTH: 5,
  ADDRESS_MAX_LENGTH: 200,
  EMPLOYEE_ID_MIN_LENGTH: 2,
  EMPLOYEE_ID_MAX_LENGTH: 20
};

/**
 * Department options for employee form
 */
export const DEPARTMENT_OPTIONS: SelectOption[] = [
  { value: '', label: 'Select a department', disabled: true },
  { value: 'EDITORIAL', label: 'Editorial' },
  { value: 'MARKETING', label: 'Marketing' },
  { value: 'SALES', label: 'Sales' },
  { value: 'PRODUCTION', label: 'Production' },
  { value: 'HR', label: 'Human Resources' },
  { value: 'FINANCE', label: 'Finance' },
  { value: 'IT', label: 'Information Technology' },
  { value: 'LEGAL', label: 'Legal' },
  { value: 'ADMIN', label: 'Administration' }
];

/**
 * Position options for employee form
 */
export const POSITION_OPTIONS: SelectOption[] = [
  { value: '', label: 'Select a position', disabled: true },
  { value: 'DIRECTOR', label: 'Director' },
  { value: 'MANAGER', label: 'Manager' },
  { value: 'SENIOR_EDITOR', label: 'Senior Editor' },
  { value: 'EDITOR', label: 'Editor' },
  { value: 'ASSISTANT_EDITOR', label: 'Assistant Editor' },
  { value: 'DESIGNER', label: 'Designer' },
  { value: 'MARKETING_SPECIALIST', label: 'Marketing Specialist' },
  { value: 'SALES_REP', label: 'Sales Representative' },
  { value: 'ACCOUNTANT', label: 'Accountant' },
  { value: 'HR_SPECIALIST', label: 'HR Specialist' },
  { value: 'IT_SPECIALIST', label: 'IT Specialist' },
  { value: 'ADMIN_ASSISTANT', label: 'Administrative Assistant' }
];

/**
 * Form notification messages
 */
export const FORM_MESSAGES = {
  VALIDATION_ERROR: 'Please fill in all required fields correctly',
  EMPLOYEE_CREATED: 'Employee created successfully',
  EMPLOYEE_UPDATED: 'Employee updated successfully',
  EMPLOYEE_CREATE_ERROR: 'Failed to create employee',
  EMPLOYEE_UPDATE_ERROR: 'Failed to update employee',
  EMPLOYEE_LOAD_ERROR: 'Failed to load employee',
  ROLES_LOAD_ERROR: 'Failed to load roles'
};