import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HotelServiceCatalogService } from '../services/hotel-service';

@Component({
  selector: 'app-service-form',
  standalone: false,
  templateUrl: './service-form.html',
  styleUrls: ['./service-form.css']
})
export class ServiceForm implements OnInit {
  serviceForm: FormGroup;
  isEdit = false;

  constructor(
    private fb: FormBuilder,
    private hotelService: HotelServiceCatalogService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.serviceForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      category: ['GENERAL', Validators.required],
      isActive: [true],
      icon: ['bi-star']
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.isEdit = true;
      this.hotelService.getServiceById(+id).subscribe(svc => {
        if (svc) {
          this.serviceForm.patchValue(svc);
        }
      });
    }
  }

  onSubmit() {
    if (this.serviceForm.valid) {
      const id = this.route.snapshot.params['id'];
      const formValue = this.serviceForm.value;
      const payload = {
        ...formValue,
        active: formValue.isActive
      };

      if (this.isEdit) {
        this.hotelService.updateService(+id, payload).subscribe(() => {
          this.router.navigate(['/services']);
        });
      } else {
        this.hotelService.createService(payload).subscribe(() => {
          this.router.navigate(['/services']);
        });
      }
    }
  }
}
