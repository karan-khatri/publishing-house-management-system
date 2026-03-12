/**
 * =============================================================================
 * PAGINATION UTILITIES
 * =============================================================================
 * Extracted from author.utils.ts
 */

/**
 * Generate pagination page numbers for display (from author.utils.ts)
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