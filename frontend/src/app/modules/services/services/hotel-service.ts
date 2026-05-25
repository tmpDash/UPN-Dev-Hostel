import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HotelService } from '../../../core/models/hotel-service';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class HotelServiceCatalogService {
  private readonly baseUrl = `${environment.apiUrl}/servicios`;

  constructor(private http: HttpClient) {}

  /** List all services in catalog */
  getServices(): Observable<HotelService[]> {
    return this.http.get<HotelService[]>(this.baseUrl);
  }

  /** Get a service by id */
  getServiceById(id: number): Observable<HotelService> {
    return this.http.get<HotelService>(`${this.baseUrl}/${id}`);
  }

  /** Create a service */
  createService(service: HotelService): Observable<HotelService> {
    return this.http.post<HotelService>(this.baseUrl, service);
  }

  /** Update a service */
  updateService(id: number, service: HotelService): Observable<HotelService> {
    return this.http.put<HotelService>(`${this.baseUrl}/${id}`, service);
  }

  /** Delete a service */
  deleteService(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
