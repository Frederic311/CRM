import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private base_url= 'http://localhost:8060/api/v1/auth';
    // Subject to broadcast user creation events
    private userCreatedSource = new Subject<void>();

    // Observable for components to subscribe to
    userCreated$ = this.userCreatedSource.asObservable();

  constructor(private http: HttpClient) {}

  getAllUsers():Observable<any[]>{
    const url = `${this.base_url}/all-users`
    return this.http.get<any[]>(url);
  }

  deleteUser(id:number): Observable<HttpResponse<void>>{
    const url = `${this.base_url}/delete-user/${id}`;
    return this.http.delete<void>(url, { observe: 'response' });
  }

  updateUser(user:any, id: number):Observable<HttpResponse<void>>{
    const url = `${this.base_url}/update-user/${id}`;
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    console.log(user);
    return this.http.put<void>(url, user, {
      headers,
      observe: 'response',
    });
  }

  getUserInfo(): Observable<any>{
    const url = `${this.base_url}/user-info`;
    return this.http.get<any>(url);
  }

  getImage(id: number){
    const url = `${this.base_url}/${id}/profile-photo`;
    return this.http.get(url, { responseType: 'blob' });
  }

  uploadImage(file: File, id: number):Observable<string>{
    const url = `${this.base_url}/${id}/upload`;
    const formData = new FormData();
    formData.append('file', file);

    const headers = new HttpHeaders({
      'Accept': 'application/json'
    });
    return this.http.post<string>(url, formData, { headers });
  }

  createUser(user: any):Observable<any>{
    const url = `${this.base_url}/register`;
    return this.http.post<any>(url, user);
  }

  // Method to trigger the userCreated event
  triggerUserCreated() {
    this.userCreatedSource.next();
  }

  getUserByEmail(email: string): Observable<any>{
    const url = `${this.base_url}/email/${email}`;
    return this.http.get<any>(url);
  }

  getRole(role: string): Observable<any>{
    const url = `${this.base_url}/roles/${role}`;
    return this.http.get<any>(url);
  }

  getUserById(userId: string | null): Observable<any>{
    const url = `${this.base_url}/user/${userId}`;
    return this.http.get<any>(url);
  }
}
