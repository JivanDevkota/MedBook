import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard factory: roleGuard('ADMIN') returns a CanActivateFn that only lets
 * users whose role is in the allowed list through the route.
 */
export const roleGuard = (...allowedRoles: string[]): CanActivateFn => {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    const user = auth.currentUser();
    if (user && allowedRoles.includes(user.role)) return true;
    return router.parseUrl('/');
  };
};
