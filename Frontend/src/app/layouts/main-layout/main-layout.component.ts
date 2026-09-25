import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    SidebarComponent,
    NavbarComponent,
    FooterComponent
  ],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent {
  private readonly authService = inject(AuthService);
  isSidebarOpen = false;

  get userName(): string {
    return this.authService.getUserName();
  }

  get userRole(): string {
    return (this.authService.getRole() ?? 'USER')
      .toLowerCase()
      .replace(/\b\w/g, letter => letter.toUpperCase());
  }

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }


  toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  closeSidebar(): void {
    this.isSidebarOpen = false;
  }
}
