/**
 * =============================================================================
 * NAVIGATION UTILITIES
 * =============================================================================
 */

/**
 * Navigate to stats page (from dashboard.component.ts)
 */
export function navigateToStats(router: any): void {
  router.navigate(['/stats']);
}

/**
 * Navigate to publications page (from dashboard.component.ts)
 */
export function navigateToPublications(router: any): void {
  router.navigate(['/publications']);
}

/**
 * Navigate to authors page (from dashboard.component.ts)
 */
export function navigateToAuthors(router: any): void {
  router.navigate(['/authors']);
}

/**
 * Navigate to employees page (from dashboard.component.ts)
 */
export function navigateToEmployees(router: any): void {
  router.navigate(['/employees']);
}

/**
 * Generic navigation helper
 */
export function navigateToRoute(router: any, route: string): void {
  router.navigate([route]);
}

/**
 * Navigate back to employees list (from employee-edit.component.ts)
 */
export function navigateToEmployeesList(router: any): void {
  router.navigate(['/employees']);
}

/**
 * Navigate with error handling
 */
export function navigateWithErrorHandling(router: any, route: string, errorCallback?: () => void): void {
  try {
    router.navigate([route]);
  } catch (error) {
    console.error('Navigation error:', error);
    if (errorCallback) {
      errorCallback();
    }
  }
}