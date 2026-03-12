/**
 * =============================================================================
 * VALIDATION UTILITIES
 * =============================================================================
 * Extracted from author.utils.ts and author-edit.component.ts
 */

/**
 * Check if author has any publications (from author.utils.ts)
 */
export function hasPublications(author: { publicationCount?: number }): boolean {
  return (author.publicationCount || 0) > 0;
}

/**
 * Get user-friendly field name for validation messages (from author-edit.component.ts)
 */
// Update your existing getFieldDisplayName function in validation.utils.ts:

/**
 * Get user-friendly field name for validation messages (enhanced from multiple components)
 */
export function getFieldDisplayName(fieldName: string): string {
  const fieldNames: { [key: string]: string } = {
    // Author fields
    name: 'Name',
    email: 'Email Address',
    nationality: 'Nationality',
    biography: 'Biography',
    birthDate: 'Birth Date',
    
    // Employee fields (from employee-edit.component.ts)
    password: 'Password',
    position: 'Position',
    department: 'Department',
    phone: 'Phone',
    employeeId: 'Employee ID',
    roleId: 'Role',
    address: 'Address',
    
    // Publication fields (future use)
    title: 'Title',
    description: 'Description',
    price: 'Price',
    isbn: 'ISBN',
    authorIds: 'Authors',
    publicationDate: 'Publication Date',
    edition: 'Edition',
    pages: 'Pages',
    type: 'Publication Type',
    status: 'Status'
  };
  return fieldNames[fieldName] || fieldName;
}

/**
 * Check if user is authenticated (from dashboard.component.ts)
 */
export function isUserAuthenticated(user: any): boolean {
  return user !== null && user !== undefined;
}

/**
 * Validate user session and redirect if needed
 */
export function validateUserSession(user: any, router: any, redirectRoute: string = '/login'): boolean {
  if (!isUserAuthenticated(user)) {
    router.navigate([redirectRoute]);
    return false;
  }
  return true;
}

/**
 * Get form validation errors (from employee-edit.component.ts)
 */
export function getFormErrors(form: any): { field: string; message: string }[] {
  const errors: { field: string; message: string }[] = [];
  
  if (form.invalid) {
    Object.keys(form.controls).forEach(key => {
      const control = form.get(key);
      if (control && control.invalid && control.touched) {
        const fieldName = getFieldDisplayName(key);
        if (control.errors?.['required']) {
          errors.push({ field: key, message: `${fieldName} is required` });
        }
        if (control.errors?.['email']) {
          errors.push({ field: key, message: `${fieldName} must be a valid email address` });
        }
        if (control.errors?.['minlength']) {
          errors.push({ field: key, message: `${fieldName} must be at least ${control.errors['minlength'].requiredLength} characters` });
        }
        if (control.errors?.['maxlength']) {
          errors.push({ field: key, message: `${fieldName} must not exceed ${control.errors['maxlength'].requiredLength} characters` });
        }
      }
    });
  }
  
  return errors;
}

/**
 * Check if form is valid and show notification if not (from employee-edit.component.ts)
 */
export function validateFormAndNotify(form: any, notificationService: any): boolean {
  if (form.invalid) {
    notificationService.warning('Please fill in all required fields correctly', 'Validation Error');
    return false;
  }
  return true;
}

/**
 * Validate email format (enhanced)
 */
export function isValidEmail(email: string): boolean {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

/**
 * Validate phone number format
 */
export function isValidPhone(phone: string): boolean {
  const cleanPhone = phone.replace(/[\s-()]/g, '');
  return cleanPhone.length >= 10 && cleanPhone.length <= 15 && /^\d+$/.test(cleanPhone);
}

/**
 * Validate password strength
 */
export function isValidPassword(password: string): boolean {
  return password.length >= 8;
}