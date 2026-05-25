import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface MonthlyOccupancy {
  month: string;
  rate: number;
}

export interface ServiceUsage {
  name: string;
  usageCount: number;
}

export interface ReportStats {
  occupancyRate: number;
  monthlyRevenue: number;
  totalReservations: number;
  totalRooms: number;
  cancellations: number;
  monthlyOccupancy: MonthlyOccupancy[];
  topServices: ServiceUsage[];
}

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private readonly baseUrl = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  getGeneralStats(): Observable<ReportStats> {
    return this.http.get<ReportStats>(`${this.baseUrl}/stats`);
  }
}
