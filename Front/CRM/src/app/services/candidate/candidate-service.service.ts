import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Candidate } from '../../pages/pipeline/candidate';

@Injectable({
  providedIn: 'root'
})
export class CandidateServiceService {
  private apiUrl = 'http://localhost:8060/api/candidates';

  constructor(private http: HttpClient) {}

  getCandidates(): Observable<Candidate[]> {
    return this.http.get<Candidate[]>(`${this.apiUrl}/`);
  }

  updateCandidateStatus(id: string, status: string): Observable<Candidate> {
    return this.http.put<Candidate>(`${this.apiUrl}/${id}/status`, { status });
  }

  updateCandidateInformation(id: string, updatedCandidate: Candidate): Observable<Candidate> {
    return this.http.put<Candidate>(`${this.apiUrl}/${id}`, updatedCandidate);
  }

  deleteCandidate(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getProfilePhoto(candidateId: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${candidateId}/profile-photo`, { responseType: 'blob' });
  }

  getCandidate(id: string): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.apiUrl}/${id}`);
  }

  /**
   * Create a new candidate
   * @param candidate - The candidate object to be created
   * @returns Observable with the response
   */
  createCandidate(candidate: any): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<any>(`${this.apiUrl}/`, candidate, { headers });
  }

  createCandidateWithoutheader(candidate: any): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<any>(`${this.apiUrl}/`, candidate);
  }

  uploadDocument(candidateId: any, file: File, documentType: string): Observable<any> {
    const formData = new FormData();
    formData.append('file', file, file.name); // Append file
    formData.append('documentType', documentType); // Append document type

    const url = `${this.apiUrl}/${candidateId}/documents`;

    return this.http.post<any>(url, formData);
  }
}
