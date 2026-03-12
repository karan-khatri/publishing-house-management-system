import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthorService, CreateUpdateAuthorRequest } from '../../services/author.service';
import { NotificationService } from '@core/services/notification.service';
import { ButtonComponent, InputFormFieldComponent } from '@shared/components/ui';
import { Author } from '@app/shared/types';
import { formatDateForApi, formatDateForInput } from '@app/shared/utils';

@Component({
  selector: 'app-author-edit',
  standalone: true,
  imports: [ReactiveFormsModule, ButtonComponent, InputFormFieldComponent],
  templateUrl: './author-edit.component.html',
})
export class AuthorEditComponent implements OnInit {
  isLoading = false;
  isEditMode = false;
  authorId: string | null = null;
  authorForm!: FormGroup;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly formBuilder: FormBuilder,
    private readonly authorService: AuthorService,
    private readonly notification: NotificationService
  ) {
    this.initializeForm(route.snapshot.paramMap.get('id') !== 'new');
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.authorId = params.get('id');
      if (this.authorId && this.authorId !== 'new') {
        this.isEditMode = true;
        this.loadAuthorData(this.authorId);
      }
    });
  }

  private initializeForm(isEditMode: boolean): void {
    this.authorForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      nationality: ['', [Validators.required]],
      biography: ['', [Validators.maxLength(1000)]],
      birthDate: ['', [Validators.required]],
    });
  }

  populateForm(author: Author): void {
    this.authorForm.patchValue({
      name: author.name,
      email: author.email,
      nationality: author.nationality,
      biography: author.biography || '',
      birthDate: author.birthDate ? formatDateForInput(author.birthDate) : '',
    });
  }

  loadAuthorData(authorId: string): void {
    if (!authorId) return;

    this.isLoading = true;
    this.authorService.getAuthorById(+authorId).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success && response.data) {
          this.populateForm(response.data);
        } else {
          this.notification.error(response.message || 'Failed to load author');
          this.router.navigate(['/authors']);
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error loading author:', error);
        this.notification.error('Failed to load author');
        this.router.navigate(['/authors']);
      },
    });
  }

  createAuthor(): void {
    if (this.authorForm.invalid) {
      this.notification.warning('Please fill in all required fields correctly', 'Validation Error');
      return;
    }

    this.isLoading = true;
    const formValue = this.authorForm.value;
    const createRequest: CreateUpdateAuthorRequest = {
      name: formValue.name,
      email: formValue.email,
      nationality: formValue.nationality,
      biography: formValue.biography,
      birthDate: formatDateForApi(formValue.birthDate),
    };

    this.authorService.createAuthor(createRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.notification.success('Author created successfully');
          this.router.navigate(['/authors']);
        } else {
          this.notification.error(response.message || 'Failed to create author');
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error creating author:', error);
        this.notification.error(error?.error?.message ?? 'Failed to create author');
      },
    });
  }

  updateAuthor(): void {
    if (this.authorForm.invalid) {
      this.notification.warning('Please fill in all required fields correctly', 'Validation Error');
      return;
    }

    this.isLoading = true;
    const formValue = this.authorForm.value;
    const updateRequest: CreateUpdateAuthorRequest = {
      name: formValue.name,
      email: formValue.email,
      nationality: formValue.nationality,
      biography: formValue.biography,
      birthDate: formatDateForApi(formValue.birthDate),
    };

    this.authorService.updateAuthor(this.authorId ?? '', updateRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.notification.success('Author updated successfully');
          this.router.navigate(['/authors']);
        } else {
          this.notification.error(response.message || 'Failed to update author');
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error updating author:', error);
        this.notification.error('Failed to update author');
      },
    });
  }

  onSubmit(): void {
    if (this.authorForm.invalid) {
      this.authorForm.markAllAsTouched();
      this.notification.warning('Please fill in all required fields correctly', 'Validation Error');
      return;
    }

    if (this.isEditMode) {
      this.updateAuthor();
    } else {
      this.createAuthor();
    }
  }

  onCancel(): void {
    this.router.navigate(['/authors']);
  }

  getFormErrors(): { field: string; message: string }[] {
    const errors: { field: string; message: string }[] = [];

    if (this.authorForm.invalid) {
      Object.keys(this.authorForm.controls).forEach((key) => {
        const control = this.authorForm.get(key);
        if (control && control.invalid && control.touched) {
          const fieldName = this.getFieldDisplayName(key);
          if (control.errors?.['required']) {
            errors.push({ field: key, message: `${fieldName} is required` });
          }
          if (control.errors?.['email']) {
            errors.push({ field: key, message: `${fieldName} must be a valid email address` });
          }
          if (control.errors?.['minlength']) {
            errors.push({
              field: key,
              message: `${fieldName} must be at least ${control.errors['minlength'].requiredLength} characters`,
            });
          }
          if (control.errors?.['maxlength']) {
            errors.push({
              field: key,
              message: `${fieldName} must be no more than ${control.errors['maxlength'].requiredLength} characters`,
            });
          }
        }
      });
    }

    return errors;
  }

  private getFieldDisplayName(fieldName: string): string {
    const fieldNames: { [key: string]: string } = {
      name: 'Name',
      email: 'Email Address',
      nationality: 'Nationality',
      biography: 'Biography',
      birthDate: 'Birth Date',
    };
    return fieldNames[fieldName] || fieldName;
  }


  // Getter for page title
  get pageTitle(): string {
    return this.isEditMode ? 'Edit Author' : 'Create New Author';
  }

  // Getter for submit button text
  get submitButtonText(): string {
    return this.isEditMode ? 'Update Author' : 'Create Author';
  }
}
