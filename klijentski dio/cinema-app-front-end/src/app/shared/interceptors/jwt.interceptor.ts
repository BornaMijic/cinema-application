import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {AuthenticationService} from "../services/authentication.service";
import {Router} from "@angular/router";
import {environment} from "../../../environments/environment";


@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(private authentication: AuthenticationService, private router: Router) {
  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (this.authentication.jwtToken != '') {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${this.authentication.jwtToken}`
        }
      });
    } else {
      let token = sessionStorage.getItem('token')
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    if (request.url == `${environment.backend}api/validate`) {
      return next.handle(request).pipe(
        catchError((error: any) => {
          if (error.status == 401 || error.status == 403) {
            this.authentication.logout();
            this.router.navigate(['login'])
          }
          throw error;
        }));
      ;
    }

    return next.handle(request).pipe(
      catchError((error: any) => {
        if (error.status == 401 || error.status == 403) {
          this.authentication.logout();
          this.router.navigate(['login'])
        }
        throw error;
      }));
  }
}
