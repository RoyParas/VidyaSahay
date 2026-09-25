import { Injectable } from '@angular/core';
import { BankSummary} from '../models/summaryResponse.dto';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/env'

@Injectable({
  providedIn: 'root'
})
export class BankService {

  private readonly API_URL = `${environment.apiUrl}/bank`;

  constructor(private http: HttpClient) { }

  getBanks(): Observable<BankSummary[]> {
    return this.http.get<BankSummary[]>(`${this.API_URL}/all`);
  }
}
