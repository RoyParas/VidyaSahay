import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/env';

export interface DocumentTypeOption {
  documentTypeId: string;
  name: string;
  description: string | null;
}

export interface ProfessionOption {
  id: string;
  name: string;
}

export interface CategoryOption {
  id: string;
  code: string;
}

@Injectable({ providedIn: 'root' })
export class SchemeCatalogService {
  private readonly api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getDocuments(): Observable<DocumentTypeOption[]> {
    return this.http.get<DocumentTypeOption[]>(`${this.api}/document/types`);
  }

  getProfessions(): Observable<ProfessionOption[]> {
    return this.http.get<ProfessionOption[]>(`${this.api}/professions`);
  }

  getCategories(): Observable<CategoryOption[]> {
    return this.http.get<CategoryOption[]>(`${this.api}/categories`);
  }
}
