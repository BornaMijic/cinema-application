import {Component, OnDestroy, OnInit} from '@angular/core';
import {of, Subscription, switchMap} from "rxjs";
import {MovieService} from "../shared/services/movie.service";
import {User} from "../shared/models/user";
import * as moment from "moment";
import {AuthenticationService} from "../shared/services/authentication.service";
import {Movie} from "../shared/models/movie.model";
import {Event} from "../calendar/event.model";
import {Router} from "@angular/router";
import {CinemaHall} from "../shared/models/cinema-hall.model";

@Component({
  selector: 'app-temporary-reservation-list',
  templateUrl: './temporary-reservation-list.component.html',
  styleUrls: ['./temporary-reservation-list.component.css']
})
export class TemporaryReservationListComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  temporaryReservationsInfo: { idReservation: string, reservationTime: moment.Moment, movie: Movie, event: Event, cinemaHall: CinemaHall, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }[] | null = null;
  errorTemporaryReservation: string = '';
  readonly DATE_HOUR_FORMAT: string = 'DD-MM-yyyy HH:mm Z z';

  private subscription: Subscription = new Subscription();

  constructor(private router: Router, private authenticationService: AuthenticationService, private movieService: MovieService) {
  }

  ngOnInit(): void {
    this.movieService.setCurrentReservationIdAndUserId({idReservation: "", idUser: ""});
    this.movieService.clearTicketBuying();

    let subscription = this.authenticationService.currentUser.pipe(
      switchMap((user: User | null) => {
        this.currentUser = user;
        if (user != null && user.id && user.id != "") {
          return this.movieService.getAllReservationByIdUserWhichAreUncomplete(user.id);
        }
        return of(false);
      })).subscribe(
      {
        next: (temporaryReservationsInfo: { idReservation: string, reservationTime: moment.Moment, movie: Movie, event: Event, cinemaHall: CinemaHall, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }[] | boolean | any) => {
          if (typeof temporaryReservationsInfo == 'boolean') {
            this.temporaryReservationsInfo = null;
          } else {
            temporaryReservationsInfo.forEach((temporaryReservationsInfo: any) => temporaryReservationsInfo.event.date = moment.utc(temporaryReservationsInfo.event.date).local()
            )
            this.temporaryReservationsInfo = temporaryReservationsInfo;
          }
        },
        error: () => {
          this.temporaryReservationsInfo = null;
        }

      });

    this.subscription.add(subscription)
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  deleteTemporaryReservation(idReservation: string) {
    let currentUser = this.currentUser;
    let temporaryReservationsInfo = this.temporaryReservationsInfo;
    if (idReservation != "" && currentUser != null && currentUser.id && currentUser.id != "") {
      let subscription = this.movieService.deleteReservationByIdAndUserId(idReservation, currentUser.id).subscribe({
        next: (value: any) => {
          this.errorTemporaryReservation = "";
          this.movieService.setCurrentReservationIdAndUserId({idReservation: "", idUser: ""});
          this.movieService.clearTicketBuying();
          this.errorTemporaryReservation = "";
          let index = temporaryReservationsInfo?.findIndex((temporaryReservationInfo) => temporaryReservationInfo.idReservation == idReservation);
          if (index != undefined && temporaryReservationsInfo != null) {
            if (temporaryReservationsInfo?.length > 0 && index != -1) {
              temporaryReservationsInfo.splice(index, 1);
            } else {
              this.errorTemporaryReservation = "Došlo je do pogreške. Pokušajte osvježiti stranicu ili pokušajte kasnije";
              location.reload();
            }
          } else {
            this.errorTemporaryReservation = "Došlo je do pogreške. Pokušajte osvježiti stranicu ili pokušajte kasnije";
            location.reload();
          }

        },
        error: () => {
          this.errorTemporaryReservation = "Došlo je do pogreške. Pokušajte osvježiti stranicu ili pokušajte kasnije";
          location.reload();
        }
      })
      this.subscription.add(subscription)
    } else {
      this.errorTemporaryReservation = "Došlo je do pogreške. Pokušajte osvježiti stranicu ili pokušajte kasnije";
      location.reload();
    }
  }

  goToPayment(temporaryReservationInfo: { idReservation: string, reservationTime: moment.Moment, movie: Movie, event: Event, cinemaHall: CinemaHall, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }) {
    let currentUser = this.currentUser;
    let reservationTime = temporaryReservationInfo.reservationTime;
    if (temporaryReservationInfo != null && temporaryReservationInfo.idReservation != "" && currentUser != null && currentUser.id && currentUser.id != "" && reservationTime) {
      this.movieService.setCurrentReservationIdAndUserId({
        idReservation: temporaryReservationInfo.idReservation,
        idUser: currentUser.id
      });
      let reservationTime = moment.utc(temporaryReservationInfo.reservationTime);
      this.movieService.setReservationTime(reservationTime);
      let currentReservations = temporaryReservationInfo.reservationSeats.map((reservationSeat) => {
        return {
          id: reservationSeat.id,
          seatId: reservationSeat.seatId,
          seatNumber: reservationSeat.seatNumber,
          rowNumber: reservationSeat.rowNumber
        }
      });
      this.movieService.setTicketBuying(temporaryReservationInfo.cinemaHall.name, temporaryReservationInfo.movie, temporaryReservationInfo.event!, temporaryReservationInfo.event!.price, currentReservations.length, currentReservations)
      this.errorTemporaryReservation = "";
      this.router.navigate(['/payment'])
    } else {
      this.errorTemporaryReservation = "Došlo je do pogreške. Pokušajte osvježiti stranicu ili pokušajte kasnije";
    }
  }

}
