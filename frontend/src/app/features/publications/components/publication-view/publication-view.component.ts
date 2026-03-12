import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PublicationService } from '../../services/publication.service';
import { NotificationService } from '@core/services/notification.service';
import { ButtonComponent } from '@shared/components/ui';
import { Publication } from '@app/shared/types';
import { AuthService } from '@app/core/services/auth.service';
import { PermissionService } from '@app/core/services/permission.service';

@Component({
  selector: 'app-publication-view',
  standalone: true,
  imports: [CommonModule, ButtonComponent],
  templateUrl: './publication-view.component.html',
})
export class PublicationViewComponent implements OnInit {
  isLoading = false;
  publicationId: string | null = null;
  publication: Publication | null = null;
  error: string | null = null;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly publicationService: PublicationService,
    private readonly permissionService: PermissionService,
    private readonly notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.publicationId = params.get('id');
      if (this.publicationId) {
        this.loadPublicationData(this.publicationId);
      } else {
        this.router.navigate(['/publications']);
      }
    });
  }

  loadPublicationData(publicationId: string): void {
    if (!publicationId) return;

    this.isLoading = true;
    this.error = null;

    this.publicationService.getPublicationById(+publicationId).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success && response.data) {
          this.publication = response.data;
        } else {
          this.error = response.message || 'Failed to load publication';
          this.notification.error(this.error);
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.error = 'Failed to load publication';
        console.error('Error loading publication:', error);
        this.notification.error(this.error);
      }
    });
  }

  onEdit(): void {
    if (this.publicationId) {
      this.router.navigate(['/publications', this.publicationId, 'edit']);
    }
  }

  onDelete(): void {
    if (!this.publication || !this.publicationId) return;

    if (!confirm(`Are you sure you want to delete "${this.publication.title}"?`)) {
      return;
    }

    this.publicationService.deletePublication(+this.publicationId).subscribe({
      next: (response) => {
        if (response.success) {
          this.notification.success(`Publication "${this.publication?.title}" deleted successfully`);
          this.router.navigate(['/publications']);
        } else {
          this.notification.error(response.message || 'Failed to delete publication');
        }
      },
      error: (error) => {
        console.error('Error deleting publication:', error);
        this.notification.error('Failed to delete publication');
      }
    });
  }

  onBackToList(): void {
    this.router.navigate(['/publications']);
  }

  // Utility methods
  canEditPublication(): boolean {
    return this.permissionService.hasPermission('canEditPublications');
  }

  canDeletePublication(): boolean {
    return this.permissionService.hasPermission('canDeletePublications');
  }

  getAuthorNames(): string {
    return this.publication?.authors?.map((author) => author.name).join(', ') || 'No authors';
  }

  formatDate(date: string | Date | undefined): string {
    if (!date) return 'Not specified';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  getFormattedPrice(): string {
    if (this.publication?.price === undefined || this.publication?.price === null) return 'Not specified';
    return `$${this.publication.price.toFixed(2)}`;
  }

  getStatusDisplayName(): string {
    if (!this.publication?.status) return 'Unknown';
    const statusMap: { [key: string]: string } = {
      DRAFT: 'Draft',
      PUBLISHED: 'Published',
      ARCHIVED: 'Archived',
      UNDER_REVIEW: 'Under Review'
    };
    return statusMap[this.publication.status] || this.publication.status;
  }

  getStatusClass(): string {
    if (!this.publication?.status) return 'bg-gray-100 text-gray-800';
    const statusClasses: { [key: string]: string } = {
      DRAFT: 'bg-yellow-100 text-yellow-800',
      PUBLISHED: 'bg-green-100 text-green-800',
      ARCHIVED: 'bg-gray-100 text-gray-800',
      UNDER_REVIEW: 'bg-blue-100 text-blue-800'
    };
    return statusClasses[this.publication.status] || 'bg-gray-100 text-gray-800';
  }

  getTypeDisplayName(): string {
    if (!this.publication?.type) return 'Unknown';
    const typeMap: { [key: string]: string } = {
      BOOK: 'Book',
      MAGAZINE: 'Magazine',
      JOURNAL: 'Journal',
      NEWSPAPER: 'Newspaper'
    };
    return typeMap[this.publication.type] || this.publication.type;
  }

  retry(): void {
    if (this.publicationId) {
      this.loadPublicationData(this.publicationId);
    }
  }
}