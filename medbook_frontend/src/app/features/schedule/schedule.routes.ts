import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./pages/schedule/schedule').then((m) => m.ScheduleComponent),
  },
] as Routes;
