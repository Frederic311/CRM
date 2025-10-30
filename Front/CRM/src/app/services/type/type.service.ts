import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TypeService {
  private base_url = 'http://localhost:8089/api/event-types'; // API endpoint defined in the Spring Boot controller

  constructor(private http: HttpClient) {}

  // Get all types
  getAllTypes(): Observable<any[]> {
    const url = `${this.base_url}`;
    return this.http.get<any[]>(url);
  }

  // Delete a type by ID
  deleteType(id: number): Observable<HttpResponse<void>> {
    const url = `${this.base_url}/${id}`; // Adjusted to match the @DeleteMapping("/{id}") endpoint
    return this.http.delete<void>(url, { observe: 'response' });
  }

  // Update type by ID
  updateType(type: any, id: number): Observable<HttpResponse<void>> {
    const url = `${this.base_url}/${id}`; // Adjusted to match the @PutMapping("/{id}") endpoint
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.put<void>(url, type, {
      headers,
      observe: 'response',
    });
  }

  // Get type by ID
  getTypeById(typeId: string | null): Observable<any> {
    const url = `${this.base_url}/${typeId}`; // Adjusted to match the @GetMapping("/{id}") endpoint
    return this.http.get<any>(url);
  }

  // Create a new type
  createType(type: any): Observable<any> {
    const url = `${this.base_url}`; // Adjusted to match the @PostMapping endpoint
    return this.http.post<any>(url, type);
  }

  // Get type by email
  getTypeByName(name: string): Observable<any> {
    const url = `${this.base_url}/name/${name}`; // Adjusted to match the @GetMapping("/email/{email}") endpoint
    return this.http.get<any>(url);
  }

  getIdByName(name: string): Observable<number> { const url = `${this.base_url}/name/${name}/id`; // Adjusted to match the endpoint for getting ID by name
   return this.http.get<number>(url); }
}
