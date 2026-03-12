import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private readonly authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Get the auth token
    const authToken = this.authService.getToken();
    
    // Skip adding auth header for login/register endpoints
    const isAuthEndpoint = request.url.includes('/auth/login') || 
                          request.url.includes('/auth/register');
    
    // If we have a token and it's not an auth endpoint, add the Authorization header
    if (authToken && !isAuthEndpoint) {
      const authReq = request.clone({
        headers: request.headers.set('Authorization', `Bearer ${authToken}`)
      });
      
      
      return next.handle(authReq);
    }

    return next.handle(request);
  }
}