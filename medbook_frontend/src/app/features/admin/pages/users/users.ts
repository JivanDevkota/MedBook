import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../../core/services/api.service';
import { AuthService } from '../../../../core/services/auth.service';
import { API } from '../../../../core/constants/api.constants';
import { User, Role } from '../../../../core/models/user.model';
import { PaginatedResponse } from '../../../../core/models/pagination.model';

interface CreateUserForm {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phone: string;
  role: Role;
  licenseNumber: string;
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [FormsModule, DatePipe],
  template: `
    <div class="space-y-6">
      <div class="flex items-center justify-between">
        <h1 class="text-2xl font-bold text-gray-800">User Management</h1>
        <button
          (click)="showCreateForm.set(!showCreateForm())"
          class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 transition-colors"
        >
          {{ showCreateForm() ? 'Cancel' : '+ Add User' }}
        </button>
      </div>

      @if (error()) {
        <div class="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{{ error() }}</div>
      }
      @if (notice()) {
        <div class="rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">{{ notice() }}</div>
      }

      @if (showCreateForm()) {
        <div class="rounded-xl border border-gray-200 bg-white p-6">
          <h2 class="mb-4 text-lg font-semibold text-gray-800">Create User</h2>
          <form #form="ngForm" (ngSubmit)="createUser()" class="grid grid-cols-1 gap-4 md:grid-cols-2">
            <input type="text" name="firstName" placeholder="First name" [(ngModel)]="createForm.firstName" required
              class="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none" />
            <input type="text" name="lastName" placeholder="Last name" [(ngModel)]="createForm.lastName" required
              class="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none" />
            <input type="email" name="email" placeholder="Email" [(ngModel)]="createForm.email" required
              class="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none" />
            <input type="password" name="password" placeholder="Password (min 8 chars)" [(ngModel)]="createForm.password" required minlength="8"
              class="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none" />
            <input type="tel" name="phone" placeholder="Phone (optional, e.g. +94771234567)" [(ngModel)]="createForm.phone"
              class="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none" />
            <select name="role" [(ngModel)]="createForm.role" required
              class="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none">
              @for (role of roles; track role) {
                <option [value]="role">{{ role }}</option>
              }
            </select>
            @if (createForm.role === 'DOCTOR') {
              <input type="text" name="licenseNumber" placeholder="Medical license number" [(ngModel)]="createForm.licenseNumber" required
                class="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none md:col-span-2" />
            }
            <div class="md:col-span-2">
              <button type="submit" [disabled]="creating() || form.invalid"
                class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50 transition-colors">
                {{ creating() ? 'Creating...' : 'Create User' }}
              </button>
            </div>
          </form>
        </div>
      }

      <div class="rounded-xl border border-gray-200 bg-white">
        <div class="border-b border-gray-200 p-4">
          <input
            type="text"
            placeholder="Search by name or email..."
            [ngModel]="searchTerm()"
            (ngModelChange)="onSearch($event)"
            name="search"
            class="w-full max-w-sm rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
          />
        </div>
        <div class="overflow-x-auto">
          <table class="w-full text-left text-sm">
            <thead class="bg-gray-50 text-xs uppercase text-gray-500">
              <tr>
                <th class="px-4 py-3">Name</th>
                <th class="px-4 py-3">Email</th>
                <th class="px-4 py-3">Role</th>
                <th class="px-4 py-3">Status</th>
                <th class="px-4 py-3">Created</th>
                <th class="px-4 py-3"></th>
              </tr>
            </thead>
            <tbody>
              @for (user of users(); track user.id) {
                <tr class="border-b border-gray-100 hover:bg-gray-50">
                  <td class="px-4 py-3 font-medium text-gray-800">{{ user.firstName }} {{ user.lastName }}</td>
                  <td class="px-4 py-3 text-gray-600">{{ user.email }}</td>
                  <td class="px-4 py-3">
                    <span class="rounded-full px-2.5 py-0.5 text-xs font-semibold"
                      [class]="roleBadgeClass(user.role)">{{ user.role }}</span>
                  </td>
                  <td class="px-4 py-3">
                    <span class="text-xs font-semibold" [class]="user.active ? 'text-green-600' : 'text-red-600'">
                      {{ user.active ? 'Active' : 'Disabled' }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-gray-500">{{ user.createdAt | date: 'MMM d, y' }}</td>
                  <td class="px-4 py-3 text-right">
                    @if (user.id !== auth.currentUser()?.id) {
                      <button (click)="toggleActive(user)" [disabled]="togglingId() === user.id"
                        class="rounded-lg px-3 py-1.5 text-xs font-medium transition-colors"
                        [class]="user.active ? 'text-red-600 hover:bg-red-50' : 'text-green-600 hover:bg-green-50'">
                        {{ togglingId() === user.id ? '...' : user.active ? 'Disable' : 'Enable' }}
                      </button>
                    }
                  </td>
                </tr>
              } @empty {
                <tr>
                  <td colspan="6" class="px-4 py-8 text-center text-gray-500">No users found.</td>
                </tr>
              }
            </tbody>
          </table>
        </div>
        <div class="flex items-center justify-between border-t border-gray-200 p-4 text-sm text-gray-600">
          <span>{{ pager().totalElements }} users · page {{ pager().page + 1 }} of {{ pager().totalPages }}</span>
          <div class="flex gap-2">
            <button (click)="goToPage(pager().page - 1)" [disabled]="pager().first || loading()"
              class="rounded-lg border border-gray-300 px-3 py-1.5 hover:bg-gray-50 disabled:opacity-40">Previous</button>
            <button (click)="goToPage(pager().page + 1)" [disabled]="pager().last || loading()"
              class="rounded-lg border border-gray-300 px-3 py-1.5 hover:bg-gray-50 disabled:opacity-40">Next</button>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class UsersComponent {
  private readonly api = inject(ApiService);
  protected readonly auth = inject(AuthService);

  protected readonly roles: Role[] = [Role.PATIENT, Role.DOCTOR, Role.STAFF, Role.ADMIN];

  protected readonly users = signal<User[]>([]);
  protected readonly loading = signal(false);
  protected readonly error = signal('');
  protected readonly notice = signal('');
  protected readonly searchTerm = signal('');
  protected readonly showCreateForm = signal(false);
  protected readonly creating = signal(false);
  protected readonly togglingId = signal<number | null>(null);
  protected readonly pager = signal({ page: 0, size: 10, totalElements: 0, totalPages: 0, first: true, last: false });

  protected readonly createForm: CreateUserForm = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phone: '',
    role: Role.PATIENT,
    licenseNumber: '',
  };

  constructor() {
    this.loadUsers();
  }

  protected onSearch(term: string): void {
    this.searchTerm.set(term);
    this.loadUsers(0);
  }

  protected goToPage(page: number): void {
    if (page < 0) return;
    this.loadUsers(page);
  }

  protected createUser(): void {
    this.creating.set(true);
    this.error.set('');
    this.notice.set('');

    const form = this.createForm;
    this.api
      .post<User>(API.ADMIN.USERS, {
        firstName: form.firstName,
        lastName: form.lastName,
        email: form.email,
        password: form.password,
        phone: form.phone || undefined,
        role: form.role,
        licenseNumber: form.role === Role.DOCTOR ? form.licenseNumber : undefined,
      })
      .subscribe({
        next: (user) => {
          this.notice.set(`Created ${user.role} account for ${user.email}.`);
          this.resetCreateForm();
          this.showCreateForm.set(false);
          this.loadUsers(0);
        },
        error: (err) => this.error.set(err.error?.message || 'Failed to create user'),
        complete: () => this.creating.set(false),
      });
  }

  protected toggleActive(user: User): void {
    this.togglingId.set(user.id);
    this.error.set('');
    this.notice.set('');

    this.api
      .patch<User>(API.ADMIN.USERS, user.id, { active: !user.active })
      .subscribe({
        next: (updated) => {
          this.notice.set(`${updated.email} is now ${updated.active ? 'active' : 'disabled'}.`);
          this.loadUsers(this.pager().page);
        },
        error: (err) => this.error.set(err.error?.message || 'Failed to update user'),
        complete: () => this.togglingId.set(null),
      });
  }

  protected roleBadgeClass(role: string): string {
    switch (role) {
      case 'ADMIN':
        return 'bg-purple-100 text-purple-700';
      case 'DOCTOR':
        return 'bg-blue-100 text-blue-700';
      case 'STAFF':
        return 'bg-amber-100 text-amber-700';
      default:
        return 'bg-gray-100 text-gray-700';
    }
  }

  private loadUsers(page = 0): void {
    this.loading.set(true);
    this.error.set('');

    this.api.getPaginated<User>(API.ADMIN.USERS, page, 10).subscribe({
      next: (res: PaginatedResponse<User>) => {
        this.users.set(res.content);
        this.pager.set({
          page: res.number,
          size: res.size,
          totalElements: res.totalElements,
          totalPages: res.totalPages,
          first: res.first,
          last: res.last,
        });
      },
      error: (err) => this.error.set(err.error?.message || 'Failed to load users'),
      complete: () => this.loading.set(false),
    });
  }

  private resetCreateForm(): void {
    this.createForm.firstName = '';
    this.createForm.lastName = '';
    this.createForm.email = '';
    this.createForm.password = '';
    this.createForm.phone = '';
    this.createForm.role = Role.PATIENT;
    this.createForm.licenseNumber = '';
  }
}
