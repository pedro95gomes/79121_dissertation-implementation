import { Component, OnInit } from '@angular/core';
import { Router } from  "@angular/router";
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.page.html',
  styleUrls: ['./register.page.scss'],
})
export class RegisterPage implements OnInit {

  url = "http://localhost:8080";

  constructor(private  authService:  AuthService, private  router:  Router) { }

  ngOnInit() {
  }

  register(form) {;
    var formValue = form.value;
    var name = formValue["name"];
    var email = formValue["email"];
    var password = formValue["password"];
    var confirm = formValue["confirm"];
    var router = this.router;
    var id = null;
    if(password != confirm){
        const alert = document.createElement('ion-alert');
        alert.header = 'Invalid password';
        alert.message = "Passwords don't match";
        alert.buttons = ['OK'];
        document.body.appendChild(alert);
        alert.present();
        return null;
    }
    var request = new XMLHttpRequest();
    request.open("GET", this.url+"/create"+"?name="+name+"&email="+email+"&password="+password, true);
    request.onreadystatechange = function() {
        // Check if the request is complete and was successful
        if(this.readyState === 4 && this.status === 200) {
            var dictionary = JSON.parse(this.responseText);
            var id = dictionary["id"];
            if(id != null){
              router.navigateByUrl('');
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
  }

}
