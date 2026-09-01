import { CommonModule } from '@angular/common';
import { Component, DestroyRef, EventEmitter, HostListener, Input, Output, inject } from '@angular/core';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { filter } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  @Input() userName = 'Govt. User';
  @Input() userRole = 'Government Officer';
  @Input() showSidebarToggle = false;

  @Output() toggleSidebar = new EventEmitter<void>();

  isProfileMenuOpen = false;
  readonly logoPath = 'VidyaSahay_Logo.png';

  constructor() {
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => {
        this.isProfileMenuOpen = false;
      });
  }

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get userInitial(): string {
    return this.userName?.trim().charAt(0).toUpperCase() || 'U';
  }

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }

  toggleProfileMenu(): void {
    this.isProfileMenuOpen = !this.isProfileMenuOpen;
  }

  closeProfileMenu(): void {
    this.isProfileMenuOpen = false;
  }

  goHome(): void {
    this.router.navigateByUrl('/');
    this.closeProfileMenu();
  }

  goToProfile(): void {
    this.router.navigateByUrl('/profile');
    this.closeProfileMenu();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/');
    this.closeProfileMenu();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement | null;
    if (!target?.closest('.user-menu-wrap')) {
      this.closeProfileMenu();
    }
  }
}
