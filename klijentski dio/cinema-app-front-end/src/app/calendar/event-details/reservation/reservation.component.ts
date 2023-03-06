import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Event} from "../../event.model";
import {Movie} from "../../../shared/models/movie.model";
import {MovieService} from "../../../shared/services/movie.service";
import {Reservation} from "../../../shared/models/reservation.model";
import {of, Subscription, switchMap} from "rxjs";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../../shared/services/authentication.service";
import {User} from "../../../shared/models/user";
import {CinemaHall} from "../../../shared/models/cinema-hall.model";
import {Seat} from "../../../shared/models/seat.model";
import * as moment from "moment";

@Component({
  selector: 'app-reservation',
  templateUrl: './reservation.component.html',
  styleUrls: ['./reservation.component.css']
})
export class ReservationComponent implements OnInit, OnDestroy {
  @Input() event: Event | null = null;
  @Input() movie: Movie | null = null;
  @Input() cinemaHall: CinemaHall | null = null;
  error: string = "";
  errorMessage: string = '';
  errorTemporaryReservation: string = '';
  loading: boolean = false;
  currentUser: User | null = null;
  seats: { id: string | null, seatNumber: number | null, rowNumber: number | null, gridPosition: number, reserved: boolean }[] = [];
  temporarySeats: { id: string | null, seatNumber: number | null, rowNumber: number | null, gridPosition: number, reserved: boolean }[] = [];
  edgeGridPosition: { row: number | null, gridPositionMin: number | null, gridPositionMax: number | null } = {
    row: null,
    gridPositionMin: null,
    gridPositionMax: null
  };
  temporaryReservation: { idReservation: string, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] } | null = null;

  tickets: number = 0;
  reservations: Reservation[] = [];
  currentReservationSeats: { userId: string | null, eventId: string, seatId: string, seatNumber: number, rowNumber: number }[] = []
  numberOfSeats = 0;
  numberOfReservedSeats = 0;
  reservationTime!: moment.Moment;

  readonly DATE_FORMAT: string = 'DD-MM-yyyy';
  readonly HOUR_FORMAT: string = "HH:mm Z z";
  readonly NUMBER_FORMAT: string = '1.2-2';

  private subscription: Subscription = new Subscription()

  constructor(private authentication: AuthenticationService, private movieService: MovieService, private router: Router) {

  }

  ngOnInit(): void {

    let subscription = this.authentication.currentUser.pipe(
      switchMap((user: User | null) => {
        this.currentUser = user;
        if (this.currentUser != null && this.currentUser.id && this.currentUser.id != "") {
          return this.movieService.getReservationByIdEventAndIdUserAndUncomplete(this.currentUser.id, this.event!.id)
        }
        return of(false);
      })).subscribe(
      {
        next: (reservation: { idReservation: string, reservationTime: moment.Moment, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] } | boolean) => {
          if (typeof reservation == 'boolean') {
            this.temporaryReservation = null;
          } else {
            if (reservation == null) {
              this.temporaryReservation = null;
            } else {
              this.reservationTime = moment.utc(reservation.reservationTime);
              this.temporaryReservation = reservation;
            }
          }
        },
        error: () => {
          this.temporaryReservation = null;
        }

      }
    )

    this.subscription.add(subscription);

    if (this.event && this.event.id && this.movie && this.cinemaHall && this.cinemaHall.seats) {
      let cinemaHall = this.cinemaHall
      let seats = cinemaHall.seats.filter((seat) => seat.seatNumber != null).sort((a: Seat, b: Seat) => a.gridPosition - b.gridPosition);
      let numberOfSeats = seats.length;
      let z = 0;

      for (let i = 1; i <= cinemaHall.gridRowsNumber; ++i) {
        for (let j = 1; j <= cinemaHall.gridColumnsNumber; ++j) {
          if (z < numberOfSeats && seats[z].gridPosition == (((i - 1) * cinemaHall.gridColumnsNumber) + j)) {
            let seat = seats[z];
            this.temporarySeats.push({
              id: seat.id,
              seatNumber: seat.seatNumber,
              rowNumber: seat.rowNumber,
              gridPosition: seat.gridPosition,
              reserved: false
            })
            ++z;
          } else {
            this.temporarySeats.push({
              id: null,
              seatNumber: null,
              rowNumber: null,
              gridPosition: ((i - 1) * cinemaHall.gridColumnsNumber) + j,
              reserved: false
            })
          }
        }
      }

      let subscription = this.movieService.loadReservation.pipe(
        switchMap(() => this.movieService.getReservationsForSpecificEvent(this.event!.id))
      ).subscribe({
        next: (reservations: Reservation[]) => {
          this.seats = []
          this.numberOfSeats = 0;
          this.numberOfReservedSeats = 0;
          this.reservations = reservations;

          this.temporarySeats.forEach(
            (seat: { id: string | null, seatNumber: number | null, rowNumber: number | null, gridPosition: number, reserved: boolean }) => {
              if (seat.seatNumber != null) {
                ++this.numberOfSeats;
              }
              if (this.reservations.find(seatReserved => seatReserved.seatNumber == seat!.seatNumber && seat.rowNumber == seatReserved!.rowNumber)) {
                ++this.numberOfReservedSeats;
                seat.reserved = true;
              }
            }
          )
          this.seats = this.temporarySeats;
          this.seats.sort((a: { id: string | null, seatNumber: number | null, rowNumber: number | null, gridPosition: number, reserved: boolean }, b: { id: string | null, seatNumber: number | null, rowNumber: number | null, gridPosition: number, reserved: boolean }) => a.gridPosition - b.gridPosition)
        },
        error: () => {
          this.reservations = []
          this.router.navigate(["error-occurred"]).then();
        }
      });
      this.subscription.add(subscription)

    }
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe()
  }

  removeFromReservation(seat: { id: string | null, seatNumber: number | null,
    rowNumber: number | null, gridPosition: number, reserved: boolean }) {
    this.error = ""
    if (this.currentReservationSeats.length == 1) {
      this.currentReservationSeats = [];
      this.edgeGridPosition = {row: null, gridPositionMin: null, gridPositionMax: null};
    } else if (seat.gridPosition == this.edgeGridPosition.gridPositionMin) {
      this.edgeGridPosition.gridPositionMin = seat.gridPosition + 1;
      let index = this.currentReservationSeats.findIndex(reservationSeat =>
        reservationSeat.seatNumber == seat.seatNumber);
      this.currentReservationSeats.splice(index, 1)
    } else if (seat.gridPosition == this.edgeGridPosition.gridPositionMax) {
      this.edgeGridPosition.gridPositionMax = seat.gridPosition - 1;
      let index = this.currentReservationSeats.findIndex(reservationSeat =>
        reservationSeat.seatNumber == seat.seatNumber);
      this.currentReservationSeats.splice(index, 1)
    } else {
      this.error = "Moguće je  odbacivanje odabranog sjedala ako se nalazi na rubu odabranih sjedala";
    }

  }

  selectForReservation(seat: { id: string | null, seatNumber: number | null, rowNumber: number | null,
    gridPosition: number, reserved: boolean }) {
    this.error = ""

    if (seat.rowNumber != null && seat.seatNumber != null && seat.id != null) {
      let reservationSeat: { userId: string | null, eventId: string, seatId: string, seatNumber: number, rowNumber: number } = {
        userId: null,
        eventId: this.event!.id,
        seatId: seat.id,
        seatNumber: seat.seatNumber,
        rowNumber: seat.rowNumber
      }

      if (seat.reserved && typeof this.seats[seat.gridPosition - 1] == undefined
        && this.seats[seat.gridPosition - 1].seatNumber == null) {
        this.error = "Nažalost, ne možemo odabrati navedeno sjedalo";
        return
      }

      if (seat.rowNumber != this.edgeGridPosition.row && this.edgeGridPosition.row != null) {
        this.error = "Odabrano sjedalo mora biti u istom redu pored drugih odabranih sjedala";
        return
      }

      if (this.currentReservationSeats.length == 0 && seat.id && seat.seatNumber) {
        this.errorMessage = ""
        this.error = "";
        this.currentReservationSeats.push(reservationSeat)
        this.edgeGridPosition = {
          row: seat.rowNumber,
          gridPositionMin: seat.gridPosition,
          gridPositionMax: seat.gridPosition
        }
      } else {
        if (this.edgeGridPosition.gridPositionMax! + 1 == seat.gridPosition && seat.id && seat.seatNumber) {
          this.error = "";
          this.currentReservationSeats.push(reservationSeat)
          this.edgeGridPosition.gridPositionMax = seat.gridPosition;
        } else if (this.edgeGridPosition.gridPositionMin! - 1 == seat.gridPosition && seat.id && seat.seatNumber) {
          this.error = "";
          this.currentReservationSeats.push(reservationSeat)
          this.edgeGridPosition.gridPositionMin = seat.gridPosition
        } else {
          this.error = "Odabrano sjedalo mora biti u istom redu i pored drugih odabranih sjedala";
        }
      }

    } else {
      this.error = "Nažalost, došlo je do pogreške";
      return;
    }
  }

  checkForSelected(seat: { id: string | null, seatNumber: number | null, rowNumber: number | null, gridPosition: number, reserved: boolean }) {
    if (seat.seatNumber == null) {
      return false
    }
    if (this.currentReservationSeats.find(seatItem => seatItem.seatNumber == seat.seatNumber && seatItem.rowNumber == seat.rowNumber)) {
      return true;
    } else {
      return false;
    }
  }

  onTicketNumberChange() {
    this.currentReservationSeats = []
  }

  reserveSeats() {
    let currentReservationSeats = this.currentReservationSeats;

    this.currentReservationSeats = []
    if (this.currentUser && this.currentUser.id) {

      let reservationSeats = currentReservationSeats.map((reservationSeat) => {
        return {eventId: reservationSeat.eventId, seatId: reservationSeat.seatId}
      })
      let reservations: { userId: string | null, eventId: string, reservationSeats: { eventId: string, seatId: string }[] } = {
        userId: this.currentUser!.id,
        eventId: this.event!.id,
        reservationSeats: reservationSeats
      };

      this.loading = true;
      let subscription = this.movieService.addReservations(this.event!.date, reservations).subscribe({
        next: (reservation: { id: string, userId: string, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }) => {
          this.errorMessage = "";
          this.navigateToPayment(reservation);
          this.loading = false;
        },
        error: (error: any) => {
          if (error.status == "404") {
            alert("Reservation time is over");
            this.router.navigate(['/'])
          } else {
            this.errorMessage = "Rezervacija nije bila uspješna";
            location.reload()
            this.loading = false;
            this.movieService.setLoadReservation(true);
          }
        }
      })
      this.subscription.add(subscription)
    } else {
      this.errorMessage = "Korisnik mora biti ulogiran kako bi rezervirao karte"
    }
    this.error = "";
  }

  private navigateToPayment(currentReservation: { id: string, userId: string, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }) {
    let cinemaHallName = this.cinemaHall ? this.cinemaHall.name : ""
    this.movieService.setCurrentReservationIdAndUserId({
      idReservation: currentReservation.id,
      idUser: currentReservation.userId
    });
    let currentReservations = currentReservation.reservationSeats.map((reservationSeat) => {
      return {
        id: reservationSeat.id,
        seatId: reservationSeat.seatId,
        seatNumber: reservationSeat.seatNumber,
        rowNumber: reservationSeat.rowNumber
      }
    });
    this.movieService.setTicketBuying(cinemaHallName, this.movie!, this.event!, this.event!.price, currentReservations.length, currentReservations)
    this.currentReservationSeats = [];
    this.movieService.clearReservationTime();
    this.router.navigate(['/payment'])
  }

  clearCurrentReservation() {
    this.currentReservationSeats = [];
    this.edgeGridPosition = {row: null, gridPositionMin: null, gridPositionMax: null};
    this.error = ""
  }

  getSortCurrentReservation() {
    let currentReservationSeats = this.currentReservationSeats;
    return currentReservationSeats.sort((a, b) => a.seatNumber - b.seatNumber)
  }

  deleteTemporaryReservation() {
    let temporaryReservation = this.temporaryReservation;
    let currentUser = this.currentUser;
    if (temporaryReservation != null && temporaryReservation.idReservation != "" && currentUser != null && currentUser.id && currentUser.id != "") {
      let subscription = this.movieService.deleteReservationByIdAndUserId(temporaryReservation.idReservation, currentUser.id).subscribe({
        next: (value: any) => {
          this.movieService.setLoadReservation(true);
          this.temporaryReservation = null;
          this.errorTemporaryReservation = "";
          this.movieService.setCurrentReservationIdAndUserId({idReservation: "", idUser: ""});
          this.movieService.clearTicketBuying();
          this.errorTemporaryReservation = "";
          location.reload()
        },
        error: () => {
          this.errorTemporaryReservation = "Došlo je do pogreške. Pokušajte osvježiti stranicu ili pokušajte kasnije";
        }
      })
      this.subscription.add(subscription)
    } else {
      this.errorTemporaryReservation = "Došlo je do pogreške. Pokušajte osvježiti stranicu ili pokušajte kasnije";
    }
  }

  goToPayment() {
    let temporaryReservation = this.temporaryReservation;
    let currentUser = this.currentUser;
    let reservationTime = this.reservationTime;
    if (temporaryReservation != null && temporaryReservation.idReservation != "" && currentUser != null && currentUser.id && currentUser.id != "" && reservationTime) {
      let cinemaHallName = this.cinemaHall ? this.cinemaHall.name : ""
      this.movieService.setCurrentReservationIdAndUserId({
        idReservation: temporaryReservation.idReservation,
        idUser: currentUser.id
      });
      this.movieService.setReservationTime(reservationTime);
      let currentReservations = temporaryReservation.reservationSeats.map((reservationSeat) => {
        return {
          id: reservationSeat.id,
          seatId: reservationSeat.seatId,
          seatNumber: reservationSeat.seatNumber,
          rowNumber: reservationSeat.rowNumber
        }
      });
      this.movieService.setTicketBuying(cinemaHallName, this.movie!, this.event!, this.event!.price, currentReservations.length, currentReservations)
      this.currentReservationSeats = [];
      this.temporaryReservation = null;
      this.errorTemporaryReservation = "";
      this.router.navigate(['/payment'])
    } else {
      this.errorTemporaryReservation = "Došlo je do pogreške. Pokušajte osvježiti stranicu ili pokušajte kasnije";
    }
  }
}
