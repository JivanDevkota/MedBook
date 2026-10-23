import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from '../services/auth.service';

const PUBLIC_AUTH_ENDPOINTS = ['/auth/login', '/auth/register', '/auth/refresh'];

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const auth = inject(AuthService);

  return next(req).pipe(
    catchError((error) => {
      const isAuthCall = PUBLIC_AUTH_ENDPOINTS.some((path) => req.url.includes(`${environment.apiUrl}${path}`));
      if (error.status === 401 && !isAuthCall) {
        auth.logout();
        router.navigate(['/auth/login']);
      }
      const message = error.error?.message || error.message || 'An unexpected error occurred';
      console.error(`[HTTP Error] ${error.status}: ${message}`);
      return throwError(() => error);
    }),
  );
};
