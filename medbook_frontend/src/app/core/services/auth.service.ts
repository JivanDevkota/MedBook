import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, finalize, firstValueFrom, share, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { User } from '../models/user.model';
import { API } from '../constants/api.constants';

export interface AuthResponse {
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
  phone?: string;
}

/**
 * Cookie-based session management. The server issues the access and refresh
 * JWTs as HttpOnly cookies (invisible to JavaScript), so this service keeps
 * no token material anywhere. All requests are sent with credentials and the
 * CSRF token is echoed by the auth interceptor.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly _currentUser = signal<User | null>(null);
  readonly currentUser = this._currentUser.asReadonly();
  readonly isAuthenticated = computed(() => this._currentUser() !== null);
  readonly isAdmin = computed(() => this._currentUser()?.role === 'ADMIN');

  private refreshInFlight: Observable<AuthResponse> | null = null;

  /**
   * App bootstrap: prime the CSRF cookie, then resolve the session from the
   * access cookie via /auth/me. If the access cookie is expired but the
   * refresh cookie is still valid, the 401 triggers the interceptor's
   * refresh-and-retry flow and the session is restored transparently.
   */
  async bootstrap(): Promise<void> {
    try {
      await firstValueFrom(
        this.http.get(`${environment.apiUrl}${API.AUTH.CSRF}`, { withCredentials: true }),
      );
    } catch {
      // CSRF priming is best-effort; the token is also refreshed lazily.
    }
    try {
      const user = await firstValueFrom(
        this.http.get<User>(`${environment.apiUrl}${API.AUTH.ME}`, { withCredentials: true }),
      );
      this._currentUser.set(user);
    } catch {
      this._currentUser.set(null);
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}${API.AUTH.LOGIN}`, credentials, { withCredentials: true })
      .pipe(tap((res) => this._currentUser.set(res.user)));
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}${API.AUTH.REGISTER}`, data, { withCredentials: true })
      .pipe(tap((res) => this._currentUser.set(res.user)));
  }

  logout(): void {
    // Best effort: the server clears the cookies even if this call races.
    this.http.post(`${environment.apiUrl}${API.AUTH.LOGOUT}`, {}, { withCredentials: true }).subscribe({
      error: () => undefined,
    });
    this._currentUser.set(null);
  }

  /**
   * Single-flight refresh: concurrent 401s share one /auth/refresh call so
   * parallel retries cannot stampede the endpoint or race the cookie rotation.
   */
  refreshSession(): Observable<AuthResponse> {
    if (!this.refreshInFlight) {
      this.refreshInFlight = this.http
        .post<AuthResponse>(`${environment.apiUrl}${API.AUTH.REFRESH}`, {}, { withCredentials: true })
        .pipe(
          tap((res) => this._currentUser.set(res.user)),
          finalize(() => (this.refreshInFlight = null)),
          share(),
        );
    }
    return this.refreshInFlight;
  }
}
