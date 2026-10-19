import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  template: `
    <header class="flex h-16 items-center justify-between border-b border-gray-200 bg-white px-6">
      <h1 class="text-lg font-semibold text-gray-800">MedBook</h1>
      <div class="flex items-center gap-4">
        <span class="text-sm text-gray-600">{{ auth.currentUser()?.firstName }} {{ auth.currentUser()?.lastName }}</span>
        <button (click)="logout()" class="rounded-lg px-3 py-1.5 text-sm text-red-600 hover:bg-red-50 transition-colors">
          Logout
        </button>
      </div>
    </header>
  `,
})
export class NavbarComponent {
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/auth/login']);
  }
}
