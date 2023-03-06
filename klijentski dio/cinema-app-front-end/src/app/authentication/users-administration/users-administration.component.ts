import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../shared/services/authentication.service";
import {User} from "../../shared/models/user";
import {Subscription, switchMap} from "rxjs";

@Component({
  selector: 'app-users-administration',
  templateUrl: './users-administration.component.html',
  styleUrls: ['./users-administration.component.css']
})
export class UsersAdministrationComponent implements OnInit {
  users: { id: string, username: string, verified: boolean, active: boolean, role: string }[] = [];
  usersAmount: number = 0;
  pageNumber: number = 1;
  currentUser: User | null = null;
  errorMessage: string = '';
  searchUsername: string = ""

  private subscription: Subscription = new Subscription();

  constructor(private router: Router, private authenticationService: AuthenticationService) {
  }

  ngOnInit(): void {
    let subscription = this.authenticationService.findTenUsersPerPageAdmin(0).subscribe(
      (value: any) => {
        this.usersAmount = value.usersAmount;
        this.users = value.usersList;
      })

    this.subscription.add(subscription)

  }


  ngOnDestroy(): void {
    this.subscription.unsubscribe()
  }

  onPageChange(event: any) {
    if (this.searchUsername.length > 0) {
      let subscription = this.authenticationService.findTenUsersPerPageSearchUsernameAdmin(event - 1, this.searchUsername).subscribe(
        (value: any) => {
          this.pageNumber = event
          this.usersAmount = value.usersAmount
          this.users = value.usersList;
        })
      this.subscription.add(subscription);
    } else {
      let subscription: Subscription = this.authenticationService.findTenUsersPerPageAdmin(event - 1).subscribe(
        (value: any) => {
          this.pageNumber = event
          this.usersAmount = value.usersAmount
          this.users = value.usersList;
        })
      this.subscription.add(subscription);
    }
  }

  filterUsers(searchUsername: string) {
    this.searchUsername = searchUsername;
    this.pageNumber = 1;
    if (searchUsername.length > 0) {
      let subscription = this.authenticationService.findTenUsersPerPageSearchUsernameAdmin(0, searchUsername).subscribe(
        (value: any) => {
          this.usersAmount = value.usersAmount
          this.users = value.usersList;
        })
      this.subscription.add(subscription);

      this.pageNumber = 1;
    } else {
      let subscription = this.authenticationService.findTenUsersPerPageAdmin(0).subscribe(
        (value: any) => {
          this.usersAmount = value.usersAmount
          this.users = value.usersList;
          this.searchUsername = "";
        })
      this.subscription.add(subscription);
      this.pageNumber = 1;
    }
  }

  i = 0;

  changeVerified(user: { id: string; username: string; verified: boolean; active: boolean }) {
    if (confirm("Da li si siguran da želiš promjeniti verifikaciju korisnika")) {
      let subscription = this.authenticationService.changeVerified(user.id, user.username, !user.verified).pipe(
        switchMap(() => {
          if (this.searchUsername.length > 0) {
            return this.authenticationService.findTenUsersPerPageSearchUsernameAdmin(this.pageNumber - 1, this.searchUsername)
          }
          return this.authenticationService.findTenUsersPerPageAdmin(this.pageNumber - 1)

        })
      ).subscribe({
          next: (value: any) => {
            this.usersAmount = value.usersAmount
            this.users = value.usersList;
            this.errorMessage = ""
          },
          error: () => {
            this.errorMessage = "Nažalost, promjena aktivnost korisnika nije bila moguća. Predlažemo da osvježite stranicu."

          }
        }
      );
      this.subscription.add(subscription);
    }

  }

  changeActive(user: { id: string; username: string; verified: boolean; active: boolean }) {
    if (confirm("Da li si siguran da želiš promjeniti aktivnost korisnika")) {
      let subscription = this.authenticationService.changeActive(user.id, user.username, !user.active).pipe(
        switchMap(() => {
          if (this.searchUsername.length > 0) {
            return this.authenticationService.findTenUsersPerPageSearchUsernameAdmin(this.pageNumber - 1, this.searchUsername)
          }
          return this.authenticationService.findTenUsersPerPageAdmin(this.pageNumber - 1)

        })
      ).subscribe({
          next: (value: any) => {
            this.usersAmount = value.usersAmount
            this.users = value.usersList;
            this.errorMessage = ""
          },
          error: () => {
            this.errorMessage = "Nažalost, promjena aktivnost korisnika nije bila moguća. Predlažemo da osvježite stranicu."

          }
        }
      );
      this.subscription.add(subscription);
    }

  }
}
