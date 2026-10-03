import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { HttpRequest } from '@angular/common/http';

import { AuthService } from './auth.service';

/**
 * The generated spec only asserted toBeTruthy(), so logout was never exercised.
 * Three defects sat behind it, each hiding the next:
 *
 * - Router was declared as `router: any` and never assigned, so both branches
 *   of logout() ended on "Cannot read properties of undefined (reading
 *   'navigate')".
 * - the token was removed from storage before being read back, so the branch
 *   that called the backend was unreachable and AuthenticationService.logout,
 *   which expires the token server side, never ran.
 * - the call sent the token in the body while POST /auth/logout reads it from
 *   the Authorization header and has no body.
 *
 * The providers are declared here rather than through a shared helper because
 * this spec has to stand on its own.
 */
describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;
  let router: Router;

  /** Matches on the path rather than the full URL, so where the API lives is not this spec's business. */
  const isLogout = (request: HttpRequest<unknown>) => request.url.endsWith('/logout');

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.resolveTo(true);
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
    http.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('logout', () => {
    it('removes the token from storage', () => {
      service.saveToken('a-token');

      service.logout();

      http.expectOne(isLogout).flush({});
      expect(localStorage.getItem('jwtToken')).toBeNull();
    });

    it('sends the token in the Authorization header, which is where POST /auth/logout reads it', () => {
      service.saveToken('a-token');

      service.logout();

      const request = http.expectOne(isLogout);
      expect(request.request.method).toBe('POST');
      expect(request.request.headers.get('Authorization')).toBe('Bearer a-token');
      expect(request.request.body).toBeNull();
      request.flush({});
    });

    it('navigates to the login page', () => {
      service.saveToken('a-token');

      service.logout();
      http.expectOne(isLogout).flush({});

      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('navigates to the login page even when the backend rejects the call', () => {
      service.saveToken('a-token');

      service.logout();
      http.expectOne(isLogout).flush('nope', { status: 500, statusText: 'Server Error' });

      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('navigates without calling the backend when there is no token', () => {
      service.logout();

      http.expectNone(isLogout);
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });
});
