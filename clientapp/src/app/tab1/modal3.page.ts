import { Component, Input } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { Router } from  "@angular/router";
import { ActivatedRoute } from '@angular/router';
import { ModalPage4 } from './modal4.page';

@Component({
  selector: 'modal-page3',
  templateUrl: 'modal3.page.html'
})
export class ModalPage3 {

    url = "http://localhost:8080";
    modalCtrl = null;
    id;
    element;
    elementCICO;

    constructor(public modalController: ModalController, private router: Router, private route: ActivatedRoute) {
      this.modalCtrl = modalController;
    }

    ngOnInit(){
      var bundles = localStorage.getItem('bundles');
      let arr = JSON.parse(bundles);
      if(arr != null){
        arr.forEach((element) => { 
          this.element = element;
          var item = document.createElement("ion-item");
          item.addEventListener("click", () => {
            this.presentModal();
          });
          var label = document.createElement("ion-label");
          label.innerHTML = element.transportOption + " : "+element.cost+" tokens - Expires: "+element.expires;
          item.appendChild(label); 
          document.getElementById("ticketlist").appendChild(item);
        });
      }
      var bundlesCICO = localStorage.getItem('bundlesCICO');
      let arrCICO = JSON.parse(bundlesCICO);
      if(arrCICO != null){
        arrCICO.forEach((element) => { 
          this.elementCICO = element;
          var item = document.createElement("ion-item");
          item.addEventListener("click", () => {
            this.presentModalCICO();
          });
          var label = document.createElement("ion-label");
          label.innerHTML = element.transportType + " : "+element.cost+" tokens - Expires: "+element.expires;
          item.appendChild(label); 
          document.getElementById("ticketlistCICO").appendChild(item);
        });
      }
    }

    async presentModal() {
      var element = JSON.stringify(this.element);
      this.modalCtrl.dismiss();
      this.modalCtrl = await this.modalController.create({
        component: ModalPage4,
        componentProps: { 
          id: this.id,
          ticket: element
        }
      });
      return await this.modalCtrl.present();
    }

    async presentModalCICO() {
      var element = JSON.stringify(this.elementCICO);
      this.modalCtrl.dismiss();
      this.modalCtrl = await this.modalController.create({
        component: ModalPage4,
        componentProps: { 
          id: this.id,
          ticket: element
        }
      });
      return await this.modalCtrl.present();
    }

  dismiss() {
    // using the injected ModalController this page
    // can "dismiss" itself and optionally pass back data
    this.modalCtrl.dismiss({
      'dismissed': true
    });
  }
  
}

