import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-input-field',
  standalone: true,
  templateUrl: './input-field.component.html',
})
export class InputFieldComponent {
  @Input() label: string = '';
  @Input() type: 'text' | 'number' | 'email' | 'password' = 'text';
  @Input() placeholder: string = '';
  @Input() value?: string = '';
  @Input() textarea: boolean = false; // toggle between input & textarea
  @Input() rows: number = 3; // textarea rows

  @Output() valueChange = new EventEmitter<string>();

  onValueChange(event: Event) {
    const target = event.target as HTMLInputElement | HTMLTextAreaElement;
    this.valueChange.emit(target.value);
  }
}
