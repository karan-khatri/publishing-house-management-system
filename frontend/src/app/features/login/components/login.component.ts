import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { Router } from '@angular/router';
import { NotificationService } from '@core/services/notification.service';
import { ButtonComponent, InputFormFieldComponent } from '@shared/components/ui';
import { AuthService, LoginRequest } from '@app/core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, ButtonComponent, ReactiveFormsModule, InputFormFieldComponent],
  templateUrl: './login.component.html',
})
export class LoginComponent implements OnInit {
  loginData: LoginRequest = {
    email: '',
    password: '',
  };
  loginForm!: FormGroup;

  isLoading = false;

  constructor(
    private readonly authService: AuthService,
    private readonly notification: NotificationService,
    private readonly formBuilder: FormBuilder,
    private readonly router: Router
  ) {
    this.initializeForm();
  }

  private initializeForm(): void {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.notification.warning('Please fill in all required fields correctly', 'Validation Error');
    }

    this.isLoading = true;
    const loginData: LoginRequest = {
      email: this.loginForm.value.email,
      password: this.loginForm.value.password,
    };

    this.authService.login(loginData).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.authService.setToken(response.data.token);
          this.authService.setUser(response.data.user);
          this.notification.success(response.message || 'Login successful!');
          this.router.navigate(['/dashboard']);
        } else {
          this.notification.error(response.message || 'Login failed');
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Login error:', error);
        this.notification.error('Invalid email or password', 'Login Failed');
      },
    });
  }
}
