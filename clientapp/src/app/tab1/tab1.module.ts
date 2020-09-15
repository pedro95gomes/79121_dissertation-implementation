import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Tab1Page } from './tab1.page';
import { ModalPage } from './modal.page';
import { ModalPage2 } from './modal2.page';
import { ModalPage3 } from './modal3.page';
import { ModalPage4 } from './modal4.page';

@NgModule({
  imports: [
    IonicModule,
    CommonModule,
    FormsModule,
    RouterModule.forChild([{ path: '', component: Tab1Page },{ path: '', component: ModalPage },{ path: '', component: ModalPage2 },{ path: '', component: ModalPage3 },{ path: '', component: ModalPage4 }])
  ],
  declarations: [Tab1Page, ModalPage, ModalPage2, ModalPage3, ModalPage4]
})
export class Tab1PageModule {}
