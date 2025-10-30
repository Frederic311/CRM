import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',

})
export class AuthService {

  private baseUrl = 'http://localhost:8060/api/v1/auth/'; // Corrected backend URL
  router: any;

  constructor(private http: HttpClient) {}

// Method to handle errors
private handleError(error: any) {
  console.error('An error occurred:', error); // Log the error
  let errorMessage = 'Something bad happened; please try again later.';
  if (error.error instanceof ErrorEvent) {
    // A client-side or network error occurred.
    errorMessage = `Error: ${error.error.message}`;
  } else {
    // The backend returned an unsuccessful response code.
    if (error.status === 404 && error.error === 'User with the given email not found.') {
      errorMessage = 'User with this email does not exist.';
    }
    else if (error.status === 400 && error.error === 'Invalid or expired token.') {
      errorMessage = 'Invalid or expired token.';
    }
     else {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
  }
  return throwError(errorMessage); // Return an observable with a user-facing error message
}


  // Method to initiate password reset
  forgotPassword(email: string): Observable<any> {
    const params = new HttpParams().set('email', email);
    return this.http.post(`${this.baseUrl}forgot-password`, null, { params, responseType: 'text' }) // Specify response type as 'text'
      .pipe(
        catchError(this.handleError)
      );
  }



  // Method to verify the token
  verifyToken(token: string): Observable<any> {
    const params = new HttpParams().set('token', token);
    return this.http.post(`${this.baseUrl}verify-token`, null, { params, responseType: 'text' }) // Use responseType 'text'
      .pipe(
        catchError(this.handleError)
      );
  }


  // Method to reset the password
  resetPassword(token: string, newPassword: string): Observable<any> {
    const params = new HttpParams()
      .set('token', token)
      .set('newPassword', newPassword);
    return this.http.post(`${this.baseUrl}reset-password`, null, { params, responseType: 'text' }) // Use responseType 'text'
      .pipe(
        catchError(this.handleError)
      );
  }


  private apiUrl = 'http://localhost:8060/api/v1/auth/authenticate';

  loginUser(credentials: any): Observable<any> {
    return this.http.post(this.apiUrl, credentials);
  }

  saveToken(token: string): void {
    localStorage.setItem('jwtToken', token);
  }

  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }
  isLoggedIn(): boolean {
    const token = localStorage.getItem('jwtToken');
    // You might also want to check if the token is valid and not expired
    return token !== null;
  }

  logout(): void {
          localStorage.removeItem('jwtToken');
    const token = this.getToken();
    if (token) {
      this.http.post(`${this.baseUrl}logout`, { token }).subscribe(
        () => {
          localStorage.removeItem('jwtToken');
          this.router.navigate(['/login']);
        },
        (error) => {
          console.error('Error during logout:', error);
        }
      );
    } else {
      this.router.navigate(['/login']);
    }
  }


  clearTokenOnReload(): void {
    const token = this.getToken();
    if (token) {
      this.http.post(`${this.baseUrl}logout`, { token }, { responseType: 'text' }).subscribe(
        (response) => {
          console.log(response); // Should log "Logout successful."
          localStorage.removeItem('jwtToken');
        },
        (error) => {
          console.error('Error during token clearance on reload:', error);
        }
      );
    }
  }


}



