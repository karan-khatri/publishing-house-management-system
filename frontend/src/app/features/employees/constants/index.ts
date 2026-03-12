export const EMPLOYEE_SORT_OPTIONS = [
  { value: 'name', label: 'Name' },
  { value: 'email', label: 'Email' },
  { value: 'role', label: 'Role' },
  { value: 'department', label: 'Department' },
  { value: 'position', label: 'Position' },
];

export const EMPLOYEE_SORT_DIRECTION_OPTIONS = [
  { value: 'asc', label: 'Ascending' },
  { value: 'desc', label: 'Descending' },
];

export const EMPLOYEE_SORT_FILTER_OPTIONS = [
  { value: '', label: 'Select an option' },
  ...EMPLOYEE_SORT_OPTIONS,
];

export const EMPLOYEE_SORT_DIRECTION_FILTER_OPTIONS = [
  { value: '', label: 'Select an option' },
  ...EMPLOYEE_SORT_DIRECTION_OPTIONS,
];
