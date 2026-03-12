import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthorService } from '../../services/author.service';
import { PublicationSearchFilters, PublicationService } from '../../../publications/services/publication.service';
import { NotificationService } from '@core/services/notification.service';
import { ButtonComponent } from '@shared/components/ui';
import { Author, Pagination, Publication } from '@app/shared/types';
import { PermissionService } from '@app/core/services/permission.service';


@Component({
  selector: 'app-author-view',
  standalone: true,
  imports: [ButtonComponent],
  templateUrl: './author-view.component.html',
})
export class AuthorViewComponent implements OnInit {
  isLoading = false;
  publicationsLoading = false;
  authorId: string | null = null;
  author: Author | null = null;
  publications: Publication[] = [];
  error: string | null = null;
  publicationsError: string | null = null;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly authorService: AuthorService,
    private readonly permissionService: PermissionService,
    private readonly publicationService: PublicationService,
    private readonly notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.authorId = params.get('id');
      if (this.authorId) {
        this.loadAuthorData(this.authorId);
        this.loadAuthorPublications(this.authorId);
      } else {
        this.router.navigate(['/authors']);
      }
    });
  }

  loadAuthorData(authorId: string): void {
    if (!authorId) return;

    this.isLoading = true;
    this.error = null;

    this.authorService.getAuthorById(+authorId).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success && response.data) {
          this.author = response.data;
        } else {
          this.error = response.message || 'Failed to load author';
          this.notification.error(this.error);
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.error = 'Failed to load author';
        console.error('Error loading author:', error);
        this.notification.error(this.error);
      }
    });
  }

  loadAuthorPublications(authorId: string): void {
    if (!authorId) return;

    this.publicationsLoading = true;
    this.publicationsError = null;

    // Create pagination object
    const pagination: Pagination = {
      page: 0,
      limit: 50, // Load up to 50 publications
      totalItems: 0
    };

    // Create search filters with authorId
    const filters: PublicationSearchFilters = {
      authorId: +authorId,
      sortBy: 'title',
      sortDir: 'desc'
    };

    this.publicationService.getPublications(pagination, filters).subscribe({
      next: (response) => {
        this.publicationsLoading = false;
        if (response.success && response.data) {
          this.publications = response.data.content;
        } else {
          this.publicationsError = response.message || 'Failed to load publications';
        }
      },
      error: (error) => {
        this.publicationsLoading = false;
        this.publicationsError = 'Failed to load publications';
        console.error('Error loading publications:', error);
      }
    });
  }

  onEdit(): void {
    if (this.authorId) {
      this.router.navigate(['/authors', this.authorId, 'edit']);
    }
  }

  onDelete(): void {
    if (!this.author || !this.authorId) return;

    if (!confirm(`Are you sure you want to delete "${this.author.name}"?`)) {
      return;
    }

    this.authorService.deleteAuthor(+this.authorId).subscribe({
      next: (response) => {
        if (response.success) {
          this.notification.success(`Author "${this.author?.name}" deleted successfully`);
          this.router.navigate(['/authors']);
        } else {
          this.notification.error(response.message || 'Failed to delete author');
        }
      },
      error: (error) => {
        console.error('Error deleting author:', error);
        this.notification.error('Failed to delete author');
      }
    });
  }

  onBackToList(): void {
    this.router.navigate(['/authors']);
  }

  onViewPublication(publicationId: number): void {
    this.router.navigate(['/publications', publicationId]);
  }

  onCreatePublication(): void {
    this.router.navigate(['/publications/new']);
  }

  formatDate(date: string | Date | undefined): string {
    if (!date) return 'Not specified';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  formatShortDate(date: string | Date | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short'
    });
  }

  calculateAge(birthDate: string | Date | undefined): string {
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

  getFormattedPrice(price: number | undefined): string {
    if (price === undefined || price === null) return 'Free';
    return `$${price.toFixed(2)}`;
  }

  getStatusDisplayName(status: string): string {
    const statusMap: { [key: string]: string } = {
      DRAFT: 'Draft',
      PUBLISHED: 'Published',
      ARCHIVED: 'Archived',
      UNDER_REVIEW: 'Under Review'
    };
    return statusMap[status] || status;
  }

  getStatusClass(status: string): string {
    const statusClasses: { [key: string]: string } = {
      DRAFT: 'bg-yellow-100 text-yellow-800',
      PUBLISHED: 'bg-green-100 text-green-800',
      ARCHIVED: 'bg-gray-100 text-gray-800',
      UNDER_REVIEW: 'bg-blue-100 text-blue-800'
    };
    return statusClasses[status] || 'bg-gray-100 text-gray-800';
  }

  getTypeDisplayName(type: string): string {
    const typeMap: { [key: string]: string } = {
      BOOK: 'Book',
      MAGAZINE: 'Magazine',
      JOURNAL: 'Journal',
      NEWSPAPER: 'Newspaper'
    };
    return typeMap[type] || type;
  }

  retry(): void {
    if (this.authorId) {
      this.loadAuthorData(this.authorId);
      this.loadAuthorPublications(this.authorId);
    }
  }

  retryPublications(): void {
    if (this.authorId) {
      this.loadAuthorPublications(this.authorId);
    }
  }

  canEditAuthor(): boolean {
    return this.permissionService.hasPermission('canEditAuthors');
  }

  canDeleteAuthor(): boolean {
    return this.permissionService.hasPermission('canDeleteAuthors');
  }
}