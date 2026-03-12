import { environment } from '@environments/environment';

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: `${environment.apiUrl}/auth/login`,
    REGISTER: `${environment.apiUrl}/auth/register`,
    VALIDATE: `${environment.apiUrl}/auth/validate`
  },
  USERS: {
    ME: `${environment.apiUrl}/users/me`,
    CHANGE_PASSWORD: `${environment.apiUrl}/users/change-password`,
    GET_ALL: `${environment.apiUrl}/users`,
    GET_BY_ID: (id: number) => `${environment.apiUrl}/users/${id}`,
    GET_BY_ROLE: (roleId: number) => `${environment.apiUrl}/users/role/${roleId}`,
    UPDATE: (id: number) => `${environment.apiUrl}/users/${id}`,
    DELETE: (id: number) => `${environment.apiUrl}/users/${id}`
  },
  AUTHORS: `${environment.apiUrl}/authors`,
  PUBLICATIONS: {
    GET_ALL: `${environment.apiUrl}/publications`,
    LIST: `${environment.apiUrl}/publications`,
    DETAIL: `${environment.apiUrl}/publications`,
    GET_BY_ID: (id: string) => `${environment.apiUrl}/publications/${id}`,
    CREATE: `${environment.apiUrl}/publications`,
    UPDATE: (id: string) => `${environment.apiUrl}/publications/${id}`,
    DELETE: (id: string) => `${environment.apiUrl}/publications/${id}`,
    SEARCH: `${environment.apiUrl}/publications/search`,
    MY_PUBLICATIONS: `${environment.apiUrl}/publications/my-publications`,
    UPDATE_STATUS: `${environment.apiUrl}/publications`,
    CATEGORIES: `${environment.apiUrl}/publications/categories`,
    STATS: `${environment.apiUrl}/publications/stats`
  },
  EMPLOYEES: {
    GET_ALL: `${environment.apiUrl}/employees`,
    GET_BY_ID: (id: number) => `${environment.apiUrl}/employees/${id}`,
    CREATE: `${environment.apiUrl}/employees`,
    UPDATE: (id: number) => `${environment.apiUrl}/employees/${id}`,
    DELETE: (id: number) => `${environment.apiUrl}/employees/${id}`
  }
};
