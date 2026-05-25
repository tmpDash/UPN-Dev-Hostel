import { Component, OnInit } from '@angular/core';
import { LayoutService } from '../../../core/services/layout';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-main-layout',
  standalone: false,
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.css',
})
export class MainLayout implements OnInit {
  sidebarVisible$: Observable<boolean>;

  constructor(private layoutService: LayoutService) {
    this.sidebarVisible$ = this.layoutService.sidebarVisible$;
  }

  ngOnInit() {
    // Cerrar sidebar por defecto en móviles
    if (window.innerWidth < 768) {
      this.layoutService.setSidebarVisible(false);
    }
  }

  toggleSidebar() {
    this.layoutService.toggleSidebar();
  }
}
