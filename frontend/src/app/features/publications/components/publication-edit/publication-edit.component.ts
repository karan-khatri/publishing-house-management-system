import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CreateUpdatePublicationRequest, PublicationService } from '../../services/publication.service';
import { AuthorService } from '@features/author/services/author.service';
import { NotificationService } from '@core/services/notification.service';
import { ButtonComponent, InputFormFieldComponent, SelectFormFieldComponent } from '@shared/components/ui';
import { 
  PUBLICATION_TYPE_OPTIONS, 
  PUBLICATION_STATUS_OPTIONS,
  DEFAULT_PUBLICATION_FORM
} from '../../constants';
import { Author, Publication } from '@app/shared/types';

@Component({
  selector: 'app-publication-edit',
  standalone: true,
  imports: [ReactiveFormsModule, ButtonComponent, InputFormFieldComponent, SelectFormFieldComponent],
  templateUrl: './publication-edit.component.html',
})
export class PublicationEditComponent implements OnInit {
  isLoading = false;
  isEditMode = false;
  publicationId: string | null = null;
  authors: Author[] = [];
  publicationForm!: FormGroup;

  // Server-side validation errors
  serverErrors: { [key: string]: string } = {};

  // Options for dropdowns - using constants
  readonly typeOptions = PUBLICATION_TYPE_OPTIONS;
  readonly statusOptions = PUBLICATION_STATUS_OPTIONS;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly formBuilder: FormBuilder,
    private readonly publicationService: PublicationService,
    private readonly authorService: AuthorService,
    private readonly notification: NotificationService
  ) {
    this.initializeForm();
  }

  ngOnInit(): void {
    this.loadAuthors();
    
    // Check if we have an ID parameter (edit mode) or not (create mode)
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id && id !== 'new') {
        this.publicationId = id;
        this.isEditMode = true;
        this.loadPublication();
      } else {
        this.isEditMode = false;
      }
    });
  }

  private initializeForm(): void {
    this.publicationForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(255)]],
      isbn: ['', [Validators.required, Validators.pattern(/^(?:\d{10}|\d{13})$/)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
      price: [null, [Validators.required, Validators.min(0)]],
      edition: ['1', [Validators.required, Validators.min(1)]],
      pages: [null, [Validators.required, Validators.min(1)]],
      type: [DEFAULT_PUBLICATION_FORM.type, [Validators.required]],
      status: [DEFAULT_PUBLICATION_FORM.status, [Validators.required]],
      authorIds: this.formBuilder.array([], [Validators.required, this.minLengthArray(1)])
    });
  }

  // Custom validator for FormArray minimum length
  private minLengthArray(min: number) {
    return (control: any) => {
      if (control instanceof FormArray) {
        return control.length >= min ? null : { minLengthArray: { requiredLength: min, actualLength: control.length } };
      }
      return null;
    };
  }

  get authorIdsFormArray(): FormArray {
    return this.publicationForm.get('authorIds') as FormArray;
  }

  loadAuthors(): void {
    this.authorService.getAuthors({limit: 100, page: 0, totalItems: 0}, {}).subscribe({
      next: (response) => {
        this.authors = response?.data.content;
      },
      error: (error) => {
        console.error('Error loading authors:', error);
        this.notification.error('Failed to load authors');
        this.authors = []; // Fallback to empty array
      }
    });
  }

  loadPublication(): void {
    if (!this.publicationId) return;

    this.isLoading = true;
    this.publicationService.getPublicationById(+this.publicationId).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.populateForm(response.data);
        } else {
          this.notification.error(response.message || 'Failed to load publication');
          this.router.navigate(['/publications']);
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error loading publication:', error);
        this.notification.error('Failed to load publication');
        this.router.navigate(['/publications']);
      }
    });
  }

  populateForm(publication: Publication): void {
    // Clear the authorIds FormArray first
    this.authorIdsFormArray.clear();
    
    // Add author IDs to the FormArray
    publication.authors?.forEach(author => {
      this.authorIdsFormArray.push(this.formBuilder.control(author.id));
    });

    // Patch the form with publication data
    this.publicationForm.patchValue({
      title: publication.title,
      isbn: publication.isbn || '',
      description: publication.description,
      price: publication.price || null,
      edition: String(publication.edition || '1'),
      pages: publication.pages || null,
      type: publication.type,
      status: publication.status
    });
  }

  /**
   * Handle API validation errors and apply them to form controls
   */
  private handleApiValidationErrors(errorResponse: any): void {
    
    if (errorResponse.error && typeof errorResponse.error === 'object') {
      this.serverErrors = errorResponse.error;
      
      // Apply server errors to form controls
      Object.keys(errorResponse.error).forEach(fieldName => {
        const control = this.publicationForm.get(fieldName);
        if (control) {
          // Set custom server error
          control.setErrors({ 
            ...control.errors, 
            serverError: errorResponse.error[fieldName] 
          });
          control.markAsTouched();
        }
      });

      // Show general notification
      this.notification.error(errorResponse.message || 'Please fix the validation errors below');
    } else {
      this.notification.error(errorResponse.message || 'Validation failed');
    }
  }

  /**
   * Clear all server-side validation errors
   */
  private clearServerErrors(): void {
    this.serverErrors = {};
    
    // Remove server errors from form controls
    Object.keys(this.publicationForm.controls).forEach(fieldName => {
      const control = this.publicationForm.get(fieldName);
      if (control?.errors) {
        delete control.errors['serverError'];
        
        // If no other errors remain, clear the errors completely
        if (Object.keys(control.errors).length === 0) {
          control.setErrors(null);
        }
      }
    });
  }

  /**
   * Get server error for a specific field
   */
  getServerError(fieldName: string): string | null {
    const control = this.publicationForm.get(fieldName);
    return control?.errors?.['serverError'] || null;
  }

  /**
   * Check if a field has server error
   */
  hasServerError(fieldName: string): boolean {
    return !!this.getServerError(fieldName);
  }

  onCancel(): void {
    this.router.navigate(['/publications']);
  }

  onSubmit(): void {
    if (this.publicationForm.invalid) {
      this.markFormGroupTouched();
      this.notification.warning('Please fill in all required fields correctly', 'Validation Error');
      return;
    }

    this.isLoading = true;
    this.clearServerErrors(); // Clear previous server errors

    if (this.isEditMode && this.publicationId) {
      this.updatePublication();
    } else {
      this.createPublication();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.publicationForm.controls).forEach(key => {
      const control = this.publicationForm.get(key);
      control?.markAsTouched();
      
      if (control instanceof FormArray) {
        control.controls.forEach(c => c.markAsTouched());
      }
    });
  }

  private createPublication(): void {
    const formValue = this.publicationForm.value;

    const createRequest: CreateUpdatePublicationRequest = {
      title: formValue.title,
      isbn: formValue.isbn || undefined,
      description: formValue.description,
      price: formValue.price || undefined,
      edition: formValue.edition || undefined,
      pages: formValue.pages || undefined,
      type: formValue.type,
      status: formValue.status,
      authorIds: formValue.authorIds || []
    };

    this.publicationService.createPublication(createRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.notification.success('Publication created successfully!');
          this.router.navigate(['/publications']);
        } else {
          this.handleApiValidationErrors(response);
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error creating publication:', error);
        
        // Handle validation errors from API response
        if (error.error && !error.error.success && error.error.error) {
          this.handleApiValidationErrors(error.error);
        } else {
          this.notification.error(error?.error?.message || 'Failed to create publication');
        }
      }
    });
  }

  private updatePublication(): void {
    if (!this.publicationId) return;

    const formValue = this.publicationForm.value;

    const updateRequest: CreateUpdatePublicationRequest = {
      title: formValue.title,
      isbn: formValue.isbn || undefined,
      description: formValue.description,
      price: formValue.price || undefined,
      edition: formValue.edition || undefined,
      pages: formValue.pages || undefined,
      type: formValue.type,
      status: formValue.status,
      authorIds: formValue.authorIds || []
    };

    this.publicationService.updatePublication(this.publicationId, updateRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.notification.success('Publication updated successfully!');
          this.router.navigate(['/publications']);
        } else {
          this.handleApiValidationErrors(response);
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error updating publication:', error);
        
        // Handle validation errors from API response
        if (error.error && !error.error.success && error.error.error) {
          this.handleApiValidationErrors(error.error);
        } else {
          this.notification.error('Failed to update publication');
        }
      }
    });
  }

  onAuthorChange(authorId: number, event: Event): void {
    const target = event.target as HTMLInputElement;
    const authorIdsArray = this.authorIdsFormArray;
    
    if (target.checked) {
      // Add author ID if not already present
      const existingIndex = authorIdsArray.value.indexOf(authorId);
      if (existingIndex === -1) {
        authorIdsArray.push(this.formBuilder.control(authorId));
      }
    } else {
      // Remove author ID
      const index = authorIdsArray.value.indexOf(authorId);
      if (index > -1) {
        authorIdsArray.removeAt(index);
      }
    }
  }

  // Helper methods for template validation
  isFieldInvalid(fieldName: string): boolean {
    const field = this.publicationForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  isAuthorSelected(authorId: number): boolean {
    return this.authorIdsFormArray.value.includes(authorId);
  }

  getPageTitle(): string {
    return this.isEditMode ? 'Edit Publication' : 'Create New Publication';
  }

  getSubmitButtonText(): string {
    return this.isEditMode ? 'Update Publication' : 'Create Publication';
  }

  /**
   * Get comprehensive form errors including server errors
   */
  getFormErrors(): { field: string; message: string }[] {
    const errors: { field: string; message: string }[] = [];
    
    if (this.publicationForm.invalid) {
      Object.keys(this.publicationForm.controls).forEach(key => {
        const control = this.publicationForm.get(key);
        if (control && control.invalid && control.touched) {
          const fieldName = this.getFieldDisplayName(key);
          
          // Check for server errors first
          if (control.errors?.['serverError']) {
            errors.push({ field: key, message: control.errors['serverError'] });
          }
          // Then check for client-side validation errors
          else if (control.errors?.['required']) {
            errors.push({ field: key, message: `${fieldName} is required` });
          }
          else if (control.errors?.['pattern']) {
            if (key === 'isbn') {
              errors.push({ field: key, message: `${fieldName} must be a valid 10 or 13 digit ISBN` });
            } else {
              errors.push({ field: key, message: `${fieldName} format is invalid` });
            }
          }
          else if (control.errors?.['minlength']) {
            errors.push({ field: key, message: `${fieldName} must be at least ${control.errors['minlength'].requiredLength} characters` });
          }
          else if (control.errors?.['maxlength']) {
            errors.push({ field: key, message: `${fieldName} must be no more than ${control.errors['maxlength'].requiredLength} characters` });
          }
          else if (control.errors?.['min']) {
            errors.push({ field: key, message: `${fieldName} must be at least ${control.errors['min'].min}` });
          }
          else if (control.errors?.['minLengthArray']) {
            errors.push({ field: key, message: `Please select at least ${control.errors['minLengthArray'].requiredLength} author(s)` });
          }
        }
      });
    }
    
    return errors;
  }

  private getFieldDisplayName(fieldName: string): string {
    const fieldNames: { [key: string]: string } = {
      'title': 'Title',
      'isbn': 'ISBN',
      'description': 'Description',
      'price': 'Price',
      'edition': 'Edition',
      'pages': 'Pages',
      'type': 'Type',
      'status': "Status",
      'authorIds': 'Authors'
    };
    return fieldNames[fieldName] || fieldName;
  }
}