import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfirmModal } from './components/confirm-modal/confirm-modal';

@NgModule({
  declarations: [
    ConfirmModal
  ],
  imports: [
    CommonModule
  ],
  exports: [
    ConfirmModal
  ]
})
export class SharedModule { }
