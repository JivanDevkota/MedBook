import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./pages/file-list/file-list').then((m) => m.FileListComponent),
  },
] as Routes;
