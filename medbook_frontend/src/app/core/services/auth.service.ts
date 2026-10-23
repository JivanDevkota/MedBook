import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { User } from '../models/user.model';
import { API } from '../constants/api.constants';

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

const TOKEN_KEY = 'medbook_access_token';
const REFRESH_TOKEN_KEY = 'medbook_refresh_token';
const USER_KEY = 'medbook_current_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly _currentUser = signal<User | null>(null);
  readonly currentUser = this._currentUser.asReadonly();
  readonly isAuthenticated = computed(() => this._currentUser() !== null);
  readonly isAdmin = computed(() => this._currentUser()?.role === 'ADMIN');

  constructor() {
    this.restoreSession();
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}${API.AUTH.LOGIN}`, credentials).pipe(
      tap((res) => this.handleAuthResponse(res)),
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}${API.AUTH.REGISTER}`, data).pipe(
      tap((res) => this.handleAuthResponse(res)),
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this._currentUser.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  private handleAuthResponse(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, res.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(res.user));
    this._currentUser.set(res.user);
  }

  /**
   * Restores the session from localStorage. The JWT only carries the subject
   * claim, so the user profile is persisted alongside the tokens. A stored
   * token that has already expired is discarded.
   */
  private restoreSession(): void {
    const token = this.getToken();
    const storedUser = localStorage.getItem(USER_KEY);
    if (!token || !storedUser) return;

    if (this.isTokenExpired(token)) {
      this.logout();
      return;
    }

    try {
      this._currentUser.set(JSON.parse(storedUser) as User);
    } catch {
      this.logout();
    }
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1])) as { exp?: number };
      if (!payload.exp) return false;
      return payload.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }
}
