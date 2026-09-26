import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/env';
import { ApplicationSummary } from '../models/summaryResponse.dto';
import { ApplicationDetailsResponse } from '../models/application-by-id.models';

export interface ApplyApplicationRequest {
  applicationType: 'LOAN' | 'SCHOLARSHIP';
  schemeId: string;
  requestedLoanAmount?: number;
  academicPercentage?: number;
  loanPurpose?: string;
  repaymentTenureYears?: number;
  coBorrowerName?: string;
  coBorrowerIncome?: number;
  documents: { documentTypeId: string; file: File }[];
}

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {
  private readonly apiUrl = `${environment.apiUrl}/application`;

  constructor(private http: HttpClient) {}

  getMyApplications(): Observable<ApplicationSummary[]> {
    return this.http.get<ApplicationSummary[]>(`${this.apiUrl}/my`);
  }

  apply(request: ApplyApplicationRequest): Observable<ApplicationDetailsResponse> {
    const body = this.toMultipartBody(request);
    return this.http.post<ApplicationDetailsResponse>(`${this.apiUrl}/apply`, body);
  }

  resubmit(applicationId: string, request: ApplyApplicationRequest): Observable<ApplicationDetailsResponse> {
    const body = this.toMultipartBody(request);
    return this.http.post<ApplicationDetailsResponse>(`${this.apiUrl}/${applicationId}/resubmit`, body);
  }

  private toMultipartBody(request: ApplyApplicationRequest): FormData {
    const body = new FormData();
    body.append('applicationType', request.applicationType);
    body.append('schemeId', request.schemeId);
    if (request.requestedLoanAmount !== undefined) body.append('requestedLoanAmount', String(request.requestedLoanAmount));
    if (request.academicPercentage !== undefined) body.append('academicPercentage', String(request.academicPercentage));
    if (request.loanPurpose !== undefined) body.append('loanPurpose', request.loanPurpose);
    if (request.repaymentTenureYears !== undefined) body.append('repaymentTenureYears', String(request.repaymentTenureYears));
    if (request.coBorrowerName !== undefined) body.append('coBorrowerName', request.coBorrowerName);
    if (request.coBorrowerIncome !== undefined) body.append('coBorrowerIncome', String(request.coBorrowerIncome));
    for (const document of request.documents) {
      body.append('documents', document.file, document.file.name);
      body.append('documentTypeIds', document.documentTypeId);
    }
    return body;
  }
}
