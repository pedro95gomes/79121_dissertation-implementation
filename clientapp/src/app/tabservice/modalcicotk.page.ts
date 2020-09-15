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
const minWeightMagnitude = 9;

@Component({
  selector: 'modal-page',
  templateUrl: 'modalcicotk.page.html'
})
export class ModalCheckoutTicket {

    url = "http://localhost:8080";
    modalCtrl = null;
    id;

    constructor(public modalController: ModalController, private router: Router, private route: ActivatedRoute) {
      this.modalCtrl = modalController;
    }

  checkOut(formVal){
    var ticket = localStorage.getItem(formVal.value+formVal.valueBundle);
    var parsed = JSON.parse(ticket);
    if(parsed == null){
      const alert = document.createElement('ion-alert');
      alert.header = 'Ticket does not exist';
      alert.message = ""+formVal.value+formVal.valueBundle;
      alert.buttons = ['OK'];
      document.body.appendChild(alert);
      alert.present();
      return;
    }
    var start = parsed["start"];
    var end = Date.now();
    var elapsedTime = end - start;
    this.transactionCheckout(ticket, elapsedTime);
    this.dismiss();
  }

  transactionCheckout(ticket, elapsedTime){
    let parsed = JSON.parse(ticket);
    var request = new XMLHttpRequest();
    request.open("GET", this.url+"/checkouttransaction"+"?id="+parsed.id+"&destType="+parsed.transportType+"&dest="+parsed.transportOption+"&elapsedTime="+elapsedTime+"&branchTransaction="+parsed.bundle[1].branchTransaction+"&trunkTransaction="+parsed.bundle[1].trunkTransaction, true);
    request.onreadystatechange = function() {
      // Check if the request is complete and was successful
      if(this.readyState === 4 && this.status === 200) {
        var response = JSON.parse(this.responseText);
        const alert = document.createElement('ion-alert');
        alert.header = 'Total price';
        alert.message = response.cost + " Tokens";
        alert.buttons = ['OK'];
        document.body.appendChild(alert);
        alert.present();
        var tickets = localStorage.getItem('tickets');
        let arr = JSON.parse(tickets);
        if(arr == null){
          arr = [];
        } 
        arr.push(JSON.stringify(response.bundle));
        let json = JSON.stringify(arr);
        localStorage.setItem('tickets', json);
        ModalCheckoutTicket.submitTickets();
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
  }

  static async submitTickets(){
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
      iota.attachToTangle(ticket[1].trunkTransaction, ticket[1].branchTransaction, minWeightMagnitude, ticket[0]).then(attachedTrytes => {
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
          ModalCheckoutTicket.removeTicketFromArray(i);
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

  static removeTicketFromArray(index){
    var tickets = localStorage.getItem('tickets');
    let arr = JSON.parse(tickets);
    var parsed = JSON.parse(arr[index]);
    console.log(parsed);
    if(arr != null){
      console.log(parsed.id+parsed.bundleId);
      if(localStorage.getItem(parsed.id+parsed.bundleId)){
        localStorage.removeItem(parsed.id+parsed.bundleId);
      }
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

