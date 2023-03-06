import {Component, OnInit} from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormControl,
  FormGroup,
  Validators
} from "@angular/forms";
import {Movie} from "../../shared/models/movie.model";
import {Subscription} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {MovieService} from "../../shared/services/movie.service";
import {CinemaHall} from "../../shared/models/cinema-hall.model";

@Component({
  selector: 'app-movie-edit',
  templateUrl: './movie-edit.component.html',
  styleUrls: ['./movie-edit.component.css']
})
export class MovieEditComponent implements OnInit {

  movieForm: FormGroup = new FormGroup({
    'id': new FormControl(null),
    'title': new FormControl('', [Validators.required, Validators.maxLength(100)]),
    'posterFile': new FormControl(''),
    'summary': new FormControl('', [Validators.required, Validators.maxLength(3000)]),
    'duration': new FormControl('', [Validators.required, Validators.pattern("[0-9]*"), this.movieService.positiveNumber.bind(this), this.movieService.maxNumberDuration.bind(this)]),
  });
  movieId: string = '';
  editingMovie: Movie = new Movie('', '', '', '', 0, '', '');
  loading: boolean = false;
  errorMessage: string = '';
  cinemaHalls: CinemaHall[] = [];
  posterImage = null;


  private subscription: Subscription = new Subscription();

  constructor(private router: Router, private route: ActivatedRoute, private movieService: MovieService) {
  }

  ngOnInit(): void {
    let subscription = this.movieService.getCinemaHalls().subscribe(
      (cinemaHalls: any) => this.cinemaHalls = cinemaHalls
    )

    this.subscription.add(subscription);

    if (this.route.snapshot.params['id']) {
      this.movieId = this.route.snapshot.params['id'];
      let subscription = this.movieService.getMovieByIdMovieAdmin(this.movieId).subscribe({
          next: (movie: Movie) => {
            this.editingMovie = movie;
            if (!this.editingMovie.id) {
              return
            }
            this.movieForm = new FormGroup({
              'id': new FormControl(this.editingMovie.id),
              'title': new FormControl(this.editingMovie.title, [Validators.required, Validators.maxLength(100)]),
              'posterFile': new FormControl(''),
              'summary': new FormControl(this.editingMovie.summary, [Validators.required, Validators.maxLength(3000)]),
              'duration': new FormControl(this.editingMovie.duration, [Validators.required, Validators.pattern("[0-9]*"), this.movieService.positiveNumber.bind(this), this.movieService.maxNumberDuration.bind(this)]),
            });
          },
          error: () => {
            this.router.navigate(["error-occurred"]).then();
          }
        }
      );
      this.subscription.add(subscription)
    } else {
      this.router.navigate(["error-occurred"]).then();
    }

  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  get controls(): AbstractControl[] {
    return (<FormArray>this.movieForm.get('events')).controls;
  }

  onSubmit() {
    if (confirm("Da li ste sigurni da želite ažurirati film")) {
      let subscription = this.movieService.updateMovie({
        title: this.movieForm.value['title'],
        summary: this.movieForm.value['summary'],
        duration: this.movieForm.value['duration'],
        id: this.movieForm.value['id']
      }, this.posterImage, this.editingMovie.imageName)
        .subscribe({
            next: () => {
              this.errorMessage = "";
              this.router.navigate(['/administration/movies']).then();
            },
            error: (error) => {
              if (error.status == "500") {
                this.errorMessage = "Ažuriranje filma nije uspjelo. Možda je veličina postera pre velika, pogledajte da li slika ima više od 900KB";
              } else if (error.status == "406") {
                this.errorMessage = "Ažuriranje filma nije uspjelo. Slika mora biti jpg ili png";
              } else {
                this.errorMessage = "Ažuriranje nije bilo moguće";
              }
              this.loading = false;
            }
          }
        )
      this.subscription.add(subscription)
    }
  }

  selectImage(event: any) {
    this.posterImage = event.target.files[0];
  }
}
