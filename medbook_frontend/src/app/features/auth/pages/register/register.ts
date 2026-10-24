import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';
import { homeForRole } from '../../../../core/utils/role.utils';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="rounded-xl bg-white p-8 shadow-lg">
      <h2 class="mb-6 text-center text-2xl font-bold text-gray-800">Create Account</h2>
      <form #form="ngForm" (ngSubmit)="onSubmit()" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="mb-1 block text-sm font-medium text-gray-700">First Name</label>
            <input
              type="text" name="firstName" [(ngModel)]="firstName" required
              class="w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-blue-500 focus:outline-none"
            />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-gray-700">Last Name</label>
            <input
              type="text" name="lastName" [(ngModel)]="lastName" required
              class="w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-blue-500 focus:outline-none"
            />
          </div>
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">Email</label>
          <input
            type="email" name="email" [(ngModel)]="email" required
            class="w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-blue-500 focus:outline-none"
          />
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">Password</label>
          <input
            type="password" name="password" [(ngModel)]="password" required minlength="8"
            class="w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-blue-500 focus:outline-none"
          />
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">Phone <span class="text-gray-400">(optional)</span></label>
          <input
            type="tel" name="phone" [(ngModel)]="phone" placeholder="+94771234567"
            class="w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-blue-500 focus:outline-none"
          />
        </div>
        @if (error()) {
          <p class="text-sm text-red-600">{{ error() }}</p>
        }
        <button
          type="submit" [disabled]="loading()"
          class="w-full rounded-lg bg-blue-600 px-4 py-2 text-white font-medium hover:bg-blue-700 disabled:opacity-50 transition-colors"
        >
          {{ loading() ? 'Creating account...' : 'Register' }}
        </button>
      </form>
      <p class="mt-4 text-center text-sm text-gray-600">
        Already have an account?
        <a routerLink="/auth/login" class="text-blue-600 hover:underline">Sign In</a>
      </p>
    </div>
  `,
})
export class RegisterComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected firstName = '';
  protected lastName = '';
  protected email = '';
  protected password = '';
  protected phone = '';
  protected loading = signal(false);
  protected error = signal('');

  onSubmit(): void {
    this.loading.set(true);
    this.error.set('');
    this.auth
      .register({
        firstName: this.firstName,
        lastName: this.lastName,
        email: this.email,
        password: this.password,
        phone: this.phone || undefined,
      })
      .subscribe({
        next: (res) => this.router.navigateByUrl(homeForRole(res.user.role)),
        error: (err) => this.error.set(err.error?.message || 'Registration failed'),
        complete: () => this.loading.set(false),
      });
  }
}
