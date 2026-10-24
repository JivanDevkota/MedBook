import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Role } from '../../core/models/user.model';

interface NavItem {
  label: string;
  route: string;
  icon: string;
  /** Roles that see this item; omitted means every role. */
  roles?: Role[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <aside class="flex w-64 flex-col border-r border-gray-200 bg-white">
      <div class="flex h-16 items-center justify-center border-b border-gray-200">
        <span class="text-xl font-bold text-blue-600">MedBook</span>
      </div>
      @if (auth.currentUser(); as user) {
        <div class="border-b border-gray-200 px-4 py-3">
          <p class="text-sm font-semibold text-gray-800">{{ user.firstName }} {{ user.lastName }}</p>
          <span class="text-xs font-medium text-blue-600">{{ user.role }}</span>
        </div>
      }
      <nav class="flex-1 space-y-1 px-3 py-4">
        @for (item of visibleNavItems(); track item.route) {
          <a
            [routerLink]="item.route"
            routerLinkActive="bg-blue-50 text-blue-700"
            class="flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 transition-colors"
          >
            <span class="text-lg">{{ item.icon }}</span>
            {{ item.label }}
          </a>
        }
      </nav>
    </aside>
  `,
})
export class SidebarComponent {
  protected readonly auth = inject(AuthService);

  protected readonly navItems: NavItem[] = [
    { label: 'Dashboard', route: '/dashboard', icon: '📊' },
    { label: 'Appointments', route: '/appointments', icon: '📅' },
    { label: 'Doctors', route: '/doctors', icon: '👨‍⚕️', roles: [Role.ADMIN, Role.STAFF, Role.PATIENT] },
    { label: 'Patients', route: '/patients', icon: '👤', roles: [Role.ADMIN, Role.STAFF, Role.DOCTOR] },
    { label: 'Schedule', route: '/schedule', icon: '⏰', roles: [Role.ADMIN, Role.DOCTOR, Role.STAFF] },
    { label: 'Medical Records', route: '/medical-records', icon: '📋', roles: [Role.ADMIN, Role.DOCTOR, Role.PATIENT] },
    { label: 'Prescriptions', route: '/prescriptions', icon: '💊', roles: [Role.ADMIN, Role.DOCTOR, Role.PATIENT] },
    { label: 'Payments', route: '/payments', icon: '💳', roles: [Role.ADMIN, Role.STAFF, Role.PATIENT] },
    { label: 'Files', route: '/files', icon: '📁', roles: [Role.ADMIN, Role.DOCTOR, Role.PATIENT] },
    { label: 'Notifications', route: '/notifications', icon: '🔔' },
    { label: 'Admin', route: '/admin', icon: '⚙️', roles: [Role.ADMIN] },
  ];

  protected visibleNavItems(): NavItem[] {
    const role = this.auth.currentUser()?.role;
    return this.navItems.filter((item) =>
      item.roles ? !!role && item.roles.includes(role) : true,
    );
  }
}
