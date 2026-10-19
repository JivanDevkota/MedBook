import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PaginatedResponse } from '../models/pagination.model';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  get<T>(path: string, params?: HttpParams): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${path}`, { params });
  }

  getPaginated<T>(path: string, page = 0, size = 20, sort?: string): Observable<PaginatedResponse<T>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (sort) params = params.set('sort', sort);
    return this.http.get<PaginatedResponse<T>>(`${this.baseUrl}${path}`, { params });
  }

  getById<T>(path: string, id: number): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${path}/${id}`);
  }

  post<T>(path: string, body: unknown): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${path}`, body);
  }

  put<T>(path: string, id: number, body: unknown): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${path}/${id}`, body);
  }

  patch<T>(path: string, id: number, body: unknown): Observable<T> {
    return this.http.patch<T>(`${this.baseUrl}${path}/${id}`, body);
  }

  delete<T>(path: string, id: number): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${path}/${id}`);
  }
}
