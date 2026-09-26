import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/env';
import { ScholarshipSchemeSummary } from '../models/summaryResponse.dto';
import { ScholarshipSchemeDetails } from '../models/scholarship-scheme.model';

@Injectable({providedIn: 'root'})
export class ScholarshipSchemeService {
  private readonly API_URL = `${environment.apiUrl}/scholarship-scheme`;

  constructor(private readonly http: HttpClient) {}

  getScholarshipSchemes(): Observable<ScholarshipSchemeSummary[]> {
    return this.http.get<ScholarshipSchemeSummary[]>(`${this.API_URL}/all`);
  }

  getActiveScholarshipSchemes(): Observable<ScholarshipSchemeSummary[]> {
    return this.http.get<ScholarshipSchemeSummary[]>(`${this.API_URL}/active`);
  }

  getEligibleScholarshipSchemes(annualFamilyIncome: number, academicPercentage: number): Observable<ScholarshipSchemeSummary[]> {
    return this.http.post<ScholarshipSchemeSummary[]>(`${this.API_URL}/eligible`, { annualFamilyIncome, academicPercentage });
  }

  getScholarshipSchemesCreatedByMe(): Observable<ScholarshipSchemeSummary[]> {
    return this.http.get<ScholarshipSchemeSummary[]>(`${this.API_URL}/created-by-me`);
  }

  createScholarshipScheme(request: Record<string, unknown>): Observable<void> {
    return this.http.post<void>(this.API_URL, request);
  }

  updateScholarshipScheme(scholarshipSchemeId: string, request: Record<string, unknown>): Observable<void> {
    return this.http.patch<void>(`${this.API_URL}/${encodeURIComponent(scholarshipSchemeId)}`, request);
  }

  getScholarshipSchemeById(scholarshipSchemeId: string): Observable<ScholarshipSchemeDetails> {
    return this.http.get<ScholarshipSchemeDetails>(`${this.API_URL}/${encodeURIComponent(scholarshipSchemeId)}`);
  }
}
