import { Component, OnInit } from '@angular/core';
import { KeycloakService } from 'src/app/services/keycloak/keycloak.service';
/* Old imports needed with JWT implementation
import { AuthenticationResponse, AuthenticationResquest } from 'src/app/services/models';
import { AuthenticationService } from 'src/app/services/services';
import { TokenService } from 'src/app/services/token/token.service';
import { Router } from '@angular/router';
*/

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  // authRequest: AuthenticationResquest = {email: '', password: ''};
  // errorMsg: Array<string> = [];

  constructor(
    private keycloakService: KeycloakService
  ) { 

  }

  async ngOnInit(): Promise<void> {
      await this.keycloakService.init();
      await this.keycloakService.login();
  }

  /*login() {
    this.errorMsg = [];
    this.authService.authenticate({
      body: this.authRequest
    }).subscribe({
      next: (res:AuthenticationResponse) => {
        this.tokenService.token = res.token as string;
        this.router.navigate(['books']);
      },
      error: (err) => {
        console.log(err);
        if (err.error.validationErrors) {
          this.errorMsg = err.error.validationErrors;
        } else {
          this.errorMsg.push(err.error.businessErrorDescription);
        }
      } 
    });
  }

  register() {
    this.router.navigate(['register']);
  } */
}
