import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { HotelService } from '../../../core/models/hotel-service';
import { HotelServiceCatalogService } from '../services/hotel-service';

@Component({
  selector: 'app-services-list',
  standalone: false,
  templateUrl: './services-list.html',
  styleUrls: ['./services-list.css']
})
export class ServicesList implements OnInit {
  services$!: Observable<HotelService[]>;

  constructor(private hotelService: HotelServiceCatalogService) {}

  ngOnInit(): void {
    this.loadServices();
  }

  loadServices() {
    this.services$ = this.hotelService.getServices();
  }

  onToggleStatus(service: HotelService) {
    const isCurrentlyActive = service.isActive === true || service.active === true;
    const newStatus = !isCurrentlyActive;
    
    // Optimistic update
    service.isActive = newStatus;
    service.active = newStatus;
    
    this.hotelService.getServiceById(service.id).subscribe(s => {
      s.isActive = newStatus;
      s.active = newStatus;
      this.hotelService.updateService(service.id, s).subscribe({
        next: () => {
          // Success, do nothing as optimistic update already applied
        },
        error: () => {
          // Revert on error
          service.isActive = isCurrentlyActive;
          service.active = isCurrentlyActive;
        }
      });
    });
  }

  onDelete(id: number) {
    if(confirm('¿Está seguro de eliminar este servicio?')) {
      this.hotelService.deleteService(id).subscribe(() => {
        this.loadServices();
      });
    }
  }

  trackByService(index: number, service: HotelService): number {
    return service.id;
  }
}
