import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('publishing_house_frontend');

  constructor(private readonly toastr: ToastrService) {}

  testToast() {
    this.toastr.success('Toastr is working!', 'Success');
  }
}
