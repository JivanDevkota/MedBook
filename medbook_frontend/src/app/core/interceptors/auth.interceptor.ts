import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Attaches CORS credentials (so the browser carries the HttpOnly auth
 * cookies) and implements the CSRF double-submit check: the XSRF-TOKEN
 * cookie value is echoed in the X-XSRF-TOKEN header on every mutating
 * request, exactly as the backend's CookieCsrfTokenRepository expects.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  req = req.clone({ withCredentials: true });

  const xsrfToken = readCookie('XSRF-TOKEN');
  if (xsrfToken && !['GET', 'HEAD', 'OPTIONS'].includes(req.method.toUpperCase())) {
    req = req.clone({ setHeaders: { 'X-XSRF-TOKEN': xsrfToken } });
  }

  return next(req);
};

function readCookie(name: string): string | null {
  const match = document.cookie
    .split('; ')
    .find((entry) => entry.startsWith(`${name}=`));
  return match ? decodeURIComponent(match.substring(name.length + 1)) : null;
}
