import {Component, OnDestroy, OnInit} from '@angular/core';
import {MovieService} from "../shared/services/movie.service";
import {Movie} from "../shared/models/movie.model";
import {Subscription} from "rxjs";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit, OnDestroy {

  movies: Movie[] = [];
  startingSoonMovies: Movie[] = [];
  moviesError: boolean = false;
  startingSoonMoviesError: boolean = false;
  private subscription: Subscription = new Subscription();

  constructor(private movieService: MovieService, private http: HttpClient) {
  }

  ngOnInit(): void {

    let subscription = this.movieService.getLatestMovies().subscribe(
      {
        next: (movies: Movie[]) => {
          this.movies = movies;
          this.moviesError = false
        },
        error: () => {
          this.moviesError = true;
        }
      }
    )

    this.subscription.add(subscription);

    subscription = this.movieService.getSoonStartingMovies().subscribe(
      {
        next: (movies: Movie[]) => {
          this.startingSoonMovies = movies;
          this.startingSoonMoviesError = false
        },
        error: () => {
          this.startingSoonMoviesError = true;
        }
      }
    )

    this.subscription.add(subscription);

  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe()
  }
}
