import { Component, Input } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { Router } from  "@angular/router";
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'modal-page2',
  templateUrl: 'modal2.page.html'
})
export class ModalPage2 {

    url = "http://localhost:8080";
    modalCtrl = null;
    id;
    public isSourceHidden: boolean;
    public isDestHidden: boolean;
    public isUberPriceHidden: boolean;
    public isUberPriceFormHidden: boolean;

    constructor(public modalController: ModalController, private router: Router, private route: ActivatedRoute) {
      this.isUberPriceFormHidden = true;
      this.modalCtrl = modalController;
    }

    ionViewWillLoad(){
      this.isUberPriceFormHidden = true;
    }

  dismiss() {
    // using the injected ModalController this page
    // can "dismiss" itself and optionally pass back data
    this.modalCtrl.dismiss({
      'dismissed': true
    });
  }

  getPrice(transport){
    var request = new XMLHttpRequest();
    request.open("GET", this.url+"/price?id="+transport, true);
    request.onreadystatechange = function() {
        // Check if the request is compete and was successful
        if(this.readyState === 4 && this.status === 200) {
          var dictionary = JSON.parse(this.responseText);
          var head = document.createElement("ion-list-header");
          var lab = document.createElement("ion-label");
          lab.innerHTML = "Options";
          head.appendChild(lab);
          document.getElementById("transportOption").appendChild(head);
          Object.entries(dictionary["price"]).forEach(([key, value]) => {
            if(value){
              var item = document.createElement("ion-item");
              var label = document.createElement("ion-label");
              if(transport == "Metro" || transport == "Bus" || transport == "Uber"){
                label.innerHTML = key+ ": "+value+" tokens";
              } else if(transport == "Taxi"){
                label.innerHTML = key+ ": Average of "+value+" tokens per km";
              } else if(transport == "Scooter"){
                label.innerHTML = key+ ": Average of "+value+" tokens per min";
              }
              var radio = document.createElement("ion-radio");
              radio.setAttribute("slot", "start");
              radio.setAttribute("name", key);
              radio.setAttribute("value",key);
              item.appendChild(label);
              item.appendChild(radio);
              document.getElementById("transportOption").appendChild(item);
            }
          });
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

  getUberPrice(formVal){
    var request = new XMLHttpRequest();
    request.open("GET", this.url+"/priceuber?id=Uber&source="+formVal.source+"&destination="+formVal.destination, true);
    request.onreadystatechange = function() {
      // Check if the request is compete and was successful
      if(this.readyState === 4 && this.status === 200) {
        var dictionary = JSON.parse(this.responseText);
        var head = document.createElement("ion-list-header");
        var lab = document.createElement("ion-label");
        lab.innerHTML = "Options";
        head.appendChild(lab);
        document.getElementById("transportOption").appendChild(head);
        Object.entries(dictionary["price"]).forEach(([key, value]) => {
          if(value){
            var item = document.createElement("ion-item");
            var label = document.createElement("ion-label");
            label.innerHTML = key+ ": "+value+" tokens";
            var radio = document.createElement("ion-radio");
            radio.setAttribute("slot", "start");
            radio.setAttribute("name", key);
            radio.setAttribute("value",key);
            item.appendChild(label);
            item.appendChild(radio);
            document.getElementById("transportOption").appendChild(item);
          }
        });
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

  onChange(event){
    var transportOption = event.detail.value;
    if(transportOption == "Uber"){
      this.showItinerary();
    } else if(transportOption == "Metro" || transportOption == "Bus"){
      this.hideItinerary();
      this.getPrice(transportOption);
    } else{
      this.hideItinerary();
    }
    document.getElementById("transportOption").innerHTML = "";
  }

  buy(formVal){
    var request = new XMLHttpRequest();
    request.open("GET", this.url+"/transaction"+"?id="+this.id+"&destType="+formVal.transportInfo+"&dest="+formVal.transportOption, true);
    request.onreadystatechange = function() {
      // Check if the request is complete and was successful
      if(this.readyState === 4 && this.status === 200) {
        var dictionary = JSON.parse(this.responseText);
        var timestamp = Date.now();
        var bundles = localStorage.getItem('bundles');
        let arr = JSON.parse(bundles);
        if(arr == null){
          arr = [];
        } 
        arr.push(dictionary);
        let json = JSON.stringify(arr);
        localStorage.setItem('bundles', json);
        ticketGeneratedAlert();
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

  buyCICO(formVal){
    var request = new XMLHttpRequest();
    request.open("GET", this.url+"/checkintransaction"+"?id="+this.id+"&destType="+formVal.transportInfoCICO+"&dest="+formVal.transportOptionCICO, true);
    request.onreadystatechange = function() {
      // Check if the request is complete and was successful
      if(this.readyState === 4 && this.status === 200) {
        var dictionary = JSON.parse(this.responseText);
        var timestamp = Date.now();
        var bundles = localStorage.getItem('bundlesCICO');
        let arr = JSON.parse(bundles);
        if(arr == null){
          arr = [];
        } 
        arr.push(dictionary);
        let json = JSON.stringify(arr);
        localStorage.setItem('bundlesCICO', json);
        ticketGeneratedAlert();
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

  onChangeOption(event){
    // DO NOTHING YET
  }

  showItinerary(){
    //this.isSourceHidden = false;
    //this.isDestHidden = false;
    //this.isUberPriceHidden = false;
    this.isUberPriceFormHidden = false;
  }

  hideItinerary(){
    //this.isSourceHidden = true;
    //this.isDestHidden = true;
    //this.isUberPriceHidden = true;
    this.isUberPriceFormHidden = true;
  }
  
}

function ticketGeneratedAlert(){
  const alert = document.createElement('ion-alert');
  alert.header = 'Ticket generated successfully';
  alert.buttons = ['OK'];

  document.body.appendChild(alert);
  alert.present();
}

