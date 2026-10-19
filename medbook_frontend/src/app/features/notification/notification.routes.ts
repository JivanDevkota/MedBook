import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./pages/notification-list/notification-list').then((m) => m.NotificationListComponent),
  },
] as Routes;
