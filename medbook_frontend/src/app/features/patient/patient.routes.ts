import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./pages/patient-list/patient-list').then((m) => m.PatientListComponent),
  },
  {
    path: ':id',
    loadComponent: () => import('./pages/patient-detail/patient-detail').then((m) => m.PatientDetailComponent),
  },
] as Routes;
