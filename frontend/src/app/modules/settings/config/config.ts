import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-config',
  standalone: false,
  templateUrl: './config.html',
  styleUrls: ['./config.css']
})
export class Config implements OnInit {
  settings = {
    currency: 'USD',
    language: 'ES',
    emailNotifications: true,
    autoBackup: false,
    theme: 'LIGHT'
  };

  constructor() { }

  ngOnInit(): void { }

  onSave() {
    Swal.fire({
      title: 'Configuración',
      text: 'Configuración guardada correctamente.',
      icon: 'success',
      confirmButtonColor: '#0D47A1'
    });
  }
}
