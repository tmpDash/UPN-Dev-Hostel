import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ReservationService } from '../services/reservation';
import { HotelServiceCatalogService } from '../../services/services/hotel-service';
import { HotelService } from '../../../core/models/hotel-service';
import { ReservationServiceDetail } from '../../../core/models/reservation';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-reservation-form',
  standalone: false,
  templateUrl: './reservation-form.html',
  styleUrls: ['./reservation-form.css']
})
export class ReservationForm implements OnInit {
  reservationForm: FormGroup;
  availableServices: HotelService[] = [];
  isEdit = false;
  roomPrices: { [key: string]: number } = {
    'SIMPLE': 100,
    'DOBLE': 180,
    'SUITE': 350
  };

  constructor(
    private fb: FormBuilder,
    private reservationService: ReservationService,
    private hotelService: HotelServiceCatalogService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.reservationForm = this.fb.group({
      customerName: ['', Validators.required],
      roomType: ['SIMPLE', Validators.required],
      checkInDate: ['', Validators.required],
      checkOutDate: ['', Validators.required],
      status: ['PENDIENTE', Validators.required],
      addedServices: this.fb.array([]),
      totalPrice: [0]
    });
  }

  ngOnInit(): void {
    const id = this.router.url.includes('/edit/') ? this.router.url.split('/').pop() : null;
    if (id && !isNaN(Number(id))) {
      this.isEdit = true;
      this.reservationService.getReservationById(+id).subscribe(res => {
        if (res) {
          // Primero parchear los valores planos
          this.reservationForm.patchValue({
            customerName: res.customerName,
            roomType: res.roomType,
            checkInDate: res.checkInDate,
            checkOutDate: res.checkOutDate,
            status: res.status,
            totalPrice: res.totalPrice
          });
          
          // Luego poblar el FormArray de servicios
          if (res.addedServices && res.addedServices.length > 0) {
            this.addedServices.clear();
            res.addedServices.forEach(s => {
              this.addedServices.push(this.fb.group({
                id: [s.id], // IMPORTANTE: incluir ID para persistencia
                serviceId: [s.serviceId],
                name: [s.name],
                quantity: [s.quantity, [Validators.required, Validators.min(1)]],
                price: [s.price]
              }));
            });
          }
        }
      });
    }

    this.hotelService.getServices().subscribe({
      next: (services) => {
        this.availableServices = services.filter(s => s.isActive === true || s.active === true || (s as any).isActive === 'true' || (s as any).active === 'true');
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching services:', err);
      }
    });

    // Escuchar cambios para recalcular precio
    this.reservationForm.valueChanges.subscribe(() => {
      this.calculateTotal();
    });
  }

  get addedServices() {
    return this.reservationForm.get('addedServices') as FormArray;
  }

  addService(service: HotelService) {
    const existingIndex = this.addedServices.controls.findIndex(c => c.value.serviceId === service.id);
    
    if (existingIndex >= 0) {
      const control = this.addedServices.at(existingIndex);
      control.patchValue({ quantity: control.value.quantity + 1 });
    } else {
      this.addedServices.push(this.fb.group({
        id: [null], // Nuevo servicio no tiene ID aún
        serviceId: [service.id],
        name: [service.name],
        quantity: [1, [Validators.required, Validators.min(1)]],
        price: [service.price]
      }));
    }
  }

  removeService(index: number) {
    this.addedServices.removeAt(index);
  }

  calculateTotal() {
    const values = this.reservationForm.getRawValue();
    let total = 0;

    // Cálculo por noches
    if (values.checkInDate && values.checkOutDate) {
      const start = new Date(values.checkInDate);
      const end = new Date(values.checkOutDate);
      const diffTime = Math.abs(end.getTime() - start.getTime());
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays > 0) {
        total += (this.roomPrices[values.roomType] || 0) * diffDays;
      }
    }

    // Cálculo por servicios con cantidades
    values.addedServices.forEach((s: any) => {
      total += (s.price * s.quantity);
    });

    // Actualizar solo si hay cambios para evitar bucles infinitos
    if (this.reservationForm.get('totalPrice')?.value !== total) {
      this.reservationForm.get('totalPrice')?.patchValue(total, { emitEvent: false });
    }
  }

  onSubmit() {
    if (this.reservationForm.valid) {
      const id = this.router.url.split('/').pop();
      const payload = this.reservationForm.value;
      
      const obs = this.isEdit && id ? 
        this.reservationService.updateReservation(+id, payload) : 
        this.reservationService.addReservation(payload);

      obs.subscribe(() => {
        this.router.navigate(['/reservations']);
      });
    }
  }
}
