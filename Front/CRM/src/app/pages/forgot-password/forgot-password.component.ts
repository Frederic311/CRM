import { Component } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss'],
  imports: [CommonModule, FormsModule],
  standalone: true,
})
export class ForgotPasswordComponent {
  email: string = '';
  token: string = '';
  newPassword: string = '';
  step: number = 1; // Step to track which form to show
  successMessage: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  // Request password reset
  requestPasswordReset() {
    this.errorMessage = ''; // Clear previous error messages
    this.successMessage = ''; // Clear previous success messages

    this.authService.forgotPassword(this.email).subscribe(
      response => {
        console.log(response);
        this.successMessage = 'A password reset link has been sent to your email.';
        this.step = 2; // Go to verification step
      },
      error => {
        console.error(error);
        this.errorMessage = error; // Show the error from the service
      }
    );
  }

  // Verify the token
  resetPassword() {
    this.errorMessage = ''; // Clear any previous error messages
    this.authService.resetPassword(this.token, this.newPassword).subscribe(
      response => {
        console.log(response);
        this.errorMessage = ''; // Clear the error message on success
        this.successMessage = 'Password has been reset successfully! You will be redirected to the login page in 5 seconds.';
        let countdown = 5;

        const interval = setInterval(() => {
          countdown--;
          this.successMessage = `Password has been reset successfully! You will be redirected to the login page in ${countdown} seconds.`;

          if (countdown === 0) {
            clearInterval(interval);
            this.router.navigate(['/login']); // Redirect to the login page
          }
        }, 1000); // Update every second
      },
      error => {
        console.error(error);
        this.errorMessage = error; // Show the error from the service
      }
    );
  }

  verifyToken() {
    this.errorMessage = ''; // Clear any previous error messages
    this.authService.verifyToken(this.token).subscribe(
      response => {
        console.log(response);
        this.errorMessage = ''; // Clear the error message on success
        this.successMessage = 'Token verified successfully!';
        // Proceed to the next step or show a success message
        this.step = 3;
      },
      error => {
        console.error(error);
        this.errorMessage = error; // Show the error from the service
      }
    );
  }


  // Reset the password
  // resetPassword() {
  //   this.authService.resetPassword(this.token, this.newPassword).subscribe(
  //     response => {
  //       console.log(response);
  //       this.successMessage = 'Password has been reset successfully! You will be redirected to the login page.';
  //       this.router.navigate(['/login']);
        // let countdown = 0;

        // const interval = setInterval(() => {
        //   countdown--;
        //   this.successMessage = `Password has been reset successfully! You will be redirected to the login page in ${countdown} seconds.`;

        //   if (countdown === 0) {
        //     clearInterval(interval);
        //     this.router.navigate(['/login']); // Redirect to the login page
        //   }
        // }, 1000); // Update every second
    //   },
    //   error => {
    //     console.error(error);
    //     this.errorMessage = error; // Show the error from the service
    //   }
    // );
    nextStep() {
      this.step++;
    }
  }


  // Navigate to the next step


