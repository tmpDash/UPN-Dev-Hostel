import { Component, OnInit } from '@angular/core';
import { Observable, BehaviorSubject, switchMap } from 'rxjs';
import { StaffUser } from '../../../core/models/staff-user';
import { StaffUserService } from '../services/staff-user';
import { ModalService } from '../../../core/services/modal.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-users-list',
  standalone: false,
  templateUrl: './users-list.html',
  styleUrls: ['./users-list.css']
})
export class UsersList implements OnInit {
  private refresh$ = new BehaviorSubject<void>(undefined);
  users$: Observable<StaffUser[]>;

  constructor(
    private staffService: StaffUserService,
    private modalService: ModalService
  ) {
    this.users$ = this.refresh$.pipe(
      switchMap(() => this.staffService.getUsers())
    );
  }

  ngOnInit(): void {}

  onDelete(id: number) {
    this.modalService.openConfirm(() => {
      this.staffService.deleteUser(id).subscribe({
        next: () => {
          Swal.fire({
            title: '¡Eliminado!',
            text: 'Usuario eliminado con éxito.',
            icon: 'success',
            confirmButtonColor: '#0D47A1'
          });
          this.refresh$.next(); // Dispara el refresco automático
        },
        error: (err) => {
          console.error('Error al borrar usuario:', err);
          Swal.fire({
            title: 'Error',
            text: 'No se pudo eliminar el usuario.',
            icon: 'error',
            confirmButtonColor: '#0D47A1'
          });
        }
      });
    });
  }
}
