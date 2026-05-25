import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { StaffUserService } from '../../users/services/staff-user';
import { StaffUser } from '../../../core/models/staff-user';
import { AuthService, User } from '../../../core/services/auth';
import { Observable } from 'rxjs';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.html',
  styleUrls: ['./profile.css']
})
export class Profile implements OnInit {
  profileForm: FormGroup;
  currentUser$: Observable<User | null>;

  constructor(
    private fb: FormBuilder,
    private staffService: StaffUserService,
    private authService: AuthService
  ) {
    this.currentUser$ = this.authService.currentUser$;
    this.profileForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      department: [''],
      bio: ['']
    });
  }

  ngOnInit(): void {
    const userId = this.authService.currentUserValue?.id;
    if (userId) {
      this.staffService.getUserById(userId).subscribe(user => {
        if (user) {
          this.profileForm.patchValue(user);
        }
      });
    }
  }

  onSubmit() {
    const userId = this.authService.currentUserValue?.id;
    if (this.profileForm.valid && userId) {
      const updatedUser: StaffUser = this.profileForm.value;
      this.staffService.updateUser(userId, updatedUser).subscribe(() => {
        Swal.fire({
          title: '¡Actualizado!',
          text: 'Perfil actualizado con éxito en la base de datos',
          icon: 'success',
          confirmButtonColor: '#0D47A1'
        });
      });
    }
  }
}
