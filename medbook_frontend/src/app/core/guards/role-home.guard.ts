import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { homeForRole } from '../utils/role.utils';

/**
 * Root and catch-all redirect: sends each visitor to their role's home page,
 * or to the login page when no cookie session exists.
 */
export const roleHomeRedirect: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated()) {
    return router.parseUrl(homeForRole(auth.currentUser()?.role));
  }
  return router.parseUrl('/auth/login');
};
