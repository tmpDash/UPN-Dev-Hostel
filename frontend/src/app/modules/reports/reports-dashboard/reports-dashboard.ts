import { Component, OnInit } from '@angular/core';
import { ModalService } from '../../../core/services/modal.service';
import { ReportService, MonthlyOccupancy, ServiceUsage } from '../services/report';

@Component({
  selector: 'app-reports-dashboard',
  standalone: false,
  templateUrl: './reports-dashboard.html',
  styleUrls: ['./reports-dashboard.css']
})
export class ReportsDashboard implements OnInit {
  
  reportsData: any[] = [];
  monthlyOccupancy: MonthlyOccupancy[] = [];
  topServices: ServiceUsage[] = [];

  constructor(
    private modalService: ModalService,
    private reportService: ReportService
  ) { }

  ngOnInit(): void {
    this.reportService.getGeneralStats().subscribe(stats => {
      this.reportsData = [
        { label: 'Ocupación', value: stats.occupancyRate, unit: '%', color: 'bg-primary', progress: stats.occupancyRate },
        { label: 'Ingresos Totales', value: stats.monthlyRevenue, unit: 'S/ ', color: 'bg-success', progress: 100 },
        { label: 'Reservas Totales', value: stats.totalReservations, unit: '', color: 'bg-info', progress: 100 },
        { label: 'Cancelaciones', value: stats.cancellations, unit: '', color: 'bg-danger', progress: 100 }
      ];

      this.monthlyOccupancy = stats.monthlyOccupancy;
      this.topServices = stats.topServices;
    });
  }

  exportData() {
    this.modalService.openConfirm(() => {
      console.log('Reporte exportado');
    });
    // Personalizar mensaje (Opcional, pero por ahora uso el default)
  }

}
