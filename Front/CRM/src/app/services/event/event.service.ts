import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private base_url = 'http://localhost:8089/api/events'; // API endpoint for events

  constructor(private http: HttpClient) {}

  // Get all events
  getAllEvents(): Observable<any[]> {
    const url = `${this.base_url}`;
    return this.http.get<any[]>(url);
  }

  // Delete an event by ID
  deleteEvent(id: number): Observable<HttpResponse<void>> {
    const url = `${this.base_url}/${id}`;
    return this.http.delete<void>(url, { observe: 'response' });
  }

  // Update an event by ID
  updateEvent(event: any, id: number): Observable<HttpResponse<void>> {
    const url = `${this.base_url}/${id}`;
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.put<void>(url, event, {
      headers,
      observe: 'response',
    });
  }

  // Get event by ID
  getEventById(eventId: string | null): Observable<any> {
    const url = `${this.base_url}/${eventId}`;
    return this.http.get<any>(url);
  }

  // Create a new event
  createEvent(event: any): Observable<any> {
    const url = `${this.base_url}`;
    return this.http.post<any>(url, event);
  }

}
