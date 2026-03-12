import { SelectOption } from "@app/shared/types";

// Author sort options
export const AUTHOR_SORT_OPTIONS: SelectOption[] = [
  { value: 'name', label: 'Name' },
  { value: 'email', label: 'Email' },
  { value: 'nationality', label: 'Nationality' },
  { value: 'createdAt', label: 'Created Date' }
];

// Sort direction options
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
