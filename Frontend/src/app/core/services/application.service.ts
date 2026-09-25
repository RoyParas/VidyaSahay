import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/env';
import { ApplicationSummary } from '../models/summaryResponse.dto';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {
  private readonly apiUrl = `${environment.apiUrl}/application`;

  constructor(private http: HttpClient) {}

  getMyApplications(): Observable<ApplicationSummary[]> {
    return this.http.get<ApplicationSummary[]>(`${this.apiUrl}/my`);
  }
}
