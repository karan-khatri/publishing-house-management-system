import { Component, Input, Output, EventEmitter } from '@angular/core';
import { SelectOption } from '@app/shared/types';

@Component({
  selector: 'app-select-field',
  standalone: true,
  templateUrl: './select-field.component.html',
})
export class SelectFieldComponent {
  @Input() label: string = '';
  @Input() required: boolean = false;
  @Input() disabled: boolean = false;
  @Input() placeholder: string = 'Select an option';
  @Input() value?: string | number | null = null;

  // Options: simple array of objects {label, value}
  @Input() options: SelectOption[] = [];

  @Output() valueChange = new EventEmitter<string | number>();

  onValueChange(event: Event) {
    const target = event.target as HTMLSelectElement;
    this.value = target.value;
    this.valueChange.emit(this.value);
  }
}
