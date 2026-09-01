import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, Input, Output, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

export interface SidebarItem {
  label: string;
  route: string;
}

export interface SidebarSection {
  items: SidebarItem[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  @Input() isOpen = false;
  @Input() userName = 'Govt. User';
  @Input() userRole = 'Government Officer';

  @Output() closeSidebar = new EventEmitter<void>();

  logoPath = 'VidyaSahay_Logo.png';
  isUserMenuOpen = false;

  sidebarItems: SidebarItem[] = [
        { label: 'Dashboard', route: '' },
        { label: 'Schemes', route: '/schemes' },
        { label: 'Create Scheme', route: '/schemes/create' },
        { label: 'Budget Management', route: '/budget' },
        { label: 'Budget Management', route: '/budget' },
        { label: 'Budget Management', route: '/budget' }
  ];

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get userInitial(): string {
    return this.userName?.trim().charAt(0).toUpperCase() || 'U';
  }

  onCloseSidebar(): void {
    this.closeSidebar.emit();
  }

  toggleUserMenu(): void {
    this.isUserMenuOpen = !this.isUserMenuOpen;
  }

  closeUserMenu(): void {
    this.isUserMenuOpen = false;
  }

  goToProfile(): void {
    this.router.navigateByUrl('/profile');
    this.closeUserMenu();
    this.onCloseSidebar();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/');
    this.closeUserMenu();
    this.onCloseSidebar();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement | null;
    if (!target?.closest('.sidebar-user-menu-wrap')) {
      this.closeUserMenu();
    }
  }
}
