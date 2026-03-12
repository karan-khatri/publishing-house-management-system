import { Author } from "@app/shared/types";

/**
 * Format author's birth date for display
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
 * Format author's created date for display
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
 * Calculate author's age from birth date
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
 * Get author's display name with fallback
 */
export function getAuthorDisplayName(author: Author): string {
  return author.name || 'Unnamed Author';
}

/**
 * Get formatted nationality display
 */
export function getNationalityDisplay(nationality?: string): string {
  return nationality || 'Not specified';
}

/**
 * Get publication count display text
 */
export function getPublicationCountDisplay(count?: number): string {
  if (count === undefined || count === null) return 'No publications';
  if (count === 0) return 'No publications';
  if (count === 1) return '1 publication';
  return `${count} publications`;
}

/**
 * Truncate biography text for list display
 */
export function truncateBiography(biography?: string, maxLength: number = 100): string {
  if (!biography) return 'No biography available';
  if (biography.length <= maxLength) return biography;
  return biography.substring(0, maxLength) + '...';
}

/**
 * Generate pagination page numbers for display
 */
export function getPaginationPages(currentPage: number, totalPages: number, maxVisible: number = 5): number[] {
  const pages: number[] = [];
  
  if (totalPages <= maxVisible) {
    // Show all pages if total is less than max visible
    for (let i = 0; i < totalPages; i++) {
      pages.push(i);
    }
  } else {
    // Calculate start and end pages
    let start = Math.max(0, currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(totalPages - 1, start + maxVisible - 1);
    
    // Adjust start if we're near the end
    if (end - start < maxVisible - 1) {
      start = Math.max(0, end - maxVisible + 1);
    }
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
  }
  
  return pages;
}

/**
 * Check if author has any publications
 */
export function hasPublications(author: Author): boolean {
  return (author.publicationCount || 0) > 0;
}

/**
 * Sort authors by different criteria
 */
export function sortAuthors(authors: Author[], sortBy: string, sortDirection: 'asc' | 'desc'): Author[] {
  return [...authors].sort((a, b) => {
    let aValue: any;
    let bValue: any;
    
    switch (sortBy) {
      case 'email':
        aValue = a.email?.toLowerCase() || '';
        bValue = b.email?.toLowerCase() || '';
        break;
      case 'nationality':
        aValue = a.nationality?.toLowerCase() || '';
        bValue = b.nationality?.toLowerCase() || '';
        break;
      case 'birthDate':
        aValue = a.birthDate ? new Date(a.birthDate).getTime() : 0;
        bValue = b.birthDate ? new Date(b.birthDate).getTime() : 0;
        break;
      case 'createdAt':
        aValue = new Date(a.createdAt).getTime();
        bValue = new Date(b.createdAt).getTime();
        break;
      case 'publicationCount':
        aValue = a.publicationCount || 0;
        bValue = b.publicationCount || 0;
        break;
      case 'name':
      default:
        // Default to name sorting
        aValue = a.name?.toLowerCase() || '';
        bValue = b.name?.toLowerCase() || '';
        break;
    }
    
    if (aValue < bValue) {
      return sortDirection === 'asc' ? -1 : 1;
    }
    if (aValue > bValue) {
      return sortDirection === 'asc' ? 1 : -1;
    }
    return 0;
  });
}
