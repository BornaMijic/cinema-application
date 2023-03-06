import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {MovieService} from "../../shared/services/movie.service";
import {Movie} from "../../shared/models/movie.model";
import {ActivatedRoute, Router} from "@angular/router";
import {map, mergeMap, Subscription} from "rxjs";
import * as moment from "moment";


@Component({
  selector: 'app-movie-details',
  templateUrl: './movie-details.component.html',
  styleUrls: ['./movie-details.component.css']
})
export class MovieDetailsComponent implements OnInit, OnDestroy {

  @Input() selectedMovie: Movie = new Movie('', '', '', '',  0, '');
  @Input() isAdministration = false;
  @Input() moviesSize = 0;
  events: {id: string, date: moment.Moment, price: number, cinemaHallName: string}[] = []
  loadingPage: boolean = false;
  loadingMovieMessage: string = "";
  errorDeletingEvent: string = "";
  readonly DATE_FORMAT: string = 'DD-MM-yyyy';
  readonly HOUR_FORMAT: string = "HH:mm Z z";

  private subscription: Subscription = new Subscription();

  constructor(private movieService: MovieService, private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    if (this.route.snapshot.params['id']) {
      let id = this.route.snapshot.params['id'];
      this.selectedMovie.id = id;
      this.loadingPage = true;
      if (this.router.url.includes("/administration/movies")) {
        this.isAdministration = true;
      }
      if (this.isAdministration) {
        let subscription = this.movieService.getMovieByIdMovieAdmin(id).pipe(
          mergeMap((movie: Movie) => {
              this.selectedMovie = movie;
              return this.movieService.getEventsBySpecificMovieIdAdmin(movie.id);
            }
          )
        ).subscribe({
          next: (events: {id: string, date: moment.Moment, price: number, cinemaHallName: string}[]) => {
            this.events = this.sortByDate(events);
            this.loadingMovieMessage = ""
          },
          error: () => {
            this.router.navigate(["error-occurred"]).then();
          }
        })
        this.subscription.add(subscription)
      } else {
        this.loadingPage = true;
        let subscription = this.movieService.getMovieByIdMovie(id).pipe(
          mergeMap((movie: Movie) => {
              this.selectedMovie = movie;
              return this.movieService.getEventsBySpecificMovieId(movie.id);
            }
          )
        ).subscribe({
          next: (events: {id: string, date: moment.Moment, price: number, cinemaHallName: string}[]) => {
            this.events = this.sortByDate(events);
            this.loadingMovieMessage = ""
          },
          error: () => {
            this.router.navigate(["error-occurred"]).then();
          }
        })
        this.subscription.add(subscription)
      }
    } else {
        this.loadingPage = true;
        if (this.isAdministration) {
          let subscription = this.movieService.selectedMovie.pipe(
            mergeMap((movie: Movie) => {
              this.errorDeletingEvent = ""
              if(movie.id == "") {
                return []
              } else {
                return this.movieService.getMovieByIdMovieAdmin(movie.id)
              }
            }),
          mergeMap((movie: Movie) => {
              return this.movieService.getEventsBySpecificMovieIdAdmin(movie.id).pipe(map(events => [movie, events]))
            }))
            .subscribe({
              next: (value: any[]) => {
                if(value[0].id == "") {
                  this.loadingMovieMessage = "Nismo mogli očitati film";
                } else {
                  this.loadingMovieMessage = "";
                  this.selectedMovie = value[0]
                  this.events = this.sortByDate(value[1])
                }
              },
              error: () => {
                this.router.navigate(["error-occurred"]).then();
              }
            });
          this.subscription.add(subscription)
        } else {
          this.loadingPage = true;
          let subscription = this.movieService.selectedMovie.pipe(
            mergeMap((movie: Movie) => {
              if(movie.id == "") {
                return []
              } else {
                return this.movieService.getMovieByIdMovie(movie.id)
              }
            }),
            mergeMap((movie: Movie) => {
              return this.movieService.getEventsBySpecificMovieId(movie.id).pipe(map(events => [movie, events]))
            }))
            .subscribe({
              next: (value: any[]) => {
                if(value[1].length <= 0 || value[0].id == "") {
                  this.loadingMovieMessage = "Nismo mogli očitati projekcije";
                } else {
                  this.loadingMovieMessage = "";
                  this.selectedMovie = value[0]
                  this.events = this.sortByDate(value[1])
                }
              },
              error: () => {
                this.router.navigate(["error-occurred"]).then();
              }
            });
          this.subscription.add(subscription)
        }
    }

  }


  ngOnDestroy(): void {
    this.subscription.unsubscribe()
  }

  private sortByDate(events: {id: string, date: moment.Moment,  price: number, cinemaHallName: string}[]): {id: string, date: moment.Moment,  price: number, cinemaHallName: string}[] {

    events.forEach(event => {
      event.date = moment.utc(event.date).local();
    });
    return events.sort((a: {id: string, date: moment.Moment,  price: number, cinemaHallName: string}, b: {id: string, date: moment.Moment, price: number, cinemaHallName: string}) => {

      return a.date.diff(b.date)
    })
  }

  deleteEvent(event: any, id: string) {
    event.stopPropagation();
    if(confirm("Da li ste sigurno da želite izbrisati projekcijuDo you want to delete this event")) {
      let subscription = this.movieService.deleteEvent(id).subscribe(
        {next:() => {
            let index = this.events.findIndex(ev => ev.id == id);
            this.events.splice(index, 1)
            this.errorDeletingEvent = "";
          },
          error: () => {
            this.errorDeletingEvent = "Brisanje nije bilo moguće";
            window.scroll(0,0);
          }
        }
      );

      this.subscription.add(subscription)
    }
  }
}
