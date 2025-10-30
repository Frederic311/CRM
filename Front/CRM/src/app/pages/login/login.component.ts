import { Component, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})

export class LoginComponent implements OnInit, OnDestroy{
  private bootstrapLink!: HTMLLinkElement;

  constructor(private router: Router, private authService: AuthService, private renderer: Renderer2) {}
  errorMessage: string | undefined;

  email: string = "";
  password: string = "";


  onSubmit() {
    // Perform validation checks before calling the login service
    if (!this.validateEmail(this.email)) {
      this.errorMessage = 'Please enter a valid email address.';
      return; // Exit the function if email is invalid
    }

    if (this.password.length < 8) {
      this.errorMessage = 'Password must be at least 8 characters long.';
      return; // Exit the function if password is too short
    }

    const credentials = {
      email: this.email,
      password: this.password
    };

    this.authService.loginUser(credentials).subscribe(
      response => {
        console.log('User logged in successfully', response.message);
        this.authService.saveToken(response.token); // Save the token
        console.log('Navigating to /dashboard');
        this.router.navigate(['/dashboard']);
      },
      error => {
        console.error('Error logging in', error);
        this.errorMessage = this.getErrorMessage(error.error.businessErrorCode);
      }
    );
  }

  // Function to validate email format
  validateEmail(email: string) {
    const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
  }

  getErrorMessage(errorCode: number): string {
    switch (errorCode) {
      case 0:
        return 'No code';
      case 300:
        return 'Current password is incorrect';
      case 301:
        return 'The new password does not match';
      case 302:
        return 'User account is locked';
      case 303:
        return 'User account is disabled';
      case 304:
        return 'Login and/or Password is incorrect';
      default:
        return 'An unexpected error occurred. Please try again later.';
    }
  }

   navigateToForgetPass() {
     this.router.navigate(['/forget-password']);
   }

   navigateToselfCreation() {
    this.router.navigate(['/selfCreation']);
  }

  ngOnInit(): void {
    this.bootstrapLink = this.renderer.createElement('link');
    this.bootstrapLink.rel = 'stylesheet';
    this.bootstrapLink.href = 'https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css'; // or your local path
    this.renderer.appendChild(document.head, this.bootstrapLink);
  }

  ngOnDestroy(): void {
    if (this.bootstrapLink) {
      this.renderer.removeChild(document.head, this.bootstrapLink);
    }
  }

  }

