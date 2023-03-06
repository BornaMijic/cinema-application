import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {AuthenticationService} from "../../shared/services/authentication.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-verify-account',
  templateUrl: './verify-account.component.html',
  styleUrls: ['./verify-account.component.css']
})
export class VerifyAccountComponent implements OnInit, OnDestroy {
  loading: boolean = false;
  verificationSuccessful = false;
  private subscription: Subscription = new Subscription();
  succesMessage: string = "";
  errorMessage: string = "";
  alreadyVerifiedMessage: string = "";

  constructor(private route: ActivatedRoute, private authenticationService: AuthenticationService) {
  }

  ngOnInit(): void {
    this.authenticationService.logout()

    let username = this.route.snapshot.params["username"];
    let codeToVerifyAccount = this.route.snapshot.params["code"];

    this.loading = true;
    if (username && codeToVerifyAccount) {
      this.alreadyVerifiedMessage = "";
      this.succesMessage = "";
      this.errorMessage = "";

      let verificationInfo = {username: username, codeToVerifyAccount: codeToVerifyAccount}
      let subscription = this.authenticationService.verifyAccount(verificationInfo).subscribe(
        {
          next: (response: { text: string }) => {
            this.loading = false;
            this.verificationSuccessful = true;
            this.alreadyVerifiedMessage = "";
            this.succesMessage = response.text;
            this.errorMessage = "";
          },
          error: (error: any) => {
            this.loading = false;
            this.verificationSuccessful = false;
            if (error.error.text == "Korisnički račun je već verificiran") {
              this.alreadyVerifiedMessage = error.error.text;
              this.succesMessage = "";
              this.errorMessage = "";
            } else {
              if (!error.error.text) {
                this.alreadyVerifiedMessage = "";
                this.succesMessage = "";
                this.errorMessage = "Verifikacija nije bila uspješna";
              } else {
                this.alreadyVerifiedMessage = "";
                this.succesMessage = "";
                this.errorMessage = error.error.text;
              }
            }
          }
        }
      )

      this.subscription.add(subscription)
    } else {
      this.loading = false;
      this.verificationSuccessful = false;
    }
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

}
