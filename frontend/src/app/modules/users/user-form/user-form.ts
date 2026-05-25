import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { StaffUserService } from '../services/staff-user';

@Component({
  selector: 'app-user-form',
  standalone: false,
  templateUrl: './user-form.html',
  styleUrls: ['./user-form.css']
})
export class UserForm implements OnInit {
  userForm: FormGroup;
  isEdit = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private staffService: StaffUserService
  ) {
    this.userForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      role: ['RECEPCIONISTA', Validators.required],
      status: ['ACTIVO', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    const id = this.router.url.includes('/edit/') ? this.router.url.split('/').pop() : null;
    if (id && !isNaN(Number(id))) {
      this.isEdit = true;
      this.staffService.getUserById(+id).subscribe(user => {
        if (user) {
          this.userForm.patchValue(user);
          // Quitar requerimiento de contraseña al editar
          this.userForm.get('password')?.setValidators([Validators.minLength(6)]);
          this.userForm.get('password')?.updateValueAndValidity();
        }
      });
    }
  }

  onSubmit() {
    if (this.userForm.valid) {
      const id = this.router.url.split('/').pop();
      const obs = this.isEdit && id ? 
        this.staffService.updateUser(+id, this.userForm.value) : 
        this.staffService.createUser(this.userForm.value);

      obs.subscribe(() => {
        this.router.navigate(['/users']);
      });
    }
  }
}
