import { Routes } from '@angular/router';
import { AuthGuard } from '@core/guards/auth.guard';
import { RoleGuard } from '@core/guards/role.guard';
import { MainLayoutComponent } from '@shared/components/ui';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('@app/features/login/components/login.component').then((m) => m.LoginComponent),
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
      {
        path: 'publications',
        loadComponent: () =>
          import(
            './features/publications/components/publication-list/publication-list.component'
          ).then((m) => m.PublicationListComponent),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'MANAGER', 'EDITOR', 'USER'] },
      },
      {
        path: 'publications/new',
        loadComponent: () =>
          import(
            './features/publications/components/publication-edit/publication-edit.component'
          ).then((m) => m.PublicationEditComponent),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'EDITOR'] },
      },
      {
        path: 'publications/:id',
        loadComponent: () =>
          import(
            './features/publications/components/publication-view/publication-view.component'
          ).then((m) => m.PublicationViewComponent),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'MANAGER', 'EDITOR', 'USER'] },
      },
      {
        path: 'publications/:id/edit',
        loadComponent: () =>
          import(
            './features/publications/components/publication-edit/publication-edit.component'
          ).then((m) => m.PublicationEditComponent),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'MANAGER', 'EDITOR'] },
      },

      // Author Routes - ADMIN and EDITOR access
      {
        path: 'authors',
        loadComponent: () =>
          import('./features/author/components/author-list/author-list.component').then(
            (m) => m.AuthorListComponent
          ),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'MANAGER', 'EDITOR', 'USER'] },
      },
      {
        path: 'authors/new',
        loadComponent: () =>
          import('./features/author/components/author-edit/author-edit.component').then(
            (m) => m.AuthorEditComponent
          ),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'MANAGER', 'EDITOR'] },
      },
      {
        path: 'authors/:id',
        loadComponent: () =>
          import('./features/author/components/author-view/author-view.component').then(
            (m) => m.AuthorViewComponent
          ),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'MANAGER', 'EDITOR', 'USER'] },
      },
      {
        path: 'authors/:id/edit',
        loadComponent: () =>
          import('./features/author/components/author-edit/author-edit.component').then(
            (m) => m.AuthorEditComponent
          ),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'MANAGER', 'EDITOR'] },
      },

      // Employee Routes - ADMIN only
      {
        path: 'employees',
        loadComponent: () =>
          import('./features/employees/components/employee-list/employee-list.component').then(
            (m) => m.EmployeeListComponent
          ),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] },
      },
      {
        path: 'employees/new',
        loadComponent: () =>
          import('./features/employees/components/employee-edit/employee-edit.component').then(
            (m) => m.EmployeeEditComponent
          ),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] },
      },
      {
        path: 'employees/:id',
        loadComponent: () =>
          import('./features/employees/components/employee-view/employee-view.component').then(
            (m) => m.EmployeeViewComponent
          ),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] },
      },
      {
        path: 'employees/:id/edit',
        loadComponent: () =>
          import('./features/employees/components/employee-edit/employee-edit.component').then(
            (m) => m.EmployeeEditComponent
          ),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] },
      },
    ],
  },

  // Publication Routes - EDITOR and ADMIN access

  { path: '**', redirectTo: '/dashboard' },
];
