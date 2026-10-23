import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes'),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadChildren: () => import('./features/dashboard/dashboard.routes'),
  },
  {
    path: 'appointments',
    canActivate: [authGuard],
    loadChildren: () => import('./features/appointment/appointment.routes'),
  },
  {
    path: 'doctors',
    canActivate: [authGuard],
    loadChildren: () => import('./features/doctor/doctor.routes'),
  },
  {
    path: 'patients',
    canActivate: [authGuard],
    loadChildren: () => import('./features/patient/patient.routes'),
  },
  {
    path: 'schedule',
    canActivate: [authGuard],
    loadChildren: () => import('./features/schedule/schedule.routes'),
  },
  {
    path: 'medical-records',
    canActivate: [authGuard],
    loadChildren: () => import('./features/medical-record/medical-record.routes'),
  },
  {
    path: 'prescriptions',
    canActivate: [authGuard],
    loadChildren: () => import('./features/prescription/prescription.routes'),
  },
  {
    path: 'payments',
    canActivate: [authGuard],
    loadChildren: () => import('./features/payment/payment.routes'),
  },
  {
    path: 'notifications',
    canActivate: [authGuard],
    loadChildren: () => import('./features/notification/notification.routes'),
  },
  {
    path: 'files',
    canActivate: [authGuard],
    loadChildren: () => import('./features/file/file.routes'),
  },
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard('ADMIN')],
    loadChildren: () => import('./features/admin/admin.routes'),
  },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: '/dashboard' },
];
