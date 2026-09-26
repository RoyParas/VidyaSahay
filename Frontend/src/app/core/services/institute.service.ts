import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/env'
import { InstituteAccountSummary, StudentSummary } from '../models/summaryResponse.dto';
import { CreateInstituteRequest } from '../models/create.instititute.dto';
import { InstituteDetails, UpdateInstituteRequest } from '../models/institute-details.dto';

@Injectable({
  providedIn: 'root'
})
export class InstituteService {

  private readonly API_URL = `${environment.apiUrl}/institute`;

  constructor(private http: HttpClient) { }

  getInstitutes(): Observable<InstituteAccountSummary[]> {
    return this.http.get<InstituteAccountSummary[]>(`${this.API_URL}/all`);
  }

  getMyStudents(): Observable<StudentSummary[]> {
    return this.http.get<StudentSummary[]>(`${this.API_URL}/my-students`);
  }

  createInstitute(request: CreateInstituteRequest): Observable<unknown> {
    return this.http.post<unknown>(this.API_URL, request);
  }

  getInstituteById(instituteId: string): Observable<InstituteDetails> {
    return this.http.get<InstituteDetails>(`${this.API_URL}/${instituteId}`);
  }

  updateInstitute(instituteId: string, request: UpdateInstituteRequest): Observable<InstituteDetails> {
    return this.http.patch<InstituteDetails>(`${this.API_URL}/${instituteId}`, request);
  }
}
