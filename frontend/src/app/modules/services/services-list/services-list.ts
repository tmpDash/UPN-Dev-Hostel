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
  services$: Observable<HotelService[]>;

  constructor(private hotelService: HotelServiceCatalogService) {
    this.services$ = this.hotelService.getServices();
  }

  ngOnInit(): void {}

  onToggleStatus(id: number) {
    this.hotelService.getServiceById(id).subscribe(service => {
      service.isActive = !service.isActive;
      this.hotelService.updateService(id, service).subscribe(() => {
        this.services$ = this.hotelService.getServices(); // Refresh
      });
    });
  }
}
