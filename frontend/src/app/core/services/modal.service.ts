import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class ModalService {
  
  openConfirm(callback: () => void, cancel?: () => void) {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#0D47A1',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Sí, continuar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        callback();
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        if (cancel) cancel();
      }
    });
  }
}
