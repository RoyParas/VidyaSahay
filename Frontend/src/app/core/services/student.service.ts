import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/env';
import { StudentSummary } from '../models/summaryResponse.dto';
import { StudentDetailedResponse } from '../models/student-verification.dto/student-verification.dto';

export interface CompleteStudentProfileRequest {
  instituteId: string;
  courseId: string;
  categoryId: string;
  addressId: string;
  location: string | null;
  pincode: number;
  aadharNumber: string;
  gender: string;
  dateOfBirth: string;
  fatherName: string;
  motherName: string;
  annualFamilyIncome: number;
}

export interface UpdateMyStudentProfileRequest extends Omit<CompleteStudentProfileRequest, 'aadharNumber'> {
  aadharNumber: string | null;
}

export interface CourseOption {
  id: string;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  private readonly API_URL = `${environment.apiUrl}/student`;

  constructor(private readonly http: HttpClient) {}

  getStudents(): Observable<StudentSummary[]> {
    return this.http.get<StudentSummary[]>(`${this.API_URL}/all`);
  }

  getStudentById(studentId: string): Observable<StudentDetailedResponse> {
    return this.http.get<StudentDetailedResponse>(`${this.API_URL}/${studentId}`);
  }

  getMyProfile(): Observable<StudentDetailedResponse> {
    return this.http.get<StudentDetailedResponse>(`${this.API_URL}/me`);
  }

  getCoursesByInstitute(instituteId: string): Observable<CourseOption[]> {
    return this.http.get<CourseOption[]>(`${environment.apiUrl}/courses/institute/${instituteId}`);
  }

  completeProfile(request: CompleteStudentProfileRequest): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/complete-profile`, request);
  }

  updateMyProfile(request: UpdateMyStudentProfileRequest): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/me/profile`, request);
  }
}
