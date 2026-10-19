import { Routes } from '@angular/router';
import { roleGuard } from '../../core/guards/role.guard';

export default [
  {
    path: '',
    canActivate: [() => roleGuard('ADMIN')],
    loadComponent: () => import('./pages/admin-dashboard/admin-dashboard').then((m) => m.AdminDashboardComponent),
  },
] as Routes;
