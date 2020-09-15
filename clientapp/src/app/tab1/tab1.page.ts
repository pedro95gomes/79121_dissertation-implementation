import { Component } from '@angular/core';
import { ModalPage} from './modal.page';
import { ModalPage2} from './modal2.page';
import { ModalPage3} from './modal3.page';
import { Router } from  "@angular/router";
import { ActivatedRoute } from '@angular/router';
import { ModalController } from '@ionic/angular';
import { AuthService } from '../auth/auth.service';


@Component({
  selector: 'app-tab1',
  templateUrl: 'tab1.page.html',
  styleUrls: ['tab1.page.scss']
})
export class Tab1Page {

  url = "http://localhost:8080";
  modalCtrl = null;
  id = null;
  email = null;

  constructor(public modalController: ModalController, private router: Router, private route: ActivatedRoute, private authService: AuthService)
  {
    this.route.queryParams.subscribe(params => {
      if (this.router.getCurrentNavigation().extras.state) {
        hideLoading();
        this.id = this.router.getCurrentNavigation().extras.state.user;
        this.email = this.router.getCurrentNavigation().extras.state.email;
        this.updateBalance();
      }
    });
  }

  async presentModal() {
    this.modalCtrl = await this.modalController.create({
      component: ModalPage,
      componentProps: { 
        id: this.id
      },
      cssClass: 'auto-height'
    });
    return await this.modalCtrl.present();
  }

  async presentModalGenTicket() {
    this.modalCtrl = await this.modalController.create({
      component: ModalPage2,
      componentProps: { 
        id: this.id
      }
    });
    return await this.modalCtrl.present();
  }

  async presentModalShowTicket() {
    this.modalCtrl = await this.modalController.create({
      component: ModalPage3,
      componentProps: { 
        id: this.id
      },
      cssClass: 'auto-height'
    });
    return await this.modalCtrl.present();
  }

  updateBalance(){
    showLoading();
    var params = {};
    params["id"] = this.id;
    params["uri"] = "balance";
    this.request(params, "/balance"+"?id="+params["id"], "GET");
  }

  logout(){
    this.authService.logout();
    this.router.navigateByUrl('');
  }

  request(params, uri, method){
    var request = new XMLHttpRequest();
    request.open(method, this.url+uri, true);
    request.onreadystatechange = function() {
        // Check if the request is compete and was successful
        if(this.readyState === 4 && this.status === 200) {
          document.getElementById('inputs').innerHTML = '';
          document.getElementById('transfers').innerHTML = '';
          var dictionary = JSON.parse(this.responseText);
          if(params["uri"] == "balance"){
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
          }
        } else if(this.readyState === 4 && this.status === 503){
          hideLoading();
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

function showInput(label){
  const alert = document.createElement('ion-alert');
  alert.header = "Input";
  alert.message = label;
  alert.buttons = ['OK'];
}

function showTransfer(label){
  const alert = document.createElement('ion-alert');
  alert.header = "Transfer";
  alert.message = label;
  alert.buttons = ['OK'];
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
