import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./pages/appointment-list/appointment-list').then((m) => m.AppointmentListComponent),
  },
  {
    path: 'new',
    loadComponent: () => import('./pages/appointment-form/appointment-form').then((m) => m.AppointmentFormComponent),
  },
  {
    path: ':id',
    loadComponent: () => import('./pages/appointment-detail/appointment-detail').then((m) => m.AppointmentDetailComponent),
  },
] as Routes;
