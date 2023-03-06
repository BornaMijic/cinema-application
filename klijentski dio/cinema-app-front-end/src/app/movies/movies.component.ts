import {Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import {Movie} from "../shared/models/movie.model";
import {Subscription, switchMap} from "rxjs";
import {MovieService} from "../shared/services/movie.service";
import {Event} from "../calendar/event.model";
import {Router} from "@angular/router";

@Component({
  selector: 'app-movies',
  templateUrl: './movies.component.html',
  styleUrls: ['./movies.component.css']
})
export class MoviesComponent implements OnInit, OnDestroy {
  amountOfMovies: number = 0;
  pageNumber: number = 1;
  currentSelectedMovie: Movie = new Movie('', '', '', '', 0, '');
  movies: Movie[] = [];
  events: Event[] = []
  errorMessage: string = '';
  search: string = "";
  isAdministration: boolean = false;
  filteringMovies: boolean = false;

  start: number = 0;
  end: number = 5;

  innerWidth: any;

  private subscription: Subscription = new Subscription();

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.innerWidth = window.innerWidth;
    if (this.innerWidth <= 991) {
      this.currentSelectedMovie = new Movie('', '', '', '', 0, '');
      this.movieService.clearSelectedMovie()
    }
  }


  constructor(private movieService: MovieService, private router: Router) {
  }

  ngOnInit(): void {
    this.innerWidth = window.innerWidth;
    if (this.router.url == "/administration/movies") {
      this.isAdministration = true;
    }

    if (this.isAdministration) {
      let subscription = this.movieService.findFiveMoviesPerPageAdmin(0).subscribe((moviesPage: any) => {
        this.amountOfMovies = moviesPage.count
        this.movies = moviesPage.moviesDTOList;
      })
      this.subscription.add(subscription);
    } else {
      let subscription = this.movieService.findFiveMoviesPerPage(0).subscribe((moviesPage: any) => {
        this.amountOfMovies = moviesPage.count
        this.movies = moviesPage.moviesDTOList;
      })
      this.subscription.add(subscription);
    }


  }

  ngOnDestroy(): void {
    this.movieService.clearSelectedMovie()
    this.subscription.unsubscribe();
  }

  startEditing(movieId: string) {
    this.router.navigate(['administration/movies/edit/', movieId])
  }

  deleteMovie(event: any, movieId: string, title: string, imageName?: string) {
    event.stopPropagation();
    if (confirm("Da li ste sigurni da želite izbrisati film " + title)) {
      let deletingMovie = false;
      this.movieService.deleteMovie(movieId, imageName).pipe(
        switchMap(() => {
          deletingMovie = true;
          if (this.search.length > 0) {
            return this.movieService.findFiveMoviesPerPageWhichContainsSearchTextAdmin(0, this.search)
          }
          return this.movieService.findFiveMoviesPerPageAdmin(0)
        })
      ).subscribe(
        {
          next: (moviesPage: any) => {
            this.amountOfMovies = moviesPage.count
            this.movies = moviesPage.moviesDTOList;
            this.pageNumber = 1;
            if (this.currentSelectedMovie.id = movieId) {
              this.currentSelectedMovie = new Movie('', '', '', '', 0, '');
            }
            this.errorMessage = "";
            window.scroll(0, 0);
          },
          error: () => {
            if (!deletingMovie) {
              this.errorMessage = "Brisanje filma nije bilo moguće";
            } else {
              this.errorMessage = "Nažalost, nismo mogli oćitati filmvoe";
            }
            window.scroll(0, 0);
          }
        }
      )
    }
  }

  onPageChange(event: any) {
    if (this.filteringMovies == false) {
      if (this.isAdministration) {
        let subscription = this.movieService.findFiveMoviesPerPageAdmin(event - 1).subscribe((moviesPage: any) => {
          this.pageNumber = event
          this.amountOfMovies = moviesPage.count
          this.movies = moviesPage.moviesDTOList;
          this.currentSelectedMovie = new Movie('', '', '', '', 0, '');
        })

        this.subscription.add(subscription)
      } else {
        let subscription = this.movieService.findFiveMoviesPerPage(event - 1).subscribe((moviesPage: any) => {
          this.pageNumber = event
          this.amountOfMovies = moviesPage.count
          this.movies = moviesPage.moviesDTOList;
          this.currentSelectedMovie = new Movie('', '', '', '', 0, '');
        })
        this.subscription.add(subscription)
      }
    } else {

      if (this.isAdministration) {
        this.pageNumber = event
        let subscription = this.movieService.findFiveMoviesPerPageWhichContainsSearchTextAdmin(event - 1, this.search).subscribe((moviesPage: any) => {
          this.pageNumber = event
          this.amountOfMovies = moviesPage.count
          this.movies = moviesPage.moviesDTOList;
          this.currentSelectedMovie = new Movie('', '', '', '', 0, '');
        })

        this.subscription.add(subscription)
      } else {
        this.pageNumber = event
        let subscription = this.movieService.findFiveMoviesPerPageWhichContainsSearchText(event - 1, this.search).subscribe((moviesPage: any) => {
          this.pageNumber = event
          this.amountOfMovies = moviesPage.count
          this.movies = moviesPage.moviesDTOList;
          this.currentSelectedMovie = new Movie('', '', '', '', 0, '');
        })

        this.subscription.add(subscription)
      }

    }
  }

  searchMovies() {
    if (this.search == "") {
      this.filteringMovies = false;
      if (this.isAdministration) {
        let subscription = this.movieService.findFiveMoviesPerPageAdmin(0).subscribe((moviesPage: any) => {
          this.amountOfMovies = moviesPage.count
          this.movies = moviesPage.moviesDTOList;
          ;
        })
        this.subscription.add(subscription)
      } else {
        let subscription = this.movieService.findFiveMoviesPerPage(0).subscribe((moviesPage: any) => {
          this.amountOfMovies = moviesPage.count
          this.movies = moviesPage.moviesDTOList;
          ;
        })
        this.subscription.add(subscription)
      }
    } else {
      this.filteringMovies = true;
      if (this.isAdministration) {
        let subscription = this.movieService.findFiveMoviesPerPageWhichContainsSearchTextAdmin(0, this.search).subscribe((moviesPage: any) => {
          this.amountOfMovies = moviesPage.count
          this.movies = moviesPage.moviesDTOList;
        })
        this.subscription.add(subscription)
      } else {
        let subscription = this.movieService.findFiveMoviesPerPageWhichContainsSearchText(0, this.search).subscribe((moviesPage: any) => {
          this.amountOfMovies = moviesPage.count
          this.movies = moviesPage.moviesDTOList;
        })
        this.subscription.add(subscription)
      }
    }
    this.pageNumber = 1;
  }

  selectedMovie(movie: Movie) {
    this.currentSelectedMovie = movie;
    this.movieService.setSelectedMovie(movie)
    window.scroll(0, 0);
  }

  navigateToMovieDetails(id: string, title: string) {
    if (this.isAdministration) {
      this.router.navigate(['/administration/movies/details/', title, id])
    } else {
      this.router.navigate(['/movies/', title, id])
    }
  }
}
