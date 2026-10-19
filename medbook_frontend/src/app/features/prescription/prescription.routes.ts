import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./pages/prescription-list/prescription-list').then((m) => m.PrescriptionListComponent),
  },
  {
    path: ':id',
    loadComponent: () => import('./pages/prescription-detail/prescription-detail').then((m) => m.PrescriptionDetailComponent),
  },
] as Routes;
