import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {map, Observable, take} from 'rxjs';
import {AuthenticationService} from "../services/authentication.service";

@Injectable({
  providedIn: 'root'
})
export class NotAuthGuard implements CanActivate {

  constructor(private authenticationService: AuthenticationService, private router: Router) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    let user = this.authenticationService.user;

    if (user && user.role == "ROLE_ADMIN") {
      return true;
    }

    let token = sessionStorage.getItem('token')
    if (token != null) {
      return this.authenticationService.validateToken(token!).pipe(
        take(1),
        map((user) => {
          if (user && user.role) {
            return this.router.createUrlTree(['']);
          }
          return true;
        })
      )
    } else {
      return true;
    }
  }

}
