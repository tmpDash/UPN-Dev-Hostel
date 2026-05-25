import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reservation } from '../../../core/models/reservation';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private readonly baseUrl = `${environment.apiUrl}/reservations`;

  constructor(private http: HttpClient) {}

  /** List all reservations */
  getReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.baseUrl);
  }

  getRecentReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.baseUrl}/recent`);
  }

  /** Get a reservation by id */
  getReservationById(id: number): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.baseUrl}/${id}`);
  }

  /** Create a reservation */
  addReservation(reservation: Reservation): Observable<Reservation> {
    return this.http.post<Reservation>(this.baseUrl, reservation);
  }

  /** Update a reservation */
  updateReservation(id: number, reservation: Reservation): Observable<Reservation> {
    return this.http.put<Reservation>(`${this.baseUrl}/${id}`, reservation);
  }

  /** Delete a reservation */
  deleteReservation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
