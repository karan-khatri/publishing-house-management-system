import { Component, Input, Output, EventEmitter, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, AbstractControl } from '@angular/forms';
import { SelectOption } from '@app/shared/types';

@Component({
  selector: 'app-form-select-field',
  standalone: true,
  templateUrl: './form-select-field.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectFormFieldComponent),
      multi: true,
    },
  ],
})
export class SelectFormFieldComponent implements ControlValueAccessor {
  // Inputs
  @Input() label = '';
  @Input() placeholder = 'Select an option';
  @Input() options: SelectOption[] = [];
  @Input() disabled = false;
  @Input() required = false;
  @Input() helperText = '';
  @Input() errorMessage = ''; // custom override
  @Input() autoDetectErrors = true; // auto detect from Angular
  @Input() control: AbstractControl | null = null; // reactive form control
  @Input() extraClasses = ''; // additional classes for the container

  // Outputs
  @Output() blur = new EventEmitter<void>();
  @Output() focus = new EventEmitter<void>();

  value: any = '';

  private onChange: (value: any) => void = () => {};
  private onTouched: () => void = () => {};

  // -------------------------
  // ControlValueAccessor
  // -------------------------
  writeValue(value: any): void {
    this.value = value;
  }
  registerOnChange(fn: (value: any) => void): void {
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
    if (errors['pattern']) return `${name} format is invalid`;
    return `${name} is invalid`;
  }

  // -------------------------
  // Event handlers
  // -------------------------
  onSelectChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.value = target.value;
    this.onChange(this.value);
  }

  onSelectBlur(): void {
    this.onTouched();
    this.blur.emit();
  }

  onSelectFocus(): void {
    this.focus.emit();
  }

  get selectClasses(): string {
    return [
      'block w-full rounded-lg border px-3 py-2 text-sm shadow-sm',
      'focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500 focus:ring-offset-1',
      this.disabled ? 'cursor-not-allowed bg-gray-100 text-gray-400' : 'bg-white text-gray-700',
      this.shouldShowError ? 'border-red-500' : 'border-gray-300',
    ].join(' ');
  }
}
