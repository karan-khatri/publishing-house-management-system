/**
 * =============================================================================
 * DATE UTILITIES
 * =============================================================================
 * Extracted from author.utils.ts and components
 */

/**
 * Format author's birth date for display (from author.utils.ts)
 */
export function formatBirthDate(birthDate?: Date): string {
  if (!birthDate) return 'N/A';
  
  try {
    const date = birthDate instanceof Date ? birthDate : new Date(birthDate);
    return date.toISOString().split('T')[0]; // Returns YYYY-MM-DD format
  } catch {
    return 'Invalid Date';
  }
}

/**
 * Format author's created date for display (from author.utils.ts)
 */
export function formatCreatedDate(createdDate: Date): string {
  try {
    const date = createdDate instanceof Date ? createdDate : new Date(createdDate);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  } catch {
    return 'Invalid Date';
  }
}

/**
 * Calculate author's age from birth date (from author.utils.ts)
 */
export function calculateAge(birthDate?: Date): number | null {
  if (!birthDate) return null;
  
  try {
    const birth = birthDate instanceof Date ? birthDate : new Date(birthDate);
    const today = new Date();
    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
      age--;
    }
    
    return age;
  } catch {
    return null;
  }
}

/**
 * Format date for display (from author-view.component.ts)
 */
export function formatDate(date: string | Date | undefined): string {
  if (!date) return 'Not specified';
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
}

/**
 * Format date in short format (from author-view.component.ts)
 */
export function formatShortDate(date: string | Date | undefined): string {
  if (!date) return 'N/A';
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short'
  });
}

/**
 * Calculate age with string return (from author-view.component.ts)
 */
export function calculateAgeString(birthDate: string | Date | undefined): string {
  if (!birthDate) return 'Not specified';
  
  const birth = new Date(birthDate);
  const today = new Date();
  let age = today.getFullYear() - birth.getFullYear();
  const monthDiff = today.getMonth() - birth.getMonth();
  
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--;
  }
  
  return `${age} years old`;
}

/**
 * Format date for input fields (from author-edit.component.ts)
 */
export function formatDateForInput(date: Date | string): string {
  const dateObj = typeof date === 'string' ? new Date(date) : date;
  return dateObj.toISOString().split('T')[0];
}

/**
 * Format date for API (from author-edit.component.ts)
 */
export function formatDateForApi(dateString: string): string {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toISOString().split('T')[0];
}