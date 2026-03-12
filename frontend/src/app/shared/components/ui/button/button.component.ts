import { Component, Input, Output, EventEmitter } from '@angular/core';

export type ButtonVariant = 'primary' | 'secondary' | 'outline' | 'danger';
export type ButtonSize = 'sm' | 'md' | 'lg';

@Component({
  selector: 'app-button',
  standalone: true,
  templateUrl: './button.component.html',
})
export class ButtonComponent {
  @Input() variant: ButtonVariant = 'primary';
  @Input() size: ButtonSize = 'md';
  @Input() disabled = false;
  @Input() loading = false;
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() fullWidth = false;

  @Output() clickEvent = new EventEmitter<Event>();

  onClick(event: Event): void {
    if (!this.disabled && !this.loading) {
      this.clickEvent.emit(event);
    }
  }

  get buttonClasses(): string {
    const base = `
      inline-flex items-center justify-center font-medium rounded-lg 
      focus:outline-none focus:ring-2 focus:ring-offset-1 transition-colors
      ${this.fullWidth ? 'w-full' : ''}
      ${this.disabled || this.loading ? 'opacity-60 cursor-not-allowed' : ''}
    `;

    const variants: Record<ButtonVariant, string> = {
      primary: 'bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500',
      secondary: 'bg-gray-100 text-gray-800 hover:bg-gray-200 focus:ring-gray-400',
      outline:
        'bg-transparent border border-blue-600 text-blue-600 hover:bg-blue-50 focus:ring-blue-400',
      danger: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500',
    };

    const sizes: Record<ButtonSize, string> = {
      sm: 'px-3 py-1.5 text-sm',
      md: 'px-4 py-2 text-base',
      lg: 'px-5 py-3 text-lg',
    };

    return [base, variants[this.variant], sizes[this.size]].join(' ');
  }
}
