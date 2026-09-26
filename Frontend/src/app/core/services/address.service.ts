import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/env';
import { AddressCityOption } from '../models/address-location.dto';

@Injectable({ providedIn: 'root' })
export class AddressService {
  private readonly API_URL = `${environment.apiUrl}/address`;

  constructor(private readonly http: HttpClient) {}

  getStates(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/states`);
  }

  getDistricts(state: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/districts`, { params: { state } });
  }

  getCities(state: string, district: string): Observable<AddressCityOption[]> {
    return this.http.get<AddressCityOption[]>(`${this.API_URL}/cities`, { params: { state, district } });
  }
}
