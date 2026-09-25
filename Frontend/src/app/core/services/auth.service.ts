import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, tap } from "rxjs";
import { LoginRequest, LoginResponse } from "../models/auth.dtos/login.dtos";
import { environment } from '../../../environments/env'
import { RegisterRequest, RegisterResponse } from "../models/auth.dtos/register.dtos";
import { UserRole } from "../enums/user-role.enum";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly API_URL = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}


  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.API_URL}/login`, request)
      .pipe(
        tap(response => {
          this.setStorageItem('token', response.accessToken);
          this.setStorageItem('role', response.user.role);
          this.setStorageItem('userName', `${response.user.firstName} ${response.user.lastName}`.trim());
        }));
  }

  register(request: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.API_URL}/register`,request);
  }

  isLoggedIn(): boolean {
    return this.getStorageItem('token') !== null;
  }

  getRole(): UserRole | null {
     let value: string | null = this.getStorageItem('role');
      if (Object.values(UserRole).includes(value as UserRole)) {
        return value as UserRole;
      }
      return null;
  }

  getDefaultRoute(): string {
    const routes: Record<UserRole, string> = {
      [UserRole.ADMIN]: '/students',
      [UserRole.BANK]: '/loan-schemes-created-by-me',
      [UserRole.GOVERNMENT]: '/scholarship-schemes-created-by-me',
      [UserRole.STUDENT]: '/my-applications',
      [UserRole.INSTITUTE]: '/my-students'
    };

    const role = this.getRole();
    return role ? routes[role] : '/login';
  }

  getUserName(): string {
    return this.getStorageItem('userName') || 'Account';
  }

  getToken(): string | null {
    return this.getStorageItem('token');
  }

  logout() : void {
    this.removeStorageItem('token');
    this.removeStorageItem('role');
    this.removeStorageItem('userName');
  }

  private setStorageItem(key: string, value: any): void | null {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return null;
    }

    return localStorage.setItem(key, value);
  }

  private getStorageItem(key: string): string | null {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return null;
    }

    return localStorage.getItem(key);
  }

  private removeStorageItem(key: string): void {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return;
    }

    localStorage.removeItem(key);
  }
}
