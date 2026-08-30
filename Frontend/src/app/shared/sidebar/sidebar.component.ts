import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

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
  @Input() isOpen = false;
  @Input() userName = 'Govt. User';
  @Input() userRole = 'Government Officer';
  @Input() notificationCount = 3;

  @Output() closeSidebar = new EventEmitter<void>();

  logoPath = 'VidyaSahay_Logo.png';

  sidebarItems: SidebarItem[] = [
        { label: 'Dashboard', route: '' },
        { label: 'Schemes', route: '/schemes' },
        { label: 'Create Scheme', route: '/schemes/create' },
        { label: 'Budget Management', route: '/budget' },
        { label: 'Budget Management', route: '/budget' },
        { label: 'Budget Management', route: '/budget' }
  ];

  onCloseSidebar(): void {
    this.closeSidebar.emit();
  }
}