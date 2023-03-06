import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormControl,
  FormGroup,
  Validators
} from "@angular/forms";
import {MovieService} from "../../shared/services/movie.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Subscription} from "rxjs";

import * as moment from "moment";
import {CinemaHall} from "../../shared/models/cinema-hall.model";

@Component({
  selector: 'app-movie-insert',
  templateUrl: './movie-insert.component.html',
  styleUrls: ['./movie-insert.component.css']
})
export class MovieInsertComponent implements OnInit, OnDestroy {
  movieForm: FormGroup = new FormGroup({
    'id': new FormControl(null),
    'title': new FormControl('', [Validators.required, Validators.maxLength(100)]),
    'posterFile': new FormControl('', [Validators.required, Validators.maxLength(255)]),
    'summary': new FormControl('', [Validators.required, Validators.maxLength(3000)]),
    'duration': new FormControl('', [Validators.required, Validators.pattern("[0-9]*"), this.movieService.positiveNumber.bind(this), this.movieService.maxNumberDuration.bind(this)]),
    'events': new FormArray([])
  });
  hours: string[] = this.movieService.hours;
  minutes: string[] = this.movieService.minutes;
  movieId: string = '';
  creatingMovie: boolean = true;
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
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  get controls(): AbstractControl[] {
    return (<FormArray>this.movieForm.get('events')).controls;
  }

  onAddEvent() {
    (<FormArray>this.movieForm.get('events')).push(
      this.movieService.getEventFormWhenAdding()
    )

  }

  deleteEvent(index: number, id?: string): void {
    (<FormArray>this.movieForm.get('events')).removeAt(index);
  }

  createMovie() {
    if (this.posterImage == null) {
      this.errorMessage = "Kreiranje filma nije uspjelo";
    } else {
      let subscription = this.movieService.addMovie({
        title: this.movieForm.value['title'],
        summary: this.movieForm.value['summary'],
        duration: this.movieForm.value['duration']
      }, this.posterImage)
        .subscribe({
          next: (response: { idMovie: string }) => {
            this.errorMessage = ""
            this.creatingMovie = false;
            this.movieId = response.idMovie;
          },
          error: (error) => {
            if (error.status == "500") {
              this.errorMessage = "Kreiranje filma nije uspjelo. Možda je veličina postera pre velika, pogledajte da li slika ima više od 900KB";
            } else if (error.status == "406") {
              this.errorMessage = "Kreiranje filma nije uspjelo. Slika mora biti jpg ili png";
            } else {
              this.errorMessage = "Kreiranje filma nije uspjelo";
            }
            window.scroll(0, 0);
          }
        });
      this.subscription.add(subscription);
    }


  }

  onSubmit() {
    if (confirm("Da li ste sigurni da želite kreirati projekciju")) {
      this.createEvents()
    }
  }

  createEvents() {
    if (this.movieForm.value['events'].length > 0) {
      let idMovie = this.movieId;
      let events: { date: moment.Moment, startHour: string, idMovie: string, idCinemaHall: string, price: number }[] = []
      this.movieForm.value['events'].map((ev: any) => {
        let startDate = moment(ev['date']);
        startDate.set({hour: ev['startHour'], minutes: ev['startMinute']})
        events.push({
          date: startDate,
          startHour: ev['startHour'] + ":" + ev['startMinute'],
          idMovie: idMovie,
          idCinemaHall: ev['idCinemaHall'],
          price: ev['price']
        })
      })
      let subscription = this.movieService
        .addEvents(events)
        .subscribe(
          {
            next: () => {
              this.errorMessage = ""
              this.router.navigate(['/administration/movies']).then();
            },
            error: () => {
              this.loading = false;
              this.errorMessage = "Kreiranje projekcija nije bilo moguće";
            },
          }
        )
      this.subscription.add(subscription);
    } else {
      this.router.navigate(['/administration/movies']).then()
    }
  }

  checkIfDateBefore(dateEvent: Date, startHour: string, startMinute: string): boolean {
    const date = new Date;

    dateEvent.setHours(Number(startHour[0]), Number(startMinute[1]));
    return dateEvent < date;

  }

  selectImage(event: any) {
    this.posterImage = event.target.files[0];
  }
}
