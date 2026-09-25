import {
  HttpClient,
  HttpResponse
} from '@angular/common/http';

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import {
  ApplicationDetailsResponse,
  ApplicationDocument,
  ApplicationStatusRequest,
  DocumentStatusRequest
} from '../models/application-by-id.models';

@Injectable({
  providedIn: 'root'
})
export class ApplicationByIdService {

  /*
   * If Angular directly calls Spring Boot:
   *
   * http://localhost:8080
   *
   * If Angular proxy is configured:
   *
   * ''
   */
  private readonly apiBaseUrl = 'http://localhost:8080';

  constructor(
    private readonly http: HttpClient
  ) {}

  getApplicationById(
    applicationId: string
  ): Observable<ApplicationDetailsResponse> {
    return this.http.get<ApplicationDetailsResponse>(
      `${this.apiBaseUrl}/api/application/${applicationId}`
    );
  }

  updateApplicationStatus(
    request: ApplicationStatusRequest
  ): Observable<unknown> {
    return this.http.patch<unknown>(
      `${this.apiBaseUrl}/api/application/status`,
      request
    );
  }

  updateDocumentStatus(
    documentId: string,
    request: DocumentStatusRequest
  ): Observable<ApplicationDocument> {
    return this.http.patch<ApplicationDocument>(
      `${this.apiBaseUrl}/api/document/${documentId}/status`,
      request
    );
  }

  getDocumentFile(
    fileUrl: string
  ): Observable<HttpResponse<Blob>> {
    return this.http.get(
      this.resolveApiUrl(fileUrl),
      {
        responseType: 'blob',
        observe: 'response'
      }
    );
  }

  resolveApiUrl(fileUrl: string): string {
    const trimmedUrl = fileUrl.trim();

    if (
      trimmedUrl.startsWith('http://') ||
      trimmedUrl.startsWith('https://')
    ) {
      return trimmedUrl;
    }

    const normalizedPath = trimmedUrl.startsWith('/')
      ? trimmedUrl
      : `/${trimmedUrl}`;

    return `${this.apiBaseUrl}${normalizedPath}`;
  }
}