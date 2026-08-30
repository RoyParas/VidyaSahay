import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  @Input() userName = 'Govt. User';
  @Input() userRole = 'Government Officer';
  @Input() notificationCount = 3;
  @Input() pageTitle = '';

  @Output() toggleSidebar = new EventEmitter<void>();

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }
}