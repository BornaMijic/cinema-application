import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {MovieService} from "../../shared/services/movie.service";
import {Event} from "../event.model";
import {Movie} from "../../shared/models/movie.model";
import {Subscription} from "rxjs";
import {CinemaHall} from "../../shared/models/cinema-hall.model";
import * as moment from "moment";
import localeIt from '@angular/common/locales/it'
import {registerLocaleData} from "@angular/common";

registerLocaleData(localeIt, 'it');

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.css']
})
export class EventDetailsComponent implements OnInit, OnDestroy {
  id: string | null = null;
  event: Event | null = null;
  movie: Movie | null = null;
  cinemaHall: CinemaHall | null = null;
  private subscription: Subscription = new Subscription();
  readonly DATE_FORMAT: string = 'DD-MM-yyyy';
  readonly HOUR_FORMAT: string = "HH:mm Z z";

  constructor(private route: ActivatedRoute, private router: Router, private movieService: MovieService) {
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id')
    if (this.id) {
      let subscription = this.movieService.getEventWithMovieAndCinemaHallById(this.id).subscribe({
        next: (eventWithMovieAndCinemaHall: { event: Event, movie: Movie, cinemaHall: CinemaHall }) => {
          this.event = eventWithMovieAndCinemaHall.event;
          this.event.date = moment.utc(this.event.date).local();

          this.movie = eventWithMovieAndCinemaHall.movie;
          this.cinemaHall = eventWithMovieAndCinemaHall.cinemaHall;
        },
        error: () => {
          this.router.navigate(["error-occurred"]).then();
        }
      });

      this.subscription.add(subscription);
    }
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe()
  }


}
