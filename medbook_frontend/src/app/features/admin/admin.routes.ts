import { Routes } from '@angular/router';
import { roleGuard } from '../../core/guards/role.guard';

export default [
  {
    path: '',
    canActivate: [roleGuard('ADMIN')],
    loadComponent: () => import('./pages/users/users').then((m) => m.UsersComponent),
  },
] as Routes;
