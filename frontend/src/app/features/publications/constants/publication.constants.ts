import { PublicationStatus, PublicationType, SelectOption } from '@app/shared/types';

// Publication Type Options for dropdowns
export const PUBLICATION_TYPE_OPTIONS: SelectOption[] = [
  { value: PublicationType.TEXTBOOK, label: 'Textbook' },
  { value: PublicationType.NOVEL, label: 'Novel' },
  { value: PublicationType.BIOGRAPHY, label: 'Biography' },
  { value: PublicationType.SCIENCE_FICTION, label: 'Science Fiction' },
  { value: PublicationType.RESEARCH_PAPER, label: 'Research Paper' },
];

// Publication Type Options with "All Types" option for filtering
export const PUBLICATION_TYPE_FILTER_OPTIONS: SelectOption[] = [
  { value: '', label: 'All Types' },
  ...PUBLICATION_TYPE_OPTIONS
];

// Publication Status Options for dropdowns
export const PUBLICATION_STATUS_OPTIONS: SelectOption[] = [
  { value: PublicationStatus.DRAFT, label: 'Draft' },
  { value: PublicationStatus.IN_REVIEW, label: 'In Review' },
  { value: PublicationStatus.PUBLISHED, label: 'Published' }
];

// Publication Status Options with "All Statuses" option for filtering
export const PUBLICATION_STATUS_FILTER_OPTIONS: SelectOption[] = [
  { value: '', label: 'All Statuses' },
  ...PUBLICATION_STATUS_OPTIONS
];

// Sort Options for publications
export const PUBLICATION_SORT_OPTIONS: SelectOption[] = [
  { value: 'title', label: 'Title' },
  { value: 'publicationDate', label: 'Publication Date' },
  { value: 'price', label: 'Price' },
  { value: 'isbn', label: 'ISBN' }
];

// Sort Direction Options
export const SORT_DIRECTION_OPTIONS: SelectOption[] = [
  { value: 'asc', label: 'Ascending' },
  { value: 'desc', label: 'Descending' }
];

// Status Display Names
export const PUBLICATION_STATUS_DISPLAY_NAMES: { [key in PublicationStatus]: string } = {
  [PublicationStatus.DRAFT]: 'Draft',
  [PublicationStatus.IN_REVIEW]: 'In Review',
  [PublicationStatus.PUBLISHED]: 'Published',
};

// Status CSS Classes
export const PUBLICATION_STATUS_CSS_CLASSES: { [key in PublicationStatus]: string } = {
  [PublicationStatus.DRAFT]: 'status-draft',
  [PublicationStatus.IN_REVIEW]: 'status-review',
  [PublicationStatus.PUBLISHED]: 'status-published',
};

// Default pagination settings
export const DEFAULT_PAGINATION = {
  SIZE: 5,
  SORT_BY: 'title',
  SORT_DIRECTION: 'desc'
} as const;

// Search debounce delay in milliseconds
export const SEARCH_DEBOUNCE_DELAY = 500;

// Default form values
export const DEFAULT_PUBLICATION_FORM = {
  title: '',
  isbn: '',
  description: '',
  price: null as number | null,
  edition: '1',
  pages: null as number | null,
  type: PublicationType.TEXTBOOK,
  status: PublicationStatus.DRAFT,
  authorIds: [] as number[]
};
