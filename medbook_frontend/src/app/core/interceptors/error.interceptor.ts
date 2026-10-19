import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const auth = inject(AuthService);

  return next(req).pipe(
    catchError((error) => {
      if (error.status === 401) {
        auth.logout();
        router.navigate(['/auth/login']);
      }
      const message = error.error?.message || error.message || 'An unexpected error occurred';
      console.error(`[HTTP Error] ${error.status}: ${message}`);
      return throwError(() => error);
    }),
  );
};
