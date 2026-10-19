import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

interface NavItem {
  label: string;
  route: string;
  icon: string;
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
      <nav class="flex-1 space-y-1 px-3 py-4">
        @for (item of navItems; track item.route) {
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
  protected readonly navItems: NavItem[] = [
    { label: 'Dashboard', route: '/dashboard', icon: '📊' },
    { label: 'Appointments', route: '/appointments', icon: '📅' },
    { label: 'Doctors', route: '/doctors', icon: '👨‍⚕️' },
    { label: 'Patients', route: '/patients', icon: '👤' },
    { label: 'Schedule', route: '/schedule', icon: '⏰' },
    { label: 'Medical Records', route: '/medical-records', icon: '📋' },
    { label: 'Prescriptions', route: '/prescriptions', icon: '💊' },
    { label: 'Payments', route: '/payments', icon: '💳' },
    { label: 'Notifications', route: '/notifications', icon: '🔔' },
    { label: 'Admin', route: '/admin', icon: '⚙️' },
  ];
}
