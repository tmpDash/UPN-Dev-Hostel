import { Component, OnInit } from '@angular/core';
import { Observable, BehaviorSubject, switchMap } from 'rxjs';
import { Reservation } from '../../../core/models/reservation';
import { ReservationService } from '../services/reservation';
import { ModalService } from '../../../core/services/modal.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-reservation-list',
  standalone: false,
  templateUrl: './reservation-list.html',
  styleUrls: ['./reservation-list.css']
})
export class ReservationList implements OnInit {
  private refresh$ = new BehaviorSubject<void>(undefined);
  reservations$: Observable<Reservation[]>;

  constructor(
    private reservationService: ReservationService,
    private modalService: ModalService
  ) {
    this.reservations$ = this.refresh$.pipe(
      switchMap(() => this.reservationService.getReservations())
    );
  }

  ngOnInit(): void {}

  onDelete(id: number) {
    this.modalService.openConfirm(() => {
      this.reservationService.deleteReservation(id).subscribe({
        next: () => {
          Swal.fire({
            title: '¡Eliminado!',
            text: 'Reserva eliminada con éxito.',
            icon: 'success',
            confirmButtonColor: '#0D47A1'
          });
          this.refresh$.next();
        },
        error: (err) => {
          console.error('Error al borrar reserva:', err);
          Swal.fire({
            title: 'Error',
            text: 'No se pudo eliminar la reserva.',
            icon: 'error',
            confirmButtonColor: '#0D47A1'
          });
        }
      });
    });
  }
}
