import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private base_url = 'http://localhost:8089/api/contacts'; // API endpoint defined in the Spring Boot controller

  constructor(private http: HttpClient) {}

  // Get all contacts
  getAllContacts(): Observable<any[]> {
    const url = `${this.base_url}`;
    return this.http.get<any[]>(url);
  }

  // Delete a contact by ID
  deleteContact(id: number): Observable<HttpResponse<void>> {
    const url = `${this.base_url}/${id}`; // Adjusted to match the @DeleteMapping("/{id}") endpoint
    return this.http.delete<void>(url, { observe: 'response' });
  }

  // Update contact by ID
  updateContact(contact: any, id: number): Observable<HttpResponse<void>> {
    const url = `${this.base_url}/${id}`; // Adjusted to match the @PutMapping("/{id}") endpoint
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.put<void>(url, contact, {
      headers,
      observe: 'response',
    });
  }

  // Get contact by ID
  getContactById(contactId: string | null): Observable<any> {
    const url = `${this.base_url}/${contactId}`; // Adjusted to match the @GetMapping("/{id}") endpoint
    return this.http.get<any>(url);
  }

  // Create a new contact
  createContact(contact: any): Observable<any> {
    const url = `${this.base_url}`; // Adjusted to match the @PostMapping endpoint
    return this.http.post<any>(url, contact);
  }

  // Get contact by email
  getContactByEmail(email: string): Observable<any> {
    const url = `${this.base_url}/email/${email}`; // Adjusted to match the @GetMapping("/email/{email}") endpoint
    return this.http.get<any>(url);
  }
}
