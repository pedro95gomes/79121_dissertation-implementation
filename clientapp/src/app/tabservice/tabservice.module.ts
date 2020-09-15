import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Routes, RouterModule } from '@angular/router';

import { IonicModule } from '@ionic/angular';

import { TabservicePage } from './tabservice.page';
import { ModalUpdatePrice } from './modalupdp.page';
import { ModalReceiveTicket } from './modalrctk.page';
import { ModalCheckoutTicket } from './modalcicotk.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RouterModule.forChild([{path: '', component: TabservicePage}, { path: '', component: ModalUpdatePrice }, { path: '', component: ModalReceiveTicket }, { path: '', component: ModalCheckoutTicket }])
  ],
  declarations: [TabservicePage, ModalUpdatePrice, ModalReceiveTicket, ModalCheckoutTicket]
})
export class TabservicePageModule {}
