import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LayoutService } from '../../../core/services/layout';
import { Observable } from 'rxjs';
import { AuthService, User } from '../../../core/services/auth';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  currentUser$: Observable<User | null>;

  constructor(
    private layoutService: LayoutService,
    private authService: AuthService,
    private router: Router
  ) {
    this.currentUser$ = this.authService.currentUser$;
  }

  toggleSidebar() {
    this.layoutService.toggleSidebar();
  }

  onLogout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
