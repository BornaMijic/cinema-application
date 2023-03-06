import {Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import {AuthenticationService} from "../shared/services/authentication.service";
import {User} from "../shared/models/user";
import {catchError, filter, of, Subscription, switchMap} from "rxjs";
import {NavigationStart, Router} from "@angular/router";
import {MovieService} from "../shared/services/movie.service";

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  innerWidth: any;
  show = false;
  username = '';
  private subscription: Subscription = new Subscription();
  activeMenu: number = 1;
  hideNavigation = false;


  getNumberOfTemporaryReservations(): number {
    return this.movieService.numberOfTemporaryReservations;
  }

  constructor(private authentication: AuthenticationService, private router: Router, private movieService: MovieService) {
    this.router.events.pipe(filter(event => event instanceof NavigationStart),
      switchMap((event: any) => {
        let currentUser = this.currentUser;
        if (currentUser != null && currentUser.id != "" && currentUser.id != undefined) {
          return this.movieService.getCountOfUncompleteReservations(currentUser.id).pipe(catchError(() => {
            return of(true)
          }))
        } else {
          return of(false);
        }
      })).subscribe((value: { countUncompleteReservations: number } | boolean) => {
      if (typeof value != 'boolean') {
        this.movieService.numberOfTemporaryReservations = value.countUncompleteReservations;
      }
    })
  }

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.innerWidth = window.innerWidth;
    if (this.innerWidth >= 768) {
      this.show = false;
    }
  }

  ngOnInit(): void {
    let subscription = this.authentication.currentUser.pipe(switchMap((user: User | null) => {
      if (user && user.id) {
        this.currentUser = user;
        if (user.username) {
          this.username = user.username;
          return this.movieService.getCountOfUncompleteReservations(user.id).pipe(catchError(() => {
            return of(false)
          }))
        } else {
          this.currentUser = null;
          this.username = '';
        }
      } else {
        this.currentUser = null;
        this.username = '';
      }
      return of(false);
    })).subscribe((value: { countUncompleteReservations: number } | boolean) => {
      if (typeof value != 'boolean') {
        this.movieService.numberOfTemporaryReservations = value.countUncompleteReservations;
      }
    })
    this.subscription.add(subscription);
    this.innerWidth = window.innerWidth;
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe()
  }

  logout() {
    this.authentication.logout();
    this.router.navigate(['login'])
  }

  showMenu(): void {
    this.show = !this.show
  }
}
