import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  isLoggedIn(): boolean {
    return this.getStorageItem('token') !== null;
  }

  getRole(): string | null {
    return this.getStorageItem('role');
  }

  getToken(): string | null {
    return this.getStorageItem('token');
  }

  logout() : void {
    this.removeStorageItem('token');
    this.removeStorageItem('role');
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
