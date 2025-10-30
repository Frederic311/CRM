// task.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private baseUrl = 'http://localhost:8084/tasks';

  constructor(private http: HttpClient) {}

  // Get all tasks
  getAllTasks(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}`);
  }

  // Get tasks by candidate ID
  getTasksByCandidateId(candidateId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/${candidateId}`);
  }

  // Get tasks by status
  getTasksByStatus(status: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/status/${status}`);
  }

  // Create a new task
  createTask(task: any): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<any>(this.baseUrl, task, { headers });
  }

  // Update an existing task
  updateTask(taskId: number, taskData: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${taskId}`, taskData);
  }


  // Delete a task
  deleteTask(id: number): Observable<HttpResponse<void>> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { observe: 'response' });
  }

  // Mark a task as completed
  // task.service.ts
  markTaskAsCompleted(id: number): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/${id}/complete`, {});
  }

  getTaskById(taskId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${taskId}`);
  }

}
