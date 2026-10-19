import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./pages/medical-record-list/medical-record-list').then((m) => m.MedicalRecordListComponent),
  },
  {
    path: ':id',
    loadComponent: () => import('./pages/medical-record-detail/medical-record-detail').then((m) => m.MedicalRecordDetailComponent),
  },
] as Routes;
