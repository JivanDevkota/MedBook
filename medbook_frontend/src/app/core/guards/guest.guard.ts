import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { homeForRole } from '../utils/role.utils';

/**
 * Lets only unauthenticated users through (login/register). An
 * already-authenticated user is sent straight to their role's home page.
 */
export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isAuthenticated()) return true;
  return router.parseUrl(homeForRole(auth.currentUser()?.role));
};
