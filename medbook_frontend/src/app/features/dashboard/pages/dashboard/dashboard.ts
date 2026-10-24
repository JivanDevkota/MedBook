import { Component, inject } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <div class="space-y-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">
          Welcome back, {{ auth.currentUser()?.firstName }}!
        </h1>
        <p class="text-sm text-gray-500">
          You are signed in as
          <span class="font-semibold text-blue-600">{{ auth.currentUser()?.role }}</span>.
        </p>
      </div>
      <div class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4">
        <div class="rounded-xl border border-blue-200 bg-blue-50 p-6">
          <p class="text-sm text-blue-600">Total Appointments</p>
          <p class="mt-2 text-3xl font-bold text-blue-800">--</p>
        </div>
        <div class="rounded-xl border border-green-200 bg-green-50 p-6">
          <p class="text-sm text-green-600">Doctors</p>
          <p class="mt-2 text-3xl font-bold text-green-800">--</p>
        </div>
        <div class="rounded-xl border border-purple-200 bg-purple-50 p-6">
          <p class="text-sm text-purple-600">Patients</p>
          <p class="mt-2 text-3xl font-bold text-purple-800">--</p>
        </div>
        <div class="rounded-xl border border-amber-200 bg-amber-50 p-6">
          <p class="text-sm text-amber-600">Revenue</p>
          <p class="mt-2 text-3xl font-bold text-amber-800">--</p>
        </div>
      </div>
    </div>
  `,
})
export class DashboardComponent {
  protected readonly auth = inject(AuthService);
}
