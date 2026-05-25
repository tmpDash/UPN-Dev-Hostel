import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MainLayout } from './main-layout/main-layout';
import { Navbar } from './navbar/navbar';
import { Sidebar } from './sidebar/sidebar';
import { SharedModule } from '../../shared/shared.module';

@NgModule({
  declarations: [
    MainLayout,
    Navbar,
    Sidebar
  ],
  imports: [
    CommonModule,
    RouterModule,
    SharedModule
  ],
  exports: [
    MainLayout,
    Navbar,
    Sidebar
  ]
})
export class LayoutModule { }
