import { Component, Input } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { Router } from  "@angular/router";
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'modal-page',
  templateUrl: 'modal.page.html'
})
export class ModalPage {

    url = "http://localhost:8080";
    modalCtrl = null;
    id;

    constructor(public modalController: ModalController, private router: Router, private route: ActivatedRoute) {
      this.modalCtrl = modalController;
    }

  dismiss() {
    // using the injected ModalController this page
    // can "dismiss" itself and optionally pass back data
    this.modalCtrl.dismiss({
      'dismissed': true
    });
  }

  buy(formVal){
    var request = new XMLHttpRequest();
    request.open("GET", this.url+"/buy"+"?id="+this.id+"&paymentInfo="+formVal.paymentInfo+"&value="+formVal.value, true);
    request.onreadystatechange = function() {
      // Check if the request is complete and was successful
      if(this.readyState === 4 && this.status === 200) {
        var dictionary = JSON.parse(this.responseText);
          const alert = document.createElement('ion-alert');
          alert.header = 'Transaction complete';
          alert.message = formVal.value + ' Tokens were transfered to your account.';
          alert.buttons = ['OK'];

          document.body.appendChild(alert);
          alert.present();
      } else if(this.readyState === 4 && this.status === 503){
        var error = JSON.parse(this.response);
        const alert = document.createElement('ion-alert');
        alert.header = error.error;
        alert.message = error.message;
        alert.buttons = ['OK'];
        document.body.appendChild(alert);
        alert.present();
      }
      }
      request.send();
      this.dismiss();
  }
  
}

