import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  inject
} from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserRole } from '../../core/enums/user-role.enum';

export interface SidebarItem {
  label: string;
  route: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  @Input() isOpen = false;
  @Input() userName = 'User';
  @Input() userRole = '';

  @Output() closeSidebar = new EventEmitter<void>();

  logoPath = 'VidyaSahay_Logo.png';

  sidebarItems: SidebarItem[] = [];

  ngOnInit(): void {
    this.loadSidebarItems();
  }

  private loadSidebarItems(): void {
    const role = this.authService.getRole();

    switch (role) {

      case UserRole.ADMIN:
        this.sidebarItems = [
          { label: 'Scholarship Schemes', route: '/scholarship-schemes' },
          { label: 'Loan Schemes', route: '/loan-schemes' },
          { label: 'Banks', route: '/banks' },
          { label: 'Institutes', route: '/institutes' },
          { label: 'Students', route: '/students' }
        ];
        break;

      case UserRole.GOVERNMENT:
        this.sidebarItems = [
          { label: 'My Scholarship Schemes', route: '/scholarship-schemes-created-by-me' },
          { label: 'Create Scholarship Scheme', route: '/scholarship-scheme/create' },
          { label: 'My Applications', route: '/my-applications' },
        ];
        break;

        case UserRole.BANK:
          this.sidebarItems = [
            { label: 'My Loan Schemes', route: '/loan-schemes-created-by-me' },
            { label: 'Create Loan Scheme', route: '/loan-scheme/create' },
            { label: 'My Applications', route: '/my-applications' },
          ];
          break;

          case UserRole.STUDENT:
            this.sidebarItems = [
              { label: 'Eligible Scholarships', route: '/scholarship-schemes/eligible' },
              { label: 'Eligible Loans', route: '/loan-schemes/eligible' },
              { label: 'My Applications', route: '/my-applications' },
              { label: 'My Profile', route: '/my-profile' },
        ];
        break;

      case UserRole.INSTITUTE:
        this.sidebarItems = [
          { label: 'My Students', route: '/my-students' },
          { label: 'Students Applications', route: '/student-applications' }
        ];
        break;

      default:
        this.sidebarItems = [];
    }
  }

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get userInitial(): string {
    return this.userName?.trim().charAt(0).toUpperCase() || 'U';
  }

  onCloseSidebar(): void {
    this.closeSidebar.emit();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/');
    this.onCloseSidebar();
  }
}
