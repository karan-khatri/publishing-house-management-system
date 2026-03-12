# Publication Constants and Utilities

This directory contains centralized constants and utility functions for the publication feature, promoting code reusability and maintainability.

## Structure

```
constants/
├── index.ts                   # Barrel file exporting all constants and utils
├── publication.constants.ts   # Publication-specific constants
├── mock-data.constants.ts     # Mock data for development
└── utils/
    └── publication.utils.ts   # Utility functions
```

## Constants (`publication.constants.ts`)

### Publication Type Options
- `PUBLICATION_TYPE_OPTIONS`: Array of all publication types for dropdowns
- `PUBLICATION_TYPE_FILTER_OPTIONS`: Publication types with "All Types" option for filtering

### Publication Status Options
- `PUBLICATION_STATUS_OPTIONS`: Array of all publication statuses
- `PUBLICATION_STATUS_FILTER_OPTIONS`: Status options with "All Statuses" for filtering

### Display and Styling
- `PUBLICATION_STATUS_DISPLAY_NAMES`: Mapping of status enums to human-readable names
- `PUBLICATION_STATUS_CSS_CLASSES`: Mapping of status enums to CSS class names

### Form and UI Settings
- `PUBLICATION_SORT_OPTIONS`: Available sorting options
- `SORT_DIRECTION_OPTIONS`: Sort direction options (asc/desc)
- `DEFAULT_PAGINATION`: Default pagination settings
- `DEFAULT_PUBLICATION_FORM`: Default form values for publication creation/editing
- `SEARCH_DEBOUNCE_DELAY`: Debounce delay for search inputs (500ms)

## Mock Data (`mock-data.constants.ts`)

### Authors Data
- `MOCK_AUTHORS`: Array of mock author objects for development
- `getMockAuthors()`: Async function simulating API call with delay

> **Note**: Replace mock data with actual API calls when author service is implemented.

## Utilities (`publication.utils.ts`)

### Display Formatters
- `getAuthorNames(publication)`: Get formatted author names from publication
- `formatDate(dateString)`: Format date string for display
- `getFormattedPrice(price)`: Format price with currency symbol

### Permission Helpers
- `canEditPublication(publication, currentUser)`: Check if user can edit publication
- `canDeletePublication(publication, currentUser)`: Check if user can delete publication

### UI Helpers
- `getAuthorSelectionOptions(authors)`: Convert authors to dropdown options
- `getPaginationPages(currentPage, totalPages, maxVisible)`: Generate pagination page numbers

## Usage Examples

### In Components

```typescript
import { 
  PUBLICATION_TYPE_OPTIONS,
  DEFAULT_PUBLICATION_FORM,
  canEditPublication,
  formatDate 
} from '../../constants';

export class MyComponent {
  typeOptions = PUBLICATION_TYPE_OPTIONS;
  formData = { ...DEFAULT_PUBLICATION_FORM };
  
  canEdit(publication: Publication): boolean {
    return canEditPublication(publication, this.authService.getCurrentUser());
  }
  
  formatPublicationDate(date: string): string {
    return formatDate(date);
  }
}
```

### Adding New Constants

1. Add constants to appropriate file in `constants/`
2. Export from `index.ts` if needed for external use
3. Update this documentation

### Benefits

✅ **DRY Principle**: Eliminates code duplication across components
✅ **Maintainability**: Single source of truth for constants
✅ **Type Safety**: TypeScript ensures consistency
✅ **Reusability**: Easy to import and use across the application
✅ **Testing**: Easier to mock and test individual utilities
✅ **Performance**: Shared functions reduce bundle size

## Future Improvements

- [ ] Replace mock authors with actual API service
- [ ] Add internationalization support for display names
- [ ] Create more specific utility functions as needed
- [ ] Add unit tests for utility functions
