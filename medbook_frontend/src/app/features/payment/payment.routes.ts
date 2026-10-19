import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./pages/payment-list/payment-list').then((m) => m.PaymentListComponent),
  },
  {
    path: ':id',
    loadComponent: () => import('./pages/payment-detail/payment-detail').then((m) => m.PaymentDetailComponent),
  },
] as Routes;
