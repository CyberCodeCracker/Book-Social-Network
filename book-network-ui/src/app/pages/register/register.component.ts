import { Token } from '@angular/compiler';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegistrationRequest } from 'src/app/services/models';
import { AuthenticationService } from 'src/app/services/services';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

  registrationRequest: RegistrationRequest = {email: '', firstName: '', lastName: '', password: ''};
  errorMsg: Array<string> = [];

  constructor(
    private router: Router,
    private authService: AuthenticationService,
  ) {

  }

  register() {
    this.errorMsg = [];
    this.authService.register({
      body: this.registrationRequest 
    }).subscribe({
      next: () => {
        this.router.navigate(['navigate-account']);
      },
      error: (err) => {
        this.errorMsg = err.error.validationErrors;
      }
    })
  }

  login() {
    this.router.navigate(['login']);
  }
}
