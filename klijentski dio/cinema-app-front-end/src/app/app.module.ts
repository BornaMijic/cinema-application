import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NavigationComponent} from './navigation/navigation.component';
import {CalendarComponent} from './calendar/calendar.component';
import {EventSortingPipe} from './shared/pipes/event-sorting.pipe';
import {MenuDirective} from './shared/directives/menu.directive';
import {EventDetailsComponent} from './calendar/event-details/event-details.component';
import {PageNotFoundComponent} from './shared/page-not-found/page-not-found.component';
import {HomePageComponent} from './home-page/home-page.component';
import {ReservationComponent} from './calendar/event-details/reservation/reservation.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AuthenticationComponent} from './authentication/authentication.component';
import {RegisterComponent} from './authentication/register/register.component';
import {MoviesComponent} from './movies/movies.component';
import {MovieDetailsComponent} from './movies/movie-details/movie-details.component';
import {MovieInsertComponent} from './movies/movie-insert/movie-insert.component';
import {EventReservationComponent} from './movies/event-reservation/event-reservation.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {PaymentComponent} from './calendar/event-details/payment/payment.component';
import {MyReservationsComponent} from './my-reservations/my-reservations.component';
import {QRCodeModule} from "angular2-qrcode";
import {JwtInterceptor} from "./shared/interceptors/jwt.interceptor";
import {ErrorOccurredComponent} from './shared/error-occurred/error-occurred.component';
import {NgxPaginationModule} from "ngx-pagination";
import {VerifyAccountComponent} from './authentication/verify-account/verify-account.component';
import {UsersAdministrationComponent} from './authentication/users-administration/users-administration.component';
import {MovieEditComponent} from './movies/movie-edit/movie-edit.component';
import {EventInsertComponent} from './movies/event-insert/event-insert.component';
import {DatePipe} from './shared/pipes/date.pipe';
import {CinemaHallInsertComponent} from './cinema-halls-list/cinema-hall-insert/cinema-hall-insert.component';
import {CinemaHallsListComponent} from './cinema-halls-list/cinema-halls-list.component';
import {TemporaryReservationListComponent} from './temporary-reservation-list/temporary-reservation-list.component';

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    CalendarComponent,
    EventSortingPipe,
    MenuDirective,
    EventDetailsComponent,
    PageNotFoundComponent,
    HomePageComponent,
    ReservationComponent,
    AuthenticationComponent,
    RegisterComponent,
    MoviesComponent,
    MovieDetailsComponent,
    MovieInsertComponent,
    EventReservationComponent,
    PaymentComponent,
    MyReservationsComponent,
    ErrorOccurredComponent,
    VerifyAccountComponent,
    UsersAdministrationComponent,
    MovieEditComponent,
    EventInsertComponent,
    DatePipe,
    CinemaHallInsertComponent,
    CinemaHallsListComponent,
    TemporaryReservationListComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    QRCodeModule,
    NgxPaginationModule,
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: JwtInterceptor,
    multi: true
  }
  ],
  bootstrap: [AppComponent]
})

export class AppModule {
}

