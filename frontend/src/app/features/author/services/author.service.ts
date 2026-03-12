import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseApiService } from '@core/services/base-api.service';
import { Author, ApiResponse, PagedApiResponse, Pagination } from '@shared/types';

export interface AuthorSearchFilters {
  query?: string;
  sortBy?: string;
  sortDir?: '' | 'asc' | 'desc';
}

export interface CreateUpdateAuthorRequest {
  name: string;
  email: string;
  birthDate: string;
  nationality: string;
  biography?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthorService extends BaseApiService<Author, CreateUpdateAuthorRequest, CreateUpdateAuthorRequest> {
  protected endpoint = '/authors';

  /**
   * Get authors with enhanced filtering
   */
  getAuthors(
    pagination: Pagination,
    filters: AuthorSearchFilters = {}
  ): Observable<PagedApiResponse<Author[]>> {
    return this.getList(pagination, filters);
  }

  /**
   * Get author by ID
   */
  getAuthorById(id: number): Observable<ApiResponse<Author>> {
    return this.getById(id);
  }

  /**
   * Create new author
   */
  createAuthor(author: CreateUpdateAuthorRequest): Observable<ApiResponse<Author>> {
    return this.create(author);
  }

  /**
   * Update existing author
   */
  updateAuthor(id: string, author: CreateUpdateAuthorRequest): Observable<ApiResponse<Author>> {
    return this.update(id, author);
  }

  /**
   * Delete author
   */
  deleteAuthor(id: number): Observable<ApiResponse<Author>> {
    return this.delete(id);
  }
}