import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ReportService, ReportStats } from '../../reports/services/report';
import { ReservationService } from '../../reservations/services/reservation';
import { Reservation } from '../../../core/models/reservation';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css'],
  standalone: false
})
export class Dashboard implements OnInit {
  stats?: ReportStats;
  recentReservations: Reservation[] = [];
  loading = true;

  constructor(
    private reportService: ReportService,
    private reservationService: ReservationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('Dashboard: Iniciando carga de estadísticas...');
    
    // Cargar estadísticas generales
    this.reportService.getGeneralStats().subscribe({
      next: (data: ReportStats) => {
        this.stats = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('Dashboard: Error al cargar estadísticas:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });

    // Cargar actividad reciente (últimas 5 reservas)
    this.reservationService.getRecentReservations().subscribe({
      next: (data) => {
        this.recentReservations = data;
        this.cdr.detectChanges();
      }
    });
  }
}
