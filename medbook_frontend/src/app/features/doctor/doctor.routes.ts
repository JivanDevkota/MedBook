import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./pages/doctor-list/doctor-list').then((m) => m.DoctorListComponent),
  },
  {
    path: ':id',
    loadComponent: () => import('./pages/doctor-detail/doctor-detail').then((m) => m.DoctorDetailComponent),
  },
] as Routes;
