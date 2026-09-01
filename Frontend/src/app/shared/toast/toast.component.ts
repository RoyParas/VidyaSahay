import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-toast-host',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.css'
})
export class ToastComponent {
  readonly toastService = inject(ToastService);

  trackByToastId(_: number, toast: { id: number }): number {
    return toast.id;
  }

  getToastClass(type: string): string {
    return `toast-${type}`;
  }
}
