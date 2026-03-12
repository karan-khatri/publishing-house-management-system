import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from '@app/core/services/auth.service';
import { User } from '@app/shared/types';
import { PermissionService } from '@core/services/permission.service';


@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './main-layout.component.html',
})
export class MainLayoutComponent implements OnInit {
  currentUser: User | null = null;
  isUserMenuOpen = false;
  isMobileMenuOpen = false;

  constructor(
    private readonly authService: AuthService,
    private readonly permissionService: PermissionService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
  }

  toggleUserMenu(): void {
    this.isUserMenuOpen = !this.isUserMenuOpen;
    this.isMobileMenuOpen = false;
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
    this.isUserMenuOpen = false;
  }

  closeMenus(): void {
    this.isUserMenuOpen = false;
    this.isMobileMenuOpen = false;
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
    this.closeMenus();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getRoleDisplayName(): string {
    if (!this.currentUser?.role?.name) return 'User';

    const roleMap: { [key: string]: string } = {
      ADMIN: 'Administrator',
      EDITOR: 'Editor',
      MANAGER: 'Manager',
      USER: 'User'

    };
    return roleMap[this.currentUser.role.name] || this.currentUser.role.name;
  }

  canView(cardName: string): boolean {
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

  canEdit(cardName: string): boolean {
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

  getInitials(): string {
    if (!this.currentUser?.name) return 'U';
    return this.currentUser.name
      .split(' ')
      .map((n) => n.charAt(0))
      .join('')
      .toUpperCase()
      .substring(0, 2);
  }
}
