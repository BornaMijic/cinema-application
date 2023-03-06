import {Component, OnDestroy} from '@angular/core';
import {NgForm} from "@angular/forms";
import {AuthenticationService} from "../../shared/services/authentication.service";
import {Subscription} from "rxjs";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnDestroy {
  errorMessage = '';
  loading: boolean = false;
  private subscription: Subscription = new Subscription();

  constructor(private authenticationService: AuthenticationService, private router: Router) {
  }

  onSubmit(e: Event, registration: NgForm) {
    e.preventDefault();

    let name = registration.value["name"];
    name = name[0].toUpperCase() + name.slice(1).toLowerCase();
    let surname = registration.value["surname"]
    surname = surname[0].toUpperCase() + surname.slice(1).toLowerCase();

    let registrationUser = {
      username: registration.value["username"],
      password: registration.value["password"],
      confirmPassword: registration.value["confirmPassword"],
      email: registration.value["email"],
      name: name,
      surname: surname
    }
    if (registrationUser.username && registrationUser.password && registrationUser.email && registrationUser.name && registrationUser.surname) {
      this.loading = true;
      let subscription = this.authenticationService.register(registrationUser).subscribe(
        {
          next: (msg: any) => {
            this.errorMessage = '';
            this.loading = false;
            this.router.navigate(['login']);
          },
          error: (error: any) => {
            this.loading = false;
            if (error.error.registrationStringResponse) {
              this.errorMessage = error.error.registrationStringResponse
            } else {
              this.errorMessage = "Došlo je do pogreške";
            }
          }
        }
      );
      this.subscription.add(subscription);
    } else {
      this.errorMessage = "Došlo je do pogreške";
    }
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

}
