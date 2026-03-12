import { Component, Input, Output, EventEmitter, forwardRef } from '@angular/core';
import { NG_VALUE_ACCESSOR, ControlValueAccessor, AbstractControl } from '@angular/forms';

export type InputType = 'text' | 'email' | 'password' | 'number' | 'tel' | 'textarea' | 'date';

@Component({
  selector: 'app-form-input-field',
  standalone: true,
  templateUrl: './form-input-field.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputFormFieldComponent),
      multi: true,
    },
  ],
})
export class InputFormFieldComponent implements ControlValueAccessor {
  // Inputs
  @Input() label = '';
  @Input() placeholder = '';
  @Input() type: InputType = 'text';
  @Input() disabled = false;
  @Input() required = false;
  @Input() helperText = '';
  @Input() errorMessage = ''; // custom override
  @Input() autoDetectErrors = true;
  @Input() control: AbstractControl | null = null;
  @Input() min?: number;
  @Input() max?: number;
  @Input() step?: number;
  @Input() rows = 3;
  @Input() extraClasses = '';

  // Outputs
  @Output() blur = new EventEmitter<void>();
  @Output() focus = new EventEmitter<void>();

  value = '';

  private onChange = (value: string) => {};
  private onTouched = () => {};

  // -------------------------
  // ControlValueAccessor
  // -------------------------
  writeValue(value: string): void {
    this.value = value || '';
  }
  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }
  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }
  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  // -------------------------
  // Error handling
  // -------------------------
  get shouldShowError(): boolean {
    if (!this.autoDetectErrors || !this.control) return false;
    return !!(this.control.invalid && (this.control.dirty || this.control.touched));
  }

  get currentErrorMessage(): string {
    if (this.errorMessage) return this.errorMessage;
    if (!this.autoDetectErrors || !this.control?.errors) return '';
    return this.getFormControlErrorMessage();
  }

  private getFormControlErrorMessage(): string {
    if (!this.control?.errors) return '';
    const errors = this.control.errors;
    const name = this.label || 'Field';

    if (errors['required']) return `${name} is required`;
    if (errors['minlength'])
      return `${name} must be at least ${errors['minlength'].requiredLength} characters`;
    if (errors['maxlength'])
      return `${name} must be at most ${errors['maxlength'].requiredLength} characters`;
    if (errors['min']) return `${name} must be at least ${errors['min'].min}`;
    if (errors['max']) return `${name} must be at most ${errors['max'].max}`;
    if (errors['pattern'] || errors['email']) return `Please enter a valid ${name.toLowerCase()}`;
    return `${name} is invalid`;
  }

  // -------------------------
  // Event handlers
  // -------------------------
  onInputChange(event: Event): void {
    const val = (event.target as HTMLInputElement | HTMLTextAreaElement).value;
    this.value = val;
    this.onChange(val);
  }

  onInputBlur(): void {
    this.onTouched();
    this.blur.emit();
  }

  onInputFocus(): void {
    this.focus.emit();
  }

  // -------------------------
  // CSS Classes
  // -------------------------
  get inputClasses(): string {
    return [
      'bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 focus:outline-none block w-full p-2.5',
      this.disabled ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-white text-gray-700',
      this.shouldShowError ? 'border-red-500' : 'border-gray-300',
    ].join(' ');
  }
}
