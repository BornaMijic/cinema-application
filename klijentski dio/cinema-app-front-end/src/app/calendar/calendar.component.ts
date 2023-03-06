import {Component, OnDestroy, OnInit} from '@angular/core';
import {Movie} from "../shared/models/movie.model";
import {Event} from "./event.model";
import {Subscription} from "rxjs";
import {MovieService} from "../shared/services/movie.service";
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import * as moment from "moment";

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit,OnDestroy {

  dates: moment.Moment[] = [];
  events: Event[] = [];
  movies: Movie[] = [];
  private subscription: Subscription = new Subscription();
  moviesAndEvents: {event: Event, movie: Movie}[] = [];
  readonly DATE_FORMAT: string = 'DD-MM-yyyy';
  readonly HOUR_FORMAT: string = "HH:mm Z z";
  message: string = "";
  loading = false;

  constructor(private movieService: MovieService, private router: Router, private http: HttpClient) { }

  ngOnInit(): void {
    this.loading = true;
    let subscription = this.movieService.getEventsWhichWillHappenInNext30Days().subscribe(
      {next: (value: {currentDate: Date, eventAndMovie: {event: Event, movie: Movie}[]}) => {
        let currentDate: moment.Moment = moment.utc(value.currentDate).local();
        for(let i = 0; i < 30; ++i) {
          this.dates.push(currentDate);
          currentDate = moment.utc(currentDate).local().add(1,"days");
        }

        for(let movieAndEvent of value.eventAndMovie) {
          movieAndEvent.event.date = moment.utc(movieAndEvent.event.date).local();

        }
        this.moviesAndEvents = value.eventAndMovie;
        this.message = ""
        if(this.moviesAndEvents.length == 0) {
          this.message = "U idućih 30 dana nema nove projekcije"
        }
        this.loading= false;
      },
        error: () => {
        this.message = "Nažalost, projekcije se nisu mogle očitati";
          this.loading= false;
        }
      }
    );

    this.subscription.add(subscription)

  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }


  getEvents(date: moment.Moment): any[]{
    let moviesAndEventsSorted: {event: Event, movie: Movie}[] = this.moviesAndEvents.filter(movieAndEvent => {
      if (movieAndEvent.event.date.year() == date.year() &&
        movieAndEvent.event.date.month() == date.month() &&
        movieAndEvent.event.date.date() == date.date() &&
        movieAndEvent.event.date.day == date.day) {
        return true;
      }
      return false;
    });

    return this.sortByDate(moviesAndEventsSorted)
  }

  navigateToEvent(movieAndEvent: {event: Event, movie: Movie}|null){
    if(movieAndEvent != null) {
      let title: string | null = movieAndEvent.movie.title;
      if(title) {
        this.router.navigate(['/calendar', title, movieAndEvent.event.id]);
      }
    }

  }

  private sortByDate(moviesAndEventsSorted: {event: Event, movie: Movie}[]): {event: Event, movie: Movie}[] {
    return moviesAndEventsSorted.sort((a: {event: Event, movie: Movie}, b: {event: Event, movie: Movie}) => {
      return a.event.date.diff(b.event.date)
    })
  }
}

