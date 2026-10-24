import { Role } from '../models/user.model';

/**
 * Landing route per role. Single source of truth used by the login and
 * register flows, the root redirect, and any post-authentication navigation.
 */
export function homeForRole(role: Role | string | undefined | null): string {
  switch (role) {
    case Role.ADMIN:
      return '/admin';
    case Role.DOCTOR:
      return '/appointments';
    case Role.STAFF:
      return '/appointments';
    case Role.PATIENT:
      return '/dashboard';
    default:
      return '/dashboard';
  }
}
