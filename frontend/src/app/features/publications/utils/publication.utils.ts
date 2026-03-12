
/**
 * Utility functions for publication-related operations
 */

import { Author, Publication, PublicationStatus, Role } from "@app/shared/types";

/**
 * Get formatted author names from a publication
 */
export const getAuthorNames = (publication: Publication): string => {
  if (!publication.authors || publication.authors.length === 0) {
    return 'Unknown Authors';
  }
  return publication.authors.map(author => author.name).join(', ');
};

/**
 * Format a date string or Date object for display
 */
export const formatDate = (date: string | Date | undefined): string => {
  if (!date) return 'No date';
  const dateObj = typeof date === 'string' ? new Date(date) : date;
  return dateObj.toLocaleDateString();
};

/**
 * Format price for display
 */
export const getFormattedPrice = (price: number | undefined): string => {
  if (!price) return 'Free';
  return `$${price.toFixed(2)}`;
};

/**
 * Get author selection options for forms
 */
export const getAuthorSelectionOptions = (authors: Author[]) => {
  return authors.map(author => ({
    value: author.id,
    label: author.name
  }));
};

/**
 * Check if a publication can be edited by the current user
 */
export const canEditPublication = (
  publication: Publication, 
  currentUser: { id: number; role: Role } | null
): boolean => {
  if (!currentUser) return false;

  // Check if current user is one of the authors
  const isAuthor = publication.authors?.some(author => author.id === currentUser.id);
  
  // Authors can edit their own drafts
  if (currentUser.role.id === 4 && isAuthor) {
    return publication.status === PublicationStatus.DRAFT;
  }

  // Editors and above can edit any publication
  return currentUser.role.id <= 3; // Admin, Manager, Editor
};

/**
 * Check if a publication can be deleted by the current user
 */
export const canDeletePublication = (
  publication: Publication, 
  currentUser: { id: number; role: Role } | null
): boolean => {
  if (!currentUser) return false;

  // Check if current user is one of the authors
  const isAuthor = publication.authors?.some(author => author.id === currentUser.id);

  // Authors can delete their own drafts
  if (currentUser.role.id === 4 && isAuthor) {
    return publication.status === PublicationStatus.DRAFT;
  }

  // Managers and above can delete any publication
  return currentUser.role.id <= 2; // Admin, Manager
};

/**
 * Generate pagination pages array
 */
export const getPaginationPages = (currentPage: number, totalPages: number, maxVisible = 5): number[] => {
  const pages: number[] = [];
  
  let start = Math.max(0, currentPage - Math.floor(maxVisible / 2));
  let end = Math.min(totalPages - 1, start + maxVisible - 1);
  
  if (end - start + 1 < maxVisible) {
    start = Math.max(0, end - maxVisible + 1);
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  
  return pages;
};
