import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "./shared/services/authentication.service";
import {User} from "./shared/models/user";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'kino-app';

  constructor(private authentication: AuthenticationService) {
  }

  ngOnInit(): void {
    let token = sessionStorage.getItem("token");

    if (token) {
      this.authentication.validateToken(token).subscribe({
          next: (user: User) => {
            let tokenInfo = token
            if (tokenInfo) {
              let payloadPartOfToken = tokenInfo.split(".")[1];
              let userInfo = JSON.parse(window.atob(payloadPartOfToken))
              user.id = userInfo.sub;
              user.role = userInfo.roles[0].authority;
              user.token = tokenInfo;
              this.authentication.setCurrentUser(user);
            } else {
              this.authentication.logout();
            }
          },
          error: () => {
            this.authentication.logout();
          }
        }
      )
    }


  }
}
