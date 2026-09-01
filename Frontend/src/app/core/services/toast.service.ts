import { Injectable, signal } from '@angular/core';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface ToastItem {
  id: number;
  type: ToastType;
  title: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private readonly toastsSignal = signal<ToastItem[]>([]);
  private nextId = 1;

  readonly toasts = this.toastsSignal.asReadonly();

  success(message: string, title = 'Success'): void {
    this.show('success', title, message);
  }

  error(message: string, title = 'Error'): void {
    this.show('error', title, message);
  }

  warning(message: string, title = 'Warning'): void {
    this.show('warning', title, message);
  }

  info(message: string, title = 'Info'): void {
    this.show('info', title, message);
  }

  remove(id: number): void {
    this.toastsSignal.update(toasts => toasts.filter(toast => toast.id !== id));
  }

  private show(type: ToastType, title: string, message: string): void {
    const toast: ToastItem = {
      id: this.nextId++,
      type,
      title,
      message
    };

    this.toastsSignal.update(toasts => [...toasts, toast]);
    globalThis.setTimeout(() => this.remove(toast.id), 3500);
  }
}
