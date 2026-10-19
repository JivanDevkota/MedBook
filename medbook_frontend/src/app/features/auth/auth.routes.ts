import { Routes } from '@angular/router';
import { AuthLayoutComponent } from '../../layouts/auth-layout/auth-layout';

export default [
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'login', loadComponent: () => import('./pages/login/login').then((m) => m.LoginComponent) },
      { path: 'register', loadComponent: () => import('./pages/register/register').then((m) => m.RegisterComponent) },
      { path: '', redirectTo: 'login', pathMatch: 'full' },
    ],
  },
] as Routes;
