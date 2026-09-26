import { Injectable } from '@angular/core';
import { BankSummary} from '../models/summaryResponse.dto';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/env'
import { CreateBankRequest } from '../models/create-bank.dto';
import { BankDetails, UpdateBankRequest } from '../models/bank-details.dto';

@Injectable({
  providedIn: 'root'
})
export class BankService {

  private readonly API_URL = `${environment.apiUrl}/bank`;

  constructor(private http: HttpClient) { }

  getBanks(): Observable<BankSummary[]> {
    return this.http.get<BankSummary[]>(`${this.API_URL}/all`);
  }

  createBank(request: CreateBankRequest): Observable<void> {
    return this.http.post<void>(this.API_URL, request);
  }

  getBankById(bankId: string): Observable<BankDetails> {
    return this.http.get<BankDetails>(`${this.API_URL}/${bankId}`);
  }

  updateBank(bankId: string, request: UpdateBankRequest): Observable<void> {
    return this.http.patch<void>(`${this.API_URL}/${bankId}`, request);
  }
}
