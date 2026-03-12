/**
 * =============================================================================
 * DISPLAY UTILITIES
 * =============================================================================
 * Extracted from author-view.component.ts
 */

/**
 * Get formatted price (from author-view.component.ts)
 */
export function getFormattedPrice(price: number | undefined): string {
  if (price === undefined || price === null) return 'Free';
  return `$${price.toFixed(2)}`;
}

/**
 * Get status display name (from author-view.component.ts)
 */
export function getStatusDisplayName(status: string): string {
  const statusMap: { [key: string]: string } = {
    DRAFT: 'Draft',
    PUBLISHED: 'Published',
    ARCHIVED: 'Archived',
    UNDER_REVIEW: 'Under Review'
  };
  return statusMap[status] || status;
}

/**
 * Get status CSS class (from author-view.component.ts)
 */
export function getStatusClass(status: string): string {
  const statusClasses: { [key: string]: string } = {
    DRAFT: 'bg-yellow-100 text-yellow-800',
    PUBLISHED: 'bg-green-100 text-green-800',
    ARCHIVED: 'bg-gray-100 text-gray-800',
    UNDER_REVIEW: 'bg-blue-100 text-blue-800'
  };
  return statusClasses[status] || 'bg-gray-100 text-gray-800';
}

/**
 * Get type display name (from author-view.component.ts)
 */
export function getTypeDisplayName(type: string): string {
  const typeMap: { [key: string]: string } = {
    BOOK: 'Book',
    MAGAZINE: 'Magazine',
    JOURNAL: 'Journal',
    NEWSPAPER: 'Newspaper'
  };
  return typeMap[type] || type;
}

/**
 * Get user display name with fallback (similar pattern to getAuthorDisplayName)
 */
export function getUserDisplayName(user: { name?: string }): string {
  return user?.name || 'Unknown User';
}

/**
 * Get user role display name
 */
export function getUserRoleDisplayName(user: { role?: { name?: string } }): string {
  return user?.role?.name || 'No Role';
}