import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/env';
import { StudentSummary } from '../models/summaryResponse.dto';
import { StudentDetailedResponse } from '../models/student-verification.dto/student-verification.dto';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  private readonly API_URL = `${environment.apiUrl}/student`;

  constructor(private readonly http: HttpClient) {}

  getStudents(): Observable<StudentSummary[]> {
    return this.http.get<StudentSummary[]>(
      `${this.API_URL}/all`
    );
  }

  getStudentById(
    studentId: string
  ): Observable<StudentDetailedResponse> {
    return this.http.get<StudentDetailedResponse>(
      `${this.API_URL}/${studentId}`
    );
  }
}