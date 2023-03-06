import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {map, Observable, take} from 'rxjs';
import {AuthenticationService} from "../services/authentication.service";

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(private authenticationService: AuthenticationService, private router: Router) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean |
    UrlTree> | boolean | UrlTree {
    let user = this.authenticationService.user;

    if (user && user.role == "ROLE_ADMIN") {
      return true;
    }

    let token = sessionStorage.getItem('token')
    if (token != null) {
      return this.authenticationService.validateToken(token!).pipe(
        take(1),
        map((user) => {
          if (user && user.role == "ROLE_ADMIN") {
            return true;
          }
          return this.router.createUrlTree(['']);
        })
      )
    } else {
      return this.router.createUrlTree(['']);
    }
  }


}


