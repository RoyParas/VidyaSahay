import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/env';
import { LoanSchemeSummary } from '../models/summaryResponse.dto';
import { LoanSchemeDetails } from '../models/loan-scheme-detailed.model';

@Injectable({providedIn: 'root'})
export class LoanSchemeService {

  private readonly API_URL = `${environment.apiUrl}/loan-scheme`;

  constructor(private http: HttpClient) { }

  getLoanSchemes(): Observable<LoanSchemeSummary[]> {
    return this.http.get<LoanSchemeSummary[]>(`${this.API_URL}/all`);
  }

  getActiveLoanSchemes(): Observable<LoanSchemeSummary[]> {
    return this.http.get<LoanSchemeSummary[]>(`${this.API_URL}/active`);
  }

  getEligibleLoanSchemes(requiredLoanAmount: number): Observable<LoanSchemeSummary[]> {
    return this.http.post<LoanSchemeSummary[]>(`${this.API_URL}/eligible`, { requiredLoanAmount });
  }

  getLoanSchemesCreatedByMe(): Observable<LoanSchemeSummary[]> {
    return this.http.get<LoanSchemeSummary[]>(`${this.API_URL}/created-by-me`);
  }

  createLoanScheme(request: Record<string, unknown>): Observable<void> {
    return this.http.post<void>(this.API_URL, request);
  }

  updateLoanScheme(loanSchemeId: string, request: Record<string, unknown>): Observable<void> {
    return this.http.patch<void>(`${this.API_URL}/${encodeURIComponent(loanSchemeId)}`, request);
  }

  getLoanSchemeById(loanSchemeId: string): Observable<LoanSchemeDetails> {
    return this.http.get<LoanSchemeDetails>(`${this.API_URL}/${encodeURIComponent(loanSchemeId)}`);
  }
}
