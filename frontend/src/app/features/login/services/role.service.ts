import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { ApiResponse, Role } from '@app/shared/types';

@Injectable({
  providedIn: 'root',
})
export class RoleService {
  constructor(private readonly http: HttpClient) {}

  private readonly apiUrl = `${environment.apiUrl}/roles`;

  getRoleById(id: number): Observable<ApiResponse<Role>> {
    return this.http.get<ApiResponse<Role>>(`${this.apiUrl}/${id}`);
  }

  getRoles(): Observable<ApiResponse<Role[]>> {
    return this.http.get<ApiResponse<Role[]>>(this.apiUrl);
  }
}
