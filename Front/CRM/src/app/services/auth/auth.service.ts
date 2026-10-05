import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',

})
export class AuthService {

  private baseUrl = 'http://localhost:8060/api/v1/auth/'; // Corrected backend URL

  constructor(private http: HttpClient, private router: Router) {}

// Method to handle errors
private handleError(error: any) {
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
    // The token has to be read before it is removed. The previous order
    // removed it first, so getToken() always answered null, the branch that
    // called the backend was unreachable, and the second removeItem was dead
    // code. AuthenticationService.logout on the server expires the token row,
    // and it was never reached, so a token kept working until it expired on its
    // own.
    const token = this.getToken();
    localStorage.removeItem('jwtToken');

    if (!token) {
      this.router.navigate(['/login']);
      return;
    }

    // POST /auth/logout takes the token from the Authorization header, it has no
    // request body. Sending { token } came back as 401 "Authorization token is
    // required", which the empty error handler swallowed.
    this.http
      .post(`${this.baseUrl}logout`, null, { headers: { Authorization: `Bearer ${token}` } })
      .subscribe(
        () => this.router.navigate(['/login']),
        // The token is already gone locally either way, so the reader still has
        // to land on the login page when the call fails.
        () => this.router.navigate(['/login'])
      );
  }


  clearTokenOnReload(): void {
    const token = this.getToken();
    if (token) {
      this.http.post(`${this.baseUrl}logout`, { token }, { responseType: 'text' }).subscribe(
        () => {
          localStorage.removeItem('jwtToken');
        },
        () => {}
      );
    }
  }


}



