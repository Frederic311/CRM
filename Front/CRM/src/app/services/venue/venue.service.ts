import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class VenueService {
  private base_url = 'http://localhost:8089/api/venues';

  constructor(private http: HttpClient) { }
  getAllVenues(): Observable<any[]> {
    const url = `${this.base_url}`;
    return this.http.get<any[]>(url);
  }

  // Delete a contact by ID
  deleteVenue(id: number): Observable<HttpResponse<void>> {
    const url = `${this.base_url}/${id}`; // Adjusted to match the @DeleteMapping("/{id}") endpoint
    return this.http.delete<void>(url, { observe: 'response' });
  }

  // Update contact by ID
  updateVenue(venue: any, id: number): Observable<HttpResponse<void>> {
    const url = `${this.base_url}/${id}`; // Adjusted to match the @PutMapping("/{id}") endpoint
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.put<void>(url, venue, {
      headers,
      observe: 'response',
    });
  }

  // Get contact by ID
  getVenueById(venueId: string | null): Observable<any> {
    const url = `${this.base_url}/${venueId}`; // Adjusted to match the @GetMapping("/{id}") endpoint
    return this.http.get<any>(url);
  }

  // Create a new contact
  createVenue(venue: any): Observable<any> {
    const url = `${this.base_url}`; // Adjusted to match the @PostMapping endpoint
    return this.http.post<any>(url, venue);
  }

  getIdByName(name: string): Observable<number> { const url = `${this.base_url}/name/${name}`; // Adjusted to match the endpoint for getting ID by name
   return this.http.get<number>(url); }

}
