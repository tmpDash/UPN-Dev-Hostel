import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StaffUser } from '../../../core/models/staff-user';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class StaffUserService {
  private readonly baseUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  /** List all staff users */
  getUsers(): Observable<StaffUser[]> {
    return this.http.get<StaffUser[]>(this.baseUrl);
  }

  /** Get a user by id */
  getUserById(id: number): Observable<StaffUser> {
    return this.http.get<StaffUser>(`${this.baseUrl}/${id}`);
  }

  /** Create a user */
  createUser(user: StaffUser): Observable<StaffUser> {
    return this.http.post<StaffUser>(this.baseUrl, user);
  }

  /** Update a user */
  updateUser(id: number, user: StaffUser): Observable<StaffUser> {
    return this.http.put<StaffUser>(`${this.baseUrl}/${id}`, user);
  }

  /** Delete a user */
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
