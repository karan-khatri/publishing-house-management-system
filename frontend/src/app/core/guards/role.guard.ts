import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { NotificationService } from '@core/services/notification.service';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly notification: NotificationService
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const currentUser = this.authService.getCurrentUser();
    
    if (!currentUser) {
      this.router.navigate(['/login']);
      return false;
    }

    const requiredRoles = route.data['roles'] as string[];
    
    if (!requiredRoles || requiredRoles.length === 0) {
      return true; // No role restriction
    }

    const userRole = currentUser.role.name;
    const hasAccess = requiredRoles.includes(userRole);

    if (!hasAccess) {
      this.notification.error('Access denied. Insufficient permissions.');
      this.router.navigate(['/dashboard']);
      return false;
    }

    return true;
  }
}