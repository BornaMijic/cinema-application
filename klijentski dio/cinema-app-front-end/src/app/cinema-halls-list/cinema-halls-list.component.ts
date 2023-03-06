import {Component, OnInit} from '@angular/core';
import {Subscription, switchMap} from "rxjs";
import {MovieService} from "../shared/services/movie.service";
import {CinemaHall} from "../shared/models/cinema-hall.model";

@Component({
  selector: 'app-cinema-halls-list',
  templateUrl: './cinema-halls-list.component.html',
  styleUrls: ['./cinema-halls-list.component.css']
})
export class CinemaHallsListComponent implements OnInit {
  cinemaHalls: CinemaHall[] = [];
  amountOfCinemaHalls: number = 0;
  pageNumber = 1;
  search = "";
  filteringCinemaHalls: boolean = false;
  errorMessage: string = ""
  private subscription: Subscription = new Subscription();

  constructor(private movieService: MovieService) {
  }

  ngOnInit(): void {
    let subscription = this.movieService.findTenCinemaHallsPerPage(0).subscribe(
      (cinemaHallPage: { count: number, cinemaHalls: CinemaHall[] }) => {
        this.errorMessage = "";
        this.amountOfCinemaHalls = cinemaHallPage.count;
        this.cinemaHalls = cinemaHallPage.cinemaHalls;
      }
    )

    this.subscription.add(subscription);
  }

  onPageChange(event: any) {
    if (this.filteringCinemaHalls == false) {
      this.pageNumber = event
      let subscription = this.movieService.findTenCinemaHallsPerPage(event - 1).subscribe((cinemaHallPage: { count: number, cinemaHalls: CinemaHall[] }) => {
        this.errorMessage = "";
        this.pageNumber = event;
        this.amountOfCinemaHalls = cinemaHallPage.count;
        this.cinemaHalls = cinemaHallPage.cinemaHalls;
      })

      this.subscription.add(subscription)
    } else {
      this.pageNumber = event
      let subscription = this.movieService.findTenCinemaPerPageWhichContainsSearchText(event - 1, this.search).subscribe((cinemaHallPage: { count: number, cinemaHalls: CinemaHall[] }) => {
        this.errorMessage = "";
        this.pageNumber = event;
        this.amountOfCinemaHalls = cinemaHallPage.count;
        this.cinemaHalls = cinemaHallPage.cinemaHalls;
      })

      this.subscription.add(subscription)
    }
  }

  searchCinemaHalls(searchName: string) {
    this.search = searchName;
    if (this.search == "") {
      this.filteringCinemaHalls = false;
      let subscription = this.movieService.findTenCinemaHallsPerPage(0).subscribe((cinemaHallPage: { count: number, cinemaHalls: CinemaHall[] }) => {
        this.errorMessage = "";
        this.amountOfCinemaHalls = cinemaHallPage.count;
        this.cinemaHalls = cinemaHallPage.cinemaHalls;
      })
      this.subscription.add(subscription)

    } else {
      this.filteringCinemaHalls = true;
      let subscription = this.movieService.findTenCinemaPerPageWhichContainsSearchText(0, this.search).subscribe((cinemaHallPage: { count: number, cinemaHalls: CinemaHall[] }) => {
        this.errorMessage = "";
        this.amountOfCinemaHalls = cinemaHallPage.count;
        this.cinemaHalls = cinemaHallPage.cinemaHalls;
      })
      this.subscription.add(subscription)
    }
    this.pageNumber = 1;
  }

  deleteCinemaHall(id: string, name: string) {
    if (confirm("Da li ste sigurni da želiš izbrisati dvoranu " + name)) {
      let deleteSuccess = false;
      if (id) {
        let subscription = this.movieService.deleteCinemaHallById(id).pipe(
          switchMap(() => {
            deleteSuccess = true;
            if (this.search.length > 0) {
              return this.movieService.findTenCinemaPerPageWhichContainsSearchText(0, this.search);
            }
            return this.movieService.findTenCinemaHallsPerPage(0);
          })
        ).subscribe({
          next: (cinemaHallPage: any) => {
            this.pageNumber = 1;
            this.amountOfCinemaHalls = cinemaHallPage.count;
            this.cinemaHalls = cinemaHallPage.cinemaHalls;
            this.errorMessage = "";
            window.scroll(0, 0);
          },
          error: () => this.errorMessage = "Brisanje nije bilo moguće"
        })

        this.subscription.add(subscription);
      } else {
        this.errorMessage = "Brisanje nije bilo moguće";
      }
    }
  }


}
