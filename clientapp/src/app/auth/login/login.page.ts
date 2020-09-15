import { Component, OnInit } from '@angular/core';
import { Router, NavigationExtras } from  "@angular/router";
import { AuthService } from '../auth.service';
import { AuthProviderService } from '../authprovider.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {

  url = "http://localhost:8080";

  constructor(private  authService:  AuthService, private  authProviderService:  AuthProviderService, private  router:  Router) {   }
  
  ngOnInit() {
  }

  login(form){
    var formValue = form.value;
    var request = new XMLHttpRequest();
    var router = this.router;
    request.open("GET", this.url+"/login"+"?email="+formValue["email"]+"&type="+formValue["type"]+"&password="+formValue["password"], true);
    request.onreadystatechange = function() {
        // Check if the request is complete and was successful
        if(this.readyState === 4 && this.status === 200) {
            var dictionary = JSON.parse(this.responseText);
            var id = dictionary["id"];
            if(id != null){
              let navigationExtras: NavigationExtras = {
                state: {
                  user: id,
                  email: formValue["email"]
                }
              };
              if(formValue["type"] == "Client"){
                router.navigateByUrl('/tabs' , navigationExtras);
              } else if(formValue["type"] == "Service"){
                router.navigateByUrl('/service' , navigationExtras);
              }
            }

            return dictionary["id"];
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
    if(formValue["type"] == "Client"){
      this.authService.login();
    } else if(formValue["type"] == "Service"){
      this.authProviderService.login();
    }
  }


}
