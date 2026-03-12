import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { ApiResponse, PagedApiResponse, Pagination } from '@shared/types';

@Injectable({
  providedIn: 'root'
})
export abstract class BaseApiService<T, TCreate = Partial<T>, TUpdate = Partial<T>> {
  protected abstract endpoint: string;

  constructor(protected readonly http: HttpClient) {}

  protected get apiUrl(): string {
    return `${environment.apiUrl}${this.endpoint}`;
  }

  /**
   * Get entity by ID
   */
  getById(id: number): Observable<ApiResponse<T>> {
    return this.http.get<ApiResponse<T>>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get paginated list with filters
   */
  getList<TFilters = any>(
    pagination: Pagination,
    filters: TFilters = {} as TFilters
  ): Observable<PagedApiResponse<T[]>> {
    const params = this.buildParams(pagination, filters);
    return this.http.get<PagedApiResponse<T[]>>(this.apiUrl, { params });
  }

  /**
   * Create new entity
   */
  create(data: TCreate): Observable<ApiResponse<T>> {
    return this.http.post<ApiResponse<T>>(this.apiUrl, data);
  }

  /**
   * Update existing entity
   */
  update(id: number | string, data: TUpdate): Observable<ApiResponse<T>> {
    return this.http.patch<ApiResponse<T>>(`${this.apiUrl}/${id}`, data);
  }

  /**
   * Delete entity
   */
  delete(id: number | string): Observable<ApiResponse<T>> {
    return this.http.delete<ApiResponse<T>>(`${this.apiUrl}/${id}`);
  }

  /**
   * Build HTTP params from pagination and filters
   */
  protected buildParams(pagination: Pagination, filters: any): HttpParams {
    let params = new HttpParams()
      .set('page', pagination.page.toString())
      .set('limit', pagination.limit.toString());

    // Add filters
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(key, value.toString());
      }
    });

    return params;
  }
}