import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';

export interface RolePermissions {
  canViewPublications: boolean;
  canCreatePublications: boolean;
  canEditPublications: boolean;
  canDeletePublications: boolean;

  canViewAuthors: boolean;
  canCreateAuthors: boolean;
  canEditAuthors: boolean;
  canDeleteAuthors: boolean;

  canViewEmployees: boolean;
  canCreateEmployees: boolean;
  canEditEmployees: boolean;
  canDeleteEmployees: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  constructor(private readonly authService: AuthService) {}

  getUserPermissions(): RolePermissions {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      return this.getDefaultPermissions();
    }

    const role = currentUser.role.name.toUpperCase();


    switch (role) {
      case 'ADMIN':
        return {
          canViewPublications: true,
          canCreatePublications: true,
          canEditPublications: true,
          canDeletePublications: true,

          canViewAuthors: true,
          canCreateAuthors: true,
          canEditAuthors: true,
          canDeleteAuthors: true,

          canViewEmployees: true,
          canCreateEmployees: true,
          canEditEmployees: true,
          canDeleteEmployees: true
        };

      case 'MANAGER':
        return {
          canViewPublications: true,
          canCreatePublications: true,
          canEditPublications: true,
          canDeletePublications: false,

          canViewAuthors: true,
          canCreateAuthors: true,
          canEditAuthors: true,
          canDeleteAuthors: false,

          canViewEmployees: true,
          canCreateEmployees: true,
          canEditEmployees: true,
          canDeleteEmployees: false
        };

      case 'EDITOR':
        return {
          canViewPublications: true,
          canCreatePublications: true,
          canEditPublications: true,
          canDeletePublications: false,

          canViewAuthors: true,
          canCreateAuthors: true,
          canEditAuthors: true,
          canDeleteAuthors: false,

          canViewEmployees: false,
          canCreateEmployees: false,
          canEditEmployees: false,
          canDeleteEmployees: false
        };

      case 'USER':
        return {
          canViewPublications: true,
          canCreatePublications: false,
          canEditPublications: false,
          canDeletePublications: false,

          canViewAuthors: false,
          canCreateAuthors: false,
          canEditAuthors: false,
          canDeleteAuthors: false,

          canViewEmployees: false,
          canCreateEmployees: false,
          canEditEmployees: false,
          canDeleteEmployees: false
        };

      default:
        return this.getDefaultPermissions();
    }
  }

  private getDefaultPermissions(): RolePermissions {
    return {
      canViewPublications: false,
      canCreatePublications: false,
      canEditPublications: false,
      canDeletePublications: false,

      canViewAuthors: false,
      canCreateAuthors: false,
      canEditAuthors: false,
      canDeleteAuthors: false,

      canViewEmployees: false,
      canCreateEmployees: false,
      canEditEmployees: false,
      canDeleteEmployees: false
    };
  }

  hasPermission(permission: keyof RolePermissions): boolean {
    const permissions = this.getUserPermissions();
    return permissions[permission];
  }

  canAccess(resource: string, action: string): boolean {
    const permissionKey = `can${action}${resource}` as keyof RolePermissions;
    return this.hasPermission(permissionKey);
  }
}
