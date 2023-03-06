import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthenticationService} from "../shared/services/authentication.service";
import {mergeMap, of, Subscription} from "rxjs";
import {MovieService} from "../shared/services/movie.service";
import {Reservation} from "../shared/models/reservation.model";
import {Event} from "../calendar/event.model";
import {Movie} from "../shared/models/movie.model";
import {CinemaHall} from "../shared/models/cinema-hall.model";
import {User} from "../shared/models/user";
import * as moment from "moment";

@Component({
  selector: 'app-my-reservations',
  templateUrl: './my-reservations.component.html',
  styleUrls: ['./my-reservations.component.css']
})
export class MyReservationsComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  userId: string = "";
  reservations: Reservation[] = [];
  events: Event[] = [];
  movies: Movie[] = [];
  reservationSortedByEvent: { idReservation: Reservation, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[], event: Event, movie: Movie, cinemaHall: CinemaHall }[] = [];
  cinemaHalls: CinemaHall[] = [];
  error: string = ""
  loadDataError: boolean = false;
  count = 1;

  pageNumber: number = 1;
  amountOfReservations: number = 0;

  readonly DATE_FORMAT: string = 'DD-MM-yyyy';
  readonly HOUR_FORMAT: string = "HH:mm Z z";

  private subscription: Subscription = new Subscription();

  constructor(private authentication: AuthenticationService, private movieService: MovieService) {
  }

  ngOnInit(): void {
    let subscription = this.authentication.currentUser.pipe(
      mergeMap((user) => {
        if (user) {
          if (user.id) {
            this.currentUser = user;
            return this.movieService.getReservationsForSpecificUser(user.id, 0);
          }
        }
        return of([])

      })
    ).subscribe({
      next: (reservations: any) => {
        this.reservationSortedByEvent = reservations.reservationsWithSeatEventMovieAndCinemaHall
        this.reservationSortedByEvent = reservations.reservationsWithSeatEventMovieAndCinemaHall
        if (this.reservationSortedByEvent) {
          this.reservationSortedByEvent.forEach((reservation) => {
            reservation.event.date = moment.utc(reservation.event.date).local()
          })
          this.amountOfReservations = reservations.count;
          this.loadDataError = false;
          this.error = "";
        } else {
          this.loadDataError = true;
        }

      },
      error: () => {
        this.loadDataError = true;
      }
    })

    this.subscription.add(subscription);

  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  onPageChange(event: any) {
    let id = this.currentUser?.id
    if (id) {
      let subscription = this.movieService.getReservationsForSpecificUser(id, event - 1).subscribe((reservations: { count: number, reservationsWithSeatEventMovieAndCinemaHall: { idReservation: Reservation, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[], event: Event, movie: Movie, cinemaHall: CinemaHall }[] }) => {
        this.pageNumber = event
        this.reservationSortedByEvent = reservations.reservationsWithSeatEventMovieAndCinemaHall
        this.reservationSortedByEvent.forEach((reservation) => {
          reservation.event.date = moment.utc(reservation.event.date).local()
        })
        this.amountOfReservations = reservations.count;
      })
      this.subscription.add(subscription)
    }
  }


  downloadTickets(reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[], event: Event, title: string, cinemaHallName: string) {
    let currentReservationSeats: { rowNumber: number | undefined; seatNumber: number | undefined; idReservationSeat: string; }[] = [];
    reservationSeats.forEach(reservationSeat => {
      currentReservationSeats.push({
        rowNumber: reservationSeat.rowNumber,
        seatNumber: reservationSeat.seatNumber,
        idReservationSeat: reservationSeat.id
      })
    })
    let dateString = moment(event.date).format('DD-MM-yyyy').toString();
    let hourString = moment(event.date).format('HH:mm Z z').toString();

    let subscription = this.movieService.downloadPdf({
      cinemaHallName: cinemaHallName,
      title: title,
      dateString: dateString,
      hourString: hourString,
      price: event.price,
      userReservationSeats: currentReservationSeats
    }, this.currentUser?.id!).subscribe({
      next: (pdf: any) => {
        this.error = ""
        this.movieService.getPdf(pdf)
      },
      error: () => {
        window.scroll(0, 0)
        this.error = "Došlo je do pogreške prilikom preuzimanja karte. Pokušajte ponovno kasnije";
      }
    });
    this.subscription.add(subscription)
  }

  getSeats(idCinemaHall: string) {
    return this.cinemaHalls.find((cinemaHall: CinemaHall) => cinemaHall.id == idCinemaHall);
  }

  getReservationForEvent(eventId: string): Reservation[] {
    return this.reservations.filter((reservation: Reservation) => reservation.eventId == eventId);
  }
}
