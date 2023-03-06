import {Component, OnDestroy, OnInit} from '@angular/core';
import {MovieService} from "../../shared/services/movie.service";
import {Event} from "../../calendar/event.model";
import {ActivatedRoute, Router} from "@angular/router";
import {Movie} from "../../shared/models/movie.model";
import {Reservation} from "../../shared/models/reservation.model";
import {Subscription, switchMap} from "rxjs";
import {CinemaHall} from "../../shared/models/cinema-hall.model";
import {Seat} from "../../shared/models/seat.model";
import {AuthenticationService} from "../../shared/services/authentication.service";
import * as moment from "moment";


@Component({
  selector: 'app-event-reservation',
  templateUrl: './event-reservation.component.html',
  styleUrls: ['./event-reservation.component.css']
})
export class EventReservationComponent implements OnInit, OnDestroy {

  ev: Event = new Event('', moment(), '', '', 0);
  movie: Movie = new Movie('', '', '', "", 0, '');
  evId: string = '';
  movieId: string = '';
  reservations: { id: string, userId: string, idReservationSeat: string, eventId: string, seatId: string, seatNumber: number, rowNumber: number, name: string, surname: string }[] = [];
  cinemaHall: CinemaHall | null = null;
  openAddReservation: boolean = false;
  selectedSeat: string = "";
  updateSelectedSeat: string = "";
  users: { id: string, name: string, surname: string }[] = [];
  edit: { id: string, mode: boolean } = {id: "", mode: false}
  errorMessage: string = '';
  private subscription: Subscription = new Subscription();
  readonly DATE_FORMAT: string = 'DD-MM-yyyy';
  readonly HOUR_FORMAT: string = "HH:mm Z z";

  constructor(private movieService: MovieService, private authentication: AuthenticationService, private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.evId = this.route.snapshot.params['idEvent'];
    if (this.evId) {
      let subscription = this.movieService.loadManageReservations.pipe(
        switchMap(() =>
          this.movieService.getEventWithMovieCinemaHallAndReservationById(this.evId)
        )
      ).subscribe(
        {
          next: (eventWithMovieCinemaHallAndReservations: { event: Event, movie: Movie, cinemaHall: CinemaHall, reservationWithSeatInfoList: { id: string, userId: string, idReservationSeat: string, eventId: string, seatId: string, seatNumber: number, rowNumber: number, name: string, surname: string }[] }) => {
            this.movie = eventWithMovieCinemaHallAndReservations.movie;
            this.ev = eventWithMovieCinemaHallAndReservations.event
            this.ev.date = moment.utc(this.ev.date).local();
            this.reservations = eventWithMovieCinemaHallAndReservations.reservationWithSeatInfoList;
            this.cinemaHall = eventWithMovieCinemaHallAndReservations.cinemaHall;
            this.sortReservations()
          },
          error: () => {
            this.errorMessage = "Nažalost, nismo mogli očitati rezervacije"
          }
        })
      this.subscription.add(subscription);
    } else {
      this.router.navigate(['error-occurred']);
    }

  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe()
  }

  deleteReservation(id: string, idReservationSeat: string) {
    if (confirm("Da li ste sigurni da želite izbrisati rezervaciju")) {
      let subscription = this.movieService.deleteReservation(id, idReservationSeat).subscribe(
        {
          next: () => {
            this.reservations = this.reservations.filter((reservation) => reservation.idReservationSeat != idReservationSeat);
            this.errorMessage = "";
          },
          error: (error) => {
            if(error.status = '409') {
              this.movieService.loadManageReservations.next(true)
              this.errorMessage = "Privremena rezervacija se ne može brisati"
            } else {
              this.movieService.loadManageReservations.next(true)
              this.errorMessage = "Brisanje rezervacije nije bilo uspješno"
            }

          }
        }
      )
      this.subscription.add(subscription);
    }
  }

  addReservation() {
    if (confirm("Da li ste sigurni da želite dodati rezervaciju")) {
      if (this.selectedSeat != null) {
        let seat: Seat | undefined = this.cinemaHall?.seats.find((seat: Seat) => seat.id == this.selectedSeat);
        if (seat != undefined) {
          let reservation: { userId: null, eventId: string, seatId: string } = {
            userId: null,
            eventId: this.evId,
            seatId: seat.id
          }
          let subscription = this.movieService.addReservation(reservation).subscribe(
            {
              next: (reservations: { id: string, userId: string, idReservationSeat: string, eventId: string, seatId: string, seatNumber: number, rowNumber: number, name: string, surname: string }[]) => {
                reservations.map(reservation => this.reservations.push(reservation))
                this.selectedSeat = "";
                this.sortReservations()
              },
              error: () => {
                this.movieService.loadManageReservations.next(true)
                this.errorMessage = "Dodavanje rezervacije nije bilo uspješno"
                this.selectedSeat = "";
              }

            }
          )

          this.subscription.add(subscription);
        }
      }
    }
  }

  updateReservation(id: string, idReservationSeat: string) {
    if (confirm("Da li ste sigurni da želite ažurirat rezervaciju")) {
      let seatId = this.updateSelectedSeat;
      let seat: Seat | undefined = this.cinemaHall?.seats.find((seat: Seat) => seat.id == seatId);
      if (seat) {
        let reservationSeat = {id: idReservationSeat, seatId: seatId};
        let subscription = this.movieService.updateReservation(reservationSeat).subscribe(
          {
            next: () => {
              ;
              this.errorMessage = "";
              this.edit.id = "";
              this.edit.mode = false;
              this.updateSelectedSeat = "";
              this.movieService.loadManageReservations.next(true)
            },
            error: () => {
              this.movieService.loadManageReservations.next(true)
              this.errorMessage = "Ažuriranje rezervacije nije bilo moguće. Neko je prije zauzeo mjesto ili je rijeć o privremenoj rezervaciji"
            }
          }
        );
        this.subscription.add(subscription);
      } else {
        this.movieService.loadManageReservations.next(true)
        this.errorMessage = "Ažuriranje rezervacije nije bilo moguće. Došlo je pogreške, molimo vas da pokušate ponovno kasnije"
      }
    }
  }


  getPossibleSeats(): Seat[] {
    let idSeatsArray = this.reservations.map((reservation: Reservation) => reservation.seatId);
    if (typeof idSeatsArray != 'undefined') {
      let reservationSet = new Set([...idSeatsArray]);
      let idSeats = Array.from(reservationSet.values())
      let seats = this.cinemaHall?.seats.filter((seat) => !idSeats.includes(seat.id) && seat.seatNumber != undefined);
      if (seats != undefined) {
        return seats;
      }
    }
    if (this.cinemaHall?.seats == undefined) {
      return []
    }
    return this.cinemaHall?.seats;
  }

  private sortReservations() {
    this.reservations.sort((resA: Reservation, resB: Reservation) => {
      if (resA.rowNumber! == resB.rowNumber!) {
        if (resA.seatNumber! > resB.seatNumber!) {
          return 1;
        }
        return -1;
      } else if (resA.rowNumber! > resB.rowNumber!) {
        return 1;
      }
      return -1;
    })
  }

  exportAs(reservation: { id: string; userId: string; idReservationSeat: string; eventId: string; seatId: string; seatNumber: number; rowNumber: number; name: string; surname: string }) {
    let reservationExport = [];

    let dateString = moment(this.ev.date).format('DD-MM-yyyy').toString();
    let hourString = moment(this.ev.date).format('HH:mm Z z').toString();


    reservationExport.push({
      rowNumber: reservation.rowNumber,
      seatNumber: reservation.seatNumber,
      idReservationSeat: reservation.idReservationSeat
    })
    let subscription = this.movieService.downloadPdfAdmin({
      cinemaHallName: this.cinemaHall?.name, title: this.movie.title, dateString: dateString,
      hourString: hourString, price: this.ev.price, userReservationSeats: reservationExport
    }).subscribe({
      next: (pdf: any) => {
        this.errorMessage = ""
        this.movieService.getPdf(pdf)
      },
      error: () => {
        this.errorMessage = "Preuzimanje karte nije bilo moguće";
      }
    });

    this.subscription.add(subscription)
  }
}
