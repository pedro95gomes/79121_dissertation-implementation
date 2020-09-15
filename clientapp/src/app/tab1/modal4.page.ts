import { Component, Input } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { Router } from  "@angular/router";
import { ActivatedRoute } from '@angular/router';
import { NFC, Ndef } from '@ionic-native/nfc/ngx';
import { AlertController } from '@ionic/angular';

@Component({
  selector: 'modal-page4',
  templateUrl: 'modal4.page.html'
})
export class ModalPage4 {

    url = "http://localhost:8080";
    modalCtrl = null;
    id;
    ticket;

    constructor(public alertController: AlertController, public modalController: ModalController, private router: Router, private route: ActivatedRoute, private nfc: NFC, private ndef: Ndef) {
      this.modalCtrl = modalController;
    }

    ngOnInit(){
      var ticket = JSON.parse(this.ticket);
      document.getElementById("cardtitle").innerHTML = ticket.transportType;
      document.getElementById("cardsubtitle").innerHTML = ticket.transportOption;
      document.getElementById("cardcost").innerHTML += ticket.cost;
      document.getElementById("cardexpires").innerHTML += ticket.expires;
      document.getElementById("cardcontent").innerHTML = JSON.stringify(ticket.bundle);
    }

  dismiss() {
    this.modalCtrl.dismiss({
      'dismissed': true
    });
  }

  copyToClipboard = str => {
    const el = document.createElement('textarea');
    el.value = str;
    document.body.appendChild(el);
    el.select();
    document.execCommand('copy');
    document.body.removeChild(el);
  };

  async use() {
    this.copyToClipboard(this.ticket);
    const alert = await this.alertController.create({
      header: 'Ticket copied to clipboard',
      buttons: ['OK']
    });
    await alert.present();
  }
  
}

function onSuccess() {
  console.log("success");
}

function onError() {
  console.log("error");
}

