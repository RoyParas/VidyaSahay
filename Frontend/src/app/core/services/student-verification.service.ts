import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/env';
import { StudentDetailedResponse, UpdateStudentVerificationRequest } from '../models/student-verification.dto/student-verification.dto';


@Injectable({
  providedIn: 'root'
})
export class StudentVerificationService {

  private readonly API_URL = `${environment.apiUrl}/student-verification`;

  constructor(private http: HttpClient) {}

  updateStatus(
    studentId: string,
    request: UpdateStudentVerificationRequest
  ): Observable<void> {
    return this.http.put<void>(
      `${this.API_URL}/${studentId}/status`,
      request
    );
  }
}