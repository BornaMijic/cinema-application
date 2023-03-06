import {Component, OnDestroy} from '@angular/core';
import {NgForm} from "@angular/forms";
import {AuthenticationService} from "../shared/services/authentication.service";
import {Subscription} from "rxjs";
import {User} from "../shared/models/user";
import {Router} from "@angular/router";

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css']
})
export class AuthenticationComponent implements OnDestroy {
  errorMessage: string = "";
  loading: boolean = false;
  private subscription: Subscription = new Subscription();
  userForVerifying: { email: String, username: String, link: String, codeToVerifyEmail: String } | null = null;
  userVerified = true;
  sendEmail: boolean = false;
  changeEmail = false;
  newEmail: string = "";
  unverifiedUser: User | null = null;
  errorMessageChangeEmail: string = "";


  constructor(private authenticaionService: AuthenticationService, private router: Router) {
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  onSubmit(e: Event, login: NgForm) {
    let username: string = login.value["username"];
    let password: string = login.value["password"];
    login.reset("");
    this.loading = true;
    if (username && password) {
      let user = {username: username, password: password}
      let subscription = this.authenticaionService.login(user).subscribe({
          next: (userWithAdditionalInfo: { username: string, email: string, token: string, verified: boolean, codeToVerifyEmail: string, active: boolean }) => {
            ;
            this.loading = false;
            this.errorMessage = "";
            if (userWithAdditionalInfo.active == false) {
              this.errorMessage = "Korisnički račun je blokiran"
            } else {
              let user: User = new User();
              user.token = userWithAdditionalInfo.token;
              let payloadPartOfToken = userWithAdditionalInfo.token.split(".")[1];
              let userInfo = JSON.parse(window.atob(payloadPartOfToken))
              user.id = userInfo.sub;
              user.username = userWithAdditionalInfo.username;
              user.email = userWithAdditionalInfo.email;
              user.role = userInfo.roles[0].authority;
              this.userVerified = userWithAdditionalInfo.verified;
              this.unverifiedUser = user;
              if (user.username && user.email && user.token && user.role) {
                if (this.userVerified != false) {
                  sessionStorage.setItem('token', user.token);
                  this.authenticaionService.setCurrentUser(user);
                  this.errorMessage = '';
                  this.router.navigate(['']);
                } else {
                  this.userForVerifying = {
                    email: user.email,
                    username: user.username,
                    link: window.location.origin,
                    codeToVerifyEmail: userWithAdditionalInfo.codeToVerifyEmail
                  };
                }
              } else {
                this.errorMessage = "Nepoznata pogreška";
              }
            }

          },
          error: () => {
            this.errorMessage = "Korisnički podatci su krivo napisani";
            this.loading = false;
          }
        }
      );
      this.subscription.add(subscription);
    } else {
      this.errorMessage = "Došlo je do pogreške";
      this.loading = false;
    }
  }

  sendMeVerificationCode(email?: string) {
    this.errorMessageChangeEmail = ""
    let userForVerifying = this.userForVerifying;
    if (email) {
      if (email.endsWith("@gmail.com")) {
        if (userForVerifying) {
          userForVerifying.email = email;
          let subscription = this.authenticaionService.changeEmailAndSendVerificationCode(userForVerifying).subscribe(
            {
              next: () => {
                this.errorMessageChangeEmail = "";
                this.goBack();
                this.sendEmail = true;
                setTimeout(() => this.sendEmail = false, 15000);
              },
              error: (response: { error: { codeToVerifyEmail: string, registrationResponse: { registrationStringResponse: string } } }) => {
                if (response.error == null) {
                  this.errorMessageChangeEmail = "Došlo je do pogreške";
                } else if (response.error.registrationResponse) {
                  if (response.error.registrationResponse.registrationStringResponse == "Korisnik je verificiran, ne možete promjeniti email") {
                    this.goBack(true, response.error.registrationResponse.registrationStringResponse);
                  } else if (response.error.registrationResponse.registrationStringResponse == "Email je zauzet") {
                    this.errorMessageChangeEmail = response.error.registrationResponse.registrationStringResponse;
                  }
                } else {
                  this.errorMessageChangeEmail = "Došlo je do pogreške";
                }

              }
            }
          )
          this.subscription.add(subscription)
        }
      } else {
        this.errorMessageChangeEmail = "Gmail je obavezan";
      }
    } else {
      const regexEmail = new RegExp('[0-9a-z._]+@gmail.com$');
      if (userForVerifying) {
        if (userForVerifying.email.endsWith("@gmail.com")) {
          this.sendEmail = true;
          let subscription = this.authenticaionService.sendVerificationLink(userForVerifying).subscribe(
            {
              next: () => {
                setTimeout(() => this.sendEmail = false, 15000);
                this.errorMessageChangeEmail = "";
              },
              error: (response) => {
                this.errorMessageChangeEmail = "Došlo je do pogreške";
              }
            }
          )
          this.subscription.add(subscription)
        } else {
          if (regexEmail.test(userForVerifying.email.toString())) {
            this.errorMessageChangeEmail = "Gmail koristi ilegalne znakove";
          } else {
            this.errorMessageChangeEmail = "Gmail je obavezan";
          }
        }
      } else {
        this.errorMessageChangeEmail = "Došlo je do pogreške";
      }
    }
  }

  goBack(keep?: boolean, error?: string) {
    this.userForVerifying = null;
    this.userVerified = true;
    this.unverifiedUser = null;
    this.errorMessage = '';
    this.loading = false;
    this.changeEmail = false;
    this.newEmail = "";
    this.sendEmail = false;
    this.errorMessageChangeEmail = ""
    if (keep && error) {
      this.errorMessage = error;
    } else {
      this.errorMessage = "";
    }
  }
}
