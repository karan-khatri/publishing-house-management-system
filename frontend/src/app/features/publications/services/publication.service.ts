import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseApiService } from '@core/services/base-api.service';
import { Publication, ApiResponse, PagedApiResponse, Pagination, PublicationStatus, PublicationType } from '@shared/types';

export interface PublicationSearchFilters {
  title?: string;
  isbn?: string;
  type?: PublicationType;
  status?: PublicationStatus;
  authorId?: number;
  minPrice?: number;
  maxPrice?: number;
  sortBy?: 'title' | 'publicationDate' | 'price' | 'isbn';
  sortDir?: 'asc' | 'desc';
}


export interface CreateUpdatePublicationRequest {
  title: string;
  isbn?: string;
  description: string;
  price?: number;
  summary?: string;
  edition?: string;
  pages?: number;
  type: PublicationType;
  status?: PublicationStatus;
  authorIds: number[];
  publicationDate?: Date;
}

@Injectable({
  providedIn: 'root'
})
export class PublicationService extends BaseApiService<Publication, CreateUpdatePublicationRequest, Partial<CreateUpdatePublicationRequest>> {
  protected endpoint = '/publications';

  /**
   * Get publications with filtering
   */
  getPublications(
    pagination: Pagination,
    filters: PublicationSearchFilters = {}
  ): Observable<PagedApiResponse<Publication[]>> {
    return this.getList(pagination, filters);
  }

  /**
   * Get publication by ID
   */
  getPublicationById(id: number): Observable<ApiResponse<Publication>> {
    return this.getById(id);
  }

  /**
   * Create new publication
   */
  createPublication(publication: CreateUpdatePublicationRequest): Observable<ApiResponse<Publication>> {
    return this.create(publication);
  }

  /**
   * Update existing publication
   */
  updatePublication(id: string, publication: Partial<CreateUpdatePublicationRequest>): Observable<ApiResponse<Publication>> {
    return this.update(id, publication);
  }

  /**
   * Delete publication
   */
  deletePublication(id: number): Observable<ApiResponse<Publication>> {
    return this.delete(id);
  }

}