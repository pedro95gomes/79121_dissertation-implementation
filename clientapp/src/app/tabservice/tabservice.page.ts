import { Component, OnInit } from '@angular/core';
import { Router } from  "@angular/router";
import { ActivatedRoute } from '@angular/router';
import { ModalController } from '@ionic/angular';
import { AuthService } from '../auth/auth.service';
import { ModalUpdatePrice} from './modalupdp.page';
import { ModalReceiveTicket } from './modalrctk.page';
import { ModalCheckoutTicket } from './modalcicotk.page';
import { composeAPI } from '@iota/core';

// Create a new instance of the IOTA API object
// Use the `provider` field to specify which node to connect to
const iota = composeAPI({
  provider: 'http://192.168.1.4:14265'
});

@Component({
  selector: 'app-tabservice',
  templateUrl: './tabservice.page.html',
  styleUrls: ['./tabservice.page.scss'],
})
export class TabservicePage{

  url = "http://localhost:8080";
  modalCtrl = null;
  id = null;
  email = null;
  minWeightMagnitude = 9;

  constructor(public modalController: ModalController, private router: Router, private route: ActivatedRoute, private authService: AuthService) {
    this.route.queryParams.subscribe(params => {
      if (this.router.getCurrentNavigation().extras.state) {
        hideLoading();
        this.id = this.router.getCurrentNavigation().extras.state.user;
        this.email = this.router.getCurrentNavigation().extras.state.email;
        this.updateBalance();
      }
    });
  }

  updateBalance(){
    showLoading()
    var request = new XMLHttpRequest();
    request.open("GET", this.url+"/service/balance"+"?id="+this.id, true);
    request.onreadystatechange = function() {
      // Check if the request is compete and was successful
      if(this.readyState === 4 && this.status === 200) {
        document.getElementById('inputs').innerHTML = '';
        document.getElementById('transfers').innerHTML = '';
        var dictionary = JSON.parse(this.responseText);
        var balance = dictionary["balance"];
        var contabbalance = dictionary["contabilisticBalance"];
        var strbal = parseBalance(balance);
        var strcontabbal = parseBalance(contabbalance);
        document.getElementById("balanceid").innerHTML = strbal;
        document.getElementById("contabbalanceid").innerHTML = strcontabbal;
        for(var i in dictionary["transfers"]){
          if(document.getElementById(dictionary["transfers"][i]["bundleHash"].substring(0,10))){
            continue;
          }
          var row = document.createElement("ion-row");
          var divdir = document.createElement("ion-col");
          var divadd = document.createElement("ion-col");
          var divval = document.createElement("ion-col");
          divdir.setAttribute("size","3");
          divadd.setAttribute("size","6");
          divval.setAttribute("size","3");
          if(dictionary["addresses"].includes(dictionary["transfers"][i]["transactions"][0]["address"])){
            divdir.innerHTML = "Received to";
            divadd.innerHTML = dictionary["transfers"][i]["transactions"][0]["address"].substring(0,30)+"...";
            divval.innerHTML = dictionary["transfers"][i]["transactions"][0]["value"];
          } else {
            divdir.innerHTML = "Sent to";
            divadd.innerHTML = dictionary["transfers"][i]["transactions"][0]["address"].substring(0,30)+"...";
            divval.innerHTML = dictionary["transfers"][i]["transactions"][0]["value"];
          }
          row.appendChild(divdir);
          row.appendChild(divadd);
          row.appendChild(divval);
          document.getElementById("transfers").appendChild(row);
        }
        for(var k in dictionary["inputs"]){
          if(document.getElementById(dictionary["inputs"][k]["keyIndex"])){
            continue;
          }
          var irow = document.createElement("ion-row");
          irow.setAttribute("id", dictionary["inputs"][k]["keyIndex"]);
          var idivadd = document.createElement("ion-col");
          var idivval = document.createElement("ion-col");
          idivadd.setAttribute("size","9");
          idivval.setAttribute("size","3");
          idivadd.innerHTML = dictionary["inputs"][k]["address"].substring(0,50)+"...";
          idivval.innerHTML = dictionary["inputs"][k]["balance"];
          irow.appendChild(idivadd);
          irow.appendChild(idivval);
          document.getElementById("inputs").appendChild(irow);
        }
        hideLoading();
        const alert = document.createElement('ion-alert');
        alert.header = 'Balance Updated';
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
    };
    // Sending the request to the server
    request.send();
  }

  //TODO
  updateAddress(){
    var request = new XMLHttpRequest();
    request.open("GET", this.url+"/service/updateaddress"+"?id="+this.id, true);
    request.onreadystatechange = function() {
      // Check if the request is compete and was successful
      if(this.readyState === 4 && this.status === 200) {
        var dictionary = JSON.parse(this.responseText);

        const alert = document.createElement('ion-alert');
        alert.header = 'Address updated';
        alert.message = dictionary["address"] + ' is the new Address';
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
    };
    request.send();
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
      if(localStorage.getItem(arr[index].id+arr[index].bundleId)){
        localStorage.removeItem(arr[index].id+arr[index].bundleId);
      }
      arr.splice(index, 1);
    } 
    let json = JSON.stringify(arr);
    localStorage.setItem('tickets', json);
  }

  async presentModalPrice() {
    this.modalCtrl = await this.modalController.create({
      component: ModalUpdatePrice,
      componentProps: { 
        id: this.id
      },
      cssClass: 'auto-height'
    });
    return await this.modalCtrl.present();
  }

  async presentModalTicket() {
    this.modalCtrl = await this.modalController.create({
      component: ModalReceiveTicket,
      componentProps: { 
        id: this.id
      },
      cssClass: 'auto-height'
    });
    return await this.modalCtrl.present();
  }

  async presentModalCheckoutTicket() {
    this.modalCtrl = await this.modalController.create({
      component: ModalCheckoutTicket,
      componentProps: { 
        id: this.id
      },
      cssClass: 'auto-height'
    });
    return await this.modalCtrl.present();
  }

  logout(){
    this.authService.logout();
    this.router.navigateByUrl('');
  }
}

function hideLoading(){
  document.getElementById("balload").style.display = "none";
  document.getElementById("inpload").style.display = "none";
  document.getElementById("traload").style.display = "none";
 }
 
 function showLoading(){
  document.getElementById("balload").style.display = "block";
  document.getElementById("inpload").style.display = "block";
  document.getElementById("traload").style.display = "block";
 }

function parseBalance(balance){
  var intbal = parseInt(balance);
  if((intbal / 10) < 1){
    return intbal + " IOTA";
  }
  else if((intbal / 10**4) < 1){
    return (intbal/10**3) + " Kilo IOTA ("+intbal+" IOTA)";
  }
  else if((intbal / 10**7) < 1){
    return (intbal/10**6) + " Mega IOTA ("+intbal+" IOTA)";
  }
  else if((intbal / 10**10) < 1){
    return (intbal/10**9) + " Giga IOTA ("+intbal+" IOTA)";
  }
  else if((intbal / 10**13) < 1){
    return (intbal/10**12) + " Tera IOTA ("+intbal+" IOTA)";
  }
  else if((intbal / 10**16) < 1){
    return (intbal/10**15) + " Peta IOTA ("+intbal+" IOTA)";
  }
  return balance + " IOTA ("+intbal+" IOTA)";
}
