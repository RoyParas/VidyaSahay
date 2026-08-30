import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  logout() : void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  }
}