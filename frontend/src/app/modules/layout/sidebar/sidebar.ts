import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Observable } from 'rxjs';
import { LayoutService } from '../../../core/services/layout';
import { AuthService, User } from '../../../core/services/auth';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.css'],
  standalone: false
})
export class Sidebar implements OnInit {
  currentUser$: Observable<User | null>;

  constructor(
    private layoutService: LayoutService, 
    private router: Router,
    private authService: AuthService
  ) {
    this.currentUser$ = this.authService.currentUser$;
  }

  ngOnInit() {
    // Cerrar sidebar al navegar en móviles
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      if (window.innerWidth < 992) {
        this.layoutService.setSidebarVisible(false);
      }
    });
  }

  canAccess(module: string): boolean {
    const user = this.authService.currentUserValue;
    if (!user) return false;

    switch (module) {
      case 'dashboard': return true;
      case 'reservations': return ['ADMIN', 'SUPERVISOR', 'RECEPCIONISTA'].includes(user.role);
      case 'services': return true;
      case 'reports': return ['ADMIN', 'SUPERVISOR'].includes(user.role);
      case 'users': return user.role === 'ADMIN';
      default: return false;
    }
  }

  onLogout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
