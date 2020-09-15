import { Component, Input } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { Router } from  "@angular/router";
import { ActivatedRoute } from '@angular/router';
import { composeAPI } from '@iota/core';
import * as $ from "jquery";


// Create a new instance of the IOTA API object
// Use the `provider` field to specify which node to connect to
const iota = composeAPI({
  provider: 'http://192.168.0.40:14265'
});

@Component({
  selector: 'modal-page',
  templateUrl: 'modalrctk.page.html'
})
export class ModalReceiveTicket {
    url = "http://localhost:8080";
    modalCtrl = null;
    id;
    minWeightMagnitude = 9;

    constructor(public modalController: ModalController, private router: Router, private route: ActivatedRoute) {
      this.modalCtrl = modalController;
    }

  receiveTicket(formVal){
    //VALIDATE TICKET
    var tickets = localStorage.getItem('tickets');
    let arr = JSON.parse(tickets);
    if(arr == null){
      arr = [];
    } 
    arr.push(formVal.value);
    let json = JSON.stringify(arr);
    localStorage.setItem('tickets', json);
    let form = JSON.parse(formVal.value);
    if(form.cost == "0" && (form.transportType =="Taxi" || form.transportType == "Scooter")){
      var timestamp = Date.now();
      form["start"] =  timestamp;
      let json = JSON.stringify(form);
      localStorage.setItem(form.id+form.bundleId, json);
    }
    this.submitTickets();
    this.dismiss();
  }

  async submitTickets(){
    var tickets = localStorage.getItem('tickets');
    let arr = JSON.parse(tickets);
    if(arr == null){
      const alert = document.createElement('ion-alert');
      alert.header = 'Submit tickets';
      alert.message = 'No tickets to submit.';
      alert.buttons = ['OK'];
      document.body.appendChild(alert);
      alert.present();
      return;
    }
    const alert = document.createElement('ion-alert');
    alert.header = 'Submit tickets';
    alert.message = 'Sending '+arr.length+' tickets.';
    alert.buttons = ['OK'];
    document.body.appendChild(alert);
    alert.present();

    //CHECK NODE AVAILABILITY
    iota.getNodeInfo()
    .then(info => console.log(info))
    .catch(error => {
      const alert = document.createElement('ion-alert');
      alert.header = 'Request Error';
      alert.message = `Node is not available +${error.message}`;
      alert.buttons = ['OK'];

      document.body.appendChild(alert);
      alert.present();
      return;
    });

    var indexes = [];
    var d = new Date();
    var n = d.getTime();
    for (let i=0; i < arr.length; i++){
      var element = arr[i];
      var ticket = JSON.parse(element);
      iota.attachToTangle(ticket.bundle[1].trunkTransaction, ticket.bundle[1].branchTransaction, this.minWeightMagnitude, ticket.bundle[0]).then(attachedTrytes => {
        var n = d.getTime();
        console.log("Attach to tangle Time: "+n);

        const alert = document.createElement('ion-alert');
        alert.header = 'Attached to Tangle';
        alert.message = attachedTrytes+' POW was done.';
        alert.buttons = ['OK'];

        document.body.appendChild(alert);
        alert.present();
        iota.storeAndBroadcast(attachedTrytes).then( storedTrytes => {
          var n = d.getTime();
          console.log("Store and broadcast Time: "+n);
          this.removeTicketFromArray(i);
          const alert = document.createElement('ion-alert');
          alert.header = 'Store and broadcast';
          alert.message = storedTrytes+' was sent to the Tangle.';
          alert.buttons = ['OK'];

          document.body.appendChild(alert);
          alert.present();
        }).catch(err => {
          const alert = document.createElement('ion-alert');
          alert.header = 'Store and broadcast';
          alert.message = "Error on Store and Broadcast";
          alert.buttons = ['OK'];

          document.body.appendChild(alert);
          alert.present();
        });
      })
      .catch(err => {
        const alert = document.createElement('ion-alert');
        alert.header = 'Attach to Tangle';
        alert.message = "Error on Attach to Tangle";
        alert.buttons = ['OK'];

        document.body.appendChild(alert);
        alert.present();
      });
    }
  }

  removeTicketFromArray(index){
    var tickets = localStorage.getItem('tickets');
    let arr = JSON.parse(tickets);
    if(arr != null){
      arr.splice(index, 1);
    } 
    let json = JSON.stringify(arr);
    localStorage.setItem('tickets', json);
  }

  dismiss() {
    // using the injected ModalController this page
    // can "dismiss" itself and optionally pass back data
    this.modalCtrl.dismiss({
      'dismissed': true
    });
  }
  
}

