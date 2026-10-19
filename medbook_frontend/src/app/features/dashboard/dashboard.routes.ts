import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./pages/dashboard/dashboard').then((m) => m.DashboardComponent),
  },
] as Routes;
