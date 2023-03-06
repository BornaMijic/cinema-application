import {Component, OnDestroy, OnInit} from '@angular/core';
import {MovieService} from "../../shared/services/movie.service";
import {Router} from "@angular/router";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-cinema-hall-insert',
  templateUrl: './cinema-hall-insert.component.html',
  styleUrls: ['./cinema-hall-insert.component.css']
})
export class CinemaHallInsertComponent implements OnInit, OnDestroy {

  seats: { gridPosition: number, exist: boolean }[] = [];
  loading: boolean = false;
  gridRowsNumber: number = 0;
  gridColumnsNumber: number = 0;
  errorMessage: string = "";
  loadingSeats = false;
  createCinemaHall = false;

  private subscription: Subscription = new Subscription;


  constructor(private movieService: MovieService, private router: Router) {
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  generateCinemaHall(rows: number, columns: number) {
    if (!(rows <= 20 && rows > 0) || !(columns <= 20 && columns > 0)) {
      this.errorMessage = "Broj redova ili kolona mora biti minimalno 1, ali ne više od 20"
    } else if (Math.floor(rows) !== rows || Math.floor(columns) !== columns) {
      this.errorMessage = "Broj redova ili kolona mora biti cijeli broj"
    } else {

      this.errorMessage = "";
      this.loadingSeats = false
      this.gridRowsNumber = rows;
      this.gridColumnsNumber = columns;
      this.loading = true
      this.seats = []

      for (let i = 1; i <= rows * columns; i++) {
        this.seats.push({gridPosition: i, exist: false})
      }

      this.loading = false;
      this.createCinemaHall = true;
    }
  }

  addSeat(seat: { gridPosition: number, exist: boolean }) {
    this.errorMessage = ""
    seat.exist = true;
  }

  removeSeat(seat: { gridPosition: number, exist: boolean }) {
    this.errorMessage = ""
    seat.exist = false;
  }


  onSubmit(cinemaHallName: string) {
    if (confirm("Da li ste sigurni da želite dodani dvoranu")) {
      let gridRowsNumber = this.gridRowsNumber;
      let gridColumnsNumber = this.gridColumnsNumber;
      let numberOfSeats = 0;
      this.seats = this.seats.sort((seatA, seatB) => seatA.gridPosition - seatB.gridPosition)
      let seatNumberI = 0;
      let seats: { seatNumber: number, rowNumber: number, gridPosition: number }[] = [];
      let currentRow = 1;
      let i = 0;
      let changeGridRow = false;
      this.seats.forEach(
        (seat) => {
          ++i;
          if (seat.exist && changeGridRow == true && numberOfSeats >= 1) {
            ++currentRow;
            changeGridRow = false;
          }
          if (seat.exist) {
            ++seatNumberI;
            ++numberOfSeats;
            seats.push({seatNumber: seatNumberI, rowNumber: currentRow, gridPosition: seat.gridPosition})
          }

          if (i % gridColumnsNumber == 0) {
            if (numberOfSeats >= 1) {
              changeGridRow = true;
            }
            seatNumberI = 0;
          }
        }
      );

      if (numberOfSeats == 0) {
        this.errorMessage = "Moraš izabrati minimalno jedno mjesto";
      } else {
        let cinemaHall: { name: string, rows: number, columns: number, seats: { seatNumber: number | null, rowNumber: number, gridPosition: number }[] } = {
          name: cinemaHallName,
          rows: gridRowsNumber,
          columns: gridColumnsNumber,
          seats: seats
        }
        let subscription = this.movieService.addCinemaHall(cinemaHall).subscribe(
          {
            next: () => {
              this.errorMessage = "";
              this.router.navigate(["/administration/cinemaHalls"]);
            },
            error: () => {
              this.errorMessage = "Kreiranje nije bilo uspješno. Pogledaj da li je ime već zauzeto";
            }
          });

        this.subscription.add(subscription)
      }
    }
  }

  deleteCinemaHall() {
    this.loadingSeats = false;
    this.errorMessage = ""
    this.gridRowsNumber = 0;
    this.gridColumnsNumber = 0;
    this.seats = []
    this.createCinemaHall = false;
  }
}
