import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { AuthProviderService } from '../auth/authprovider.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService {

  constructor(
    public authenticationService: AuthService,
    public authenticationProviderService: AuthProviderService
      ) {}

  canActivate(): boolean {
    return (this.authenticationService.authenticated() || this.authenticationProviderService.authenticated());
  }
}
