import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@app/core/services/auth.service';
import { PermissionService } from '@app/core/services/permission.service';
import { User } from '@app/shared/types';
import {
  DASHBOARD_FEATURE_CARDS,
  APP_ROUTES,
  validateUserSession,
  navigateToRoute,
} from '@app/shared/utils';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;
  featureCards = DASHBOARD_FEATURE_CARDS;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly permissionService: PermissionService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    validateUserSession(this.currentUser, this.router, APP_ROUTES.LOGIN);
  }

  navigateToPublications(): void {
    navigateToRoute(this.router, APP_ROUTES.PUBLICATIONS);
  }

  navigateToAuthors(): void {
    navigateToRoute(this.router, APP_ROUTES.AUTHORS);
  }

  navigateToEmployees(): void {
    navigateToRoute(this.router, APP_ROUTES.EMPLOYEES);
  }

  canViewCards(cardName: string): boolean {
    switch (cardName) {
      case 'Publications':
        return this.permissionService.hasPermission('canViewPublications');
      case 'Authors':
        return this.permissionService.hasPermission('canViewAuthors');
      case 'Employees':
        return this.permissionService.hasPermission('canViewEmployees');
      default:
        return false;
    }
  }

  canEditCards(cardName: string): boolean {
    switch (cardName) {
      case 'Publications':
        return this.permissionService.hasPermission('canEditPublications');
      case 'Authors':
        return this.permissionService.hasPermission('canEditAuthors');
      case 'Employees':
        return this.permissionService.hasPermission('canEditEmployees');
      default:
        return false;
    }
  }

}
