import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from '../services/auth.service';

const PUBLIC_AUTH_ENDPOINTS = ['/auth/login', '/auth/register', '/auth/refresh', '/auth/csrf'];
const RETRY_MARKER = 'X-MedBook-Retried';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const auth = inject(AuthService);

  return next(req).pipe(
    catchError((error) => {
      const isPublicAuthCall = PUBLIC_AUTH_ENDPOINTS.some((path) =>
        req.url.includes(`${environment.apiUrl}${path}`),
      );

      // Expired access cookie on a protected endpoint: try one silent refresh
      // (single-flight), then replay the original request. Never refresh for
      // the auth endpoints themselves to avoid loops.
      if (error.status === 401 && !isPublicAuthCall && !req.headers.has(RETRY_MARKER)) {
        return auth.refreshSession().pipe(
          switchMap(() => next(req.clone({ setHeaders: { [RETRY_MARKER]: 'true' } }))),
          catchError((refreshError) => {
            auth.logout();
            router.navigate(['/auth/login']);
            return throwError(() => refreshError);
          }),
        );
      }

      if (error.status === 401 && !isPublicAuthCall) {
        auth.logout();
        router.navigate(['/auth/login']);
      }

      const message = error.error?.message || error.message || 'An unexpected error occurred';
      console.error(`[HTTP Error] ${error.status}: ${message}`);
      return throwError(() => error);
    }),
  );
};
