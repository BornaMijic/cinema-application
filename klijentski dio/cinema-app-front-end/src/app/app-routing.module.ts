import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {CalendarComponent} from "./calendar/calendar.component";
import {EventDetailsComponent} from "./calendar/event-details/event-details.component";
import {PageNotFoundComponent} from "./shared/page-not-found/page-not-found.component";
import {HomePageComponent} from "./home-page/home-page.component";
import {AuthenticationComponent} from "./authentication/authentication.component";
import {RegisterComponent} from "./authentication/register/register.component";
import {MoviesComponent} from "./movies/movies.component";
import {MovieDetailsComponent} from "./movies/movie-details/movie-details.component";
import {MovieInsertComponent} from "./movies/movie-insert/movie-insert.component";
import {EventReservationComponent} from "./movies/event-reservation/event-reservation.component";
import {PaymentComponent} from "./calendar/event-details/payment/payment.component";
import {MyReservationsComponent} from "./my-reservations/my-reservations.component";
import {NotAuthGuard} from "./shared/guards/not-auth.guard";
import {AdminGuard} from "./shared/guards/admin.guard";
import {ErrorOccurredComponent} from "./shared/error-occurred/error-occurred.component";
import {VerifyAccountComponent} from "./authentication/verify-account/verify-account.component";
import {UsersAdministrationComponent} from "./authentication/users-administration/users-administration.component";
import {MovieEditComponent} from "./movies/movie-edit/movie-edit.component";
import {EventInsertComponent} from "./movies/event-insert/event-insert.component";
import {UserGuard} from "./shared/guards/user.guard";
import {CinemaHallInsertComponent} from "./cinema-halls-list/cinema-hall-insert/cinema-hall-insert.component";
import {CinemaHallsListComponent} from "./cinema-halls-list/cinema-halls-list.component";
import {TemporaryReservationListComponent} from "./temporary-reservation-list/temporary-reservation-list.component";

const routes: Routes = [
  {path: '', component: HomePageComponent},
  {path: 'calendar', component: CalendarComponent},
  {path: 'calendar/:title/:id', component: EventDetailsComponent},
  {path: 'payment', component: PaymentComponent, canActivate: [UserGuard]},
  {path: 'movies', component: MoviesComponent},
  {path: 'movies/:title/:id', component: MovieDetailsComponent},
  {path: 'movies/reservations/:title/:id', component: EventDetailsComponent},
  { path: 'myreservations', component: MyReservationsComponent,  canActivate: [UserGuard]},
  {path: 'reservations/uncomplete', component: TemporaryReservationListComponent, canActivate: [UserGuard]},
  {path: 'login', component: AuthenticationComponent,canActivate: [NotAuthGuard]},
  {path: 'register', component: RegisterComponent,canActivate: [NotAuthGuard]},
  { path: 'administration/movies', component: MoviesComponent, canActivate: [AdminGuard]},
  {path: 'administration/movies/details/:title/:id', component: MovieDetailsComponent, canActivate: [AdminGuard]},
  { path: 'administration/movies/new', component: MovieInsertComponent, canActivate: [AdminGuard]},
  { path: 'administration/movies/edit/:id', component: MovieEditComponent, canActivate: [AdminGuard]},
  { path: 'administration/movies/event/new/:idMovie', component: EventInsertComponent, canActivate: [AdminGuard]},
  { path: 'administration/movies/event/edit/:id', component: EventInsertComponent, canActivate: [AdminGuard]},
  { path: 'administration/movies/reservations/:title/:idMovie/:idEvent', component: EventReservationComponent, canActivate: [AdminGuard]},
  { path: 'administration/users', component: UsersAdministrationComponent, canActivate: [AdminGuard]},
  { path: 'administration/cinemaHalls', component: CinemaHallsListComponent, canActivate: [AdminGuard]},
  { path: 'administration/cinemaHalls/insert', component: CinemaHallInsertComponent, canActivate: [AdminGuard]},
  { path: 'verify-code/:username/:code', component: VerifyAccountComponent},
  { path: 'error-occurred', component: ErrorOccurredComponent},
  { path: '**', pathMatch: 'full', component: PageNotFoundComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
