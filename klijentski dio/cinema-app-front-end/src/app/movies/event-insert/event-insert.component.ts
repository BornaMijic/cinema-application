import { Component, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  Validators
} from "@angular/forms";
import {Event} from "../../calendar/event.model";
import {Subscription} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {MovieService} from "../../shared/services/movie.service";
import * as moment from "moment";
import {CinemaHall} from "../../shared/models/cinema-hall.model";

@Component({
  selector: 'app-event-insert',
  templateUrl: './event-insert.component.html',
  styleUrls: ['./event-insert.component.css']
})
export class EventInsertComponent implements OnInit {

  eventForm: FormGroup = this.movieService.getEventFormWhenAdding();

  hours: string[] = this.movieService.hours;
  minutes: string[] = this.movieService.minutes;
  eventId: string = "";
  edit: boolean = false;
  editingEvent!: Event;
  loading: boolean = false;
  errorMessage: string = '';
  cinemaHalls: CinemaHall[] = [];
  idMovie: string = "";

  private subscription: Subscription = new Subscription();

  constructor(private router: Router, private route: ActivatedRoute, private movieService: MovieService) {
  }

  ngOnInit(): void {
    let subscription = this.movieService.getCinemaHalls().subscribe(
      (cinemaHalls: any) => this.cinemaHalls = cinemaHalls
    )

    this.subscription.add(subscription);

    this.eventId = this.route.snapshot.params['id'];

    if (this.eventId) {
      this.edit = true;
      let subscription = this.movieService.getEventByIdAdmin(this.eventId).subscribe({
          next: (event: Event) => {
            event.date = moment.utc(event.date).local();
            this.editingEvent = event;
            let date: string = moment.utc(event.date).local().format('YYYY-MM-DD');
            let dateHours = this.editingEvent.date.hours();
            let dateMinutes = this.editingEvent.date.minutes();
            let editHours = "";
            let editMinutes = "";
            if(dateHours < 10 && dateHours >= 0) {
              editHours = "0" + dateHours.toString();
            } else {
              editHours = dateHours.toString()
            }

            if(dateMinutes < 10 && dateMinutes >= 0) {
              editMinutes = "0" +  dateMinutes.toString();
            } else {
              editMinutes = dateMinutes.toString()
            }

            this.eventForm = new FormGroup({
              'id': new FormControl(this.editingEvent.id),
              'date': new FormControl(date, [Validators.required]),
              'startHour': new FormControl(editHours),
              'startMinute': new FormControl(editMinutes),
              'price': new FormControl(this.editingEvent.price, [Validators.required, Validators.pattern("[0-9]*.?[0-9]{1,2}"), this.movieService.positiveNumber.bind(this), this.movieService.maxNumberPrice.bind(this)]),
            }, )
          },
          error: () => {
            this.router.navigate(["error-occurred"]).then();
          }
        }
      );
      this.subscription.add(subscription)
    } else {
      this.idMovie = this.route.snapshot.params['idMovie'];
      if(this.route.snapshot.params['idMovie']) {
        this.edit = false;
      } else {
        this.router.navigate(["error-occurred"]).then();
      }
    }

  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }


  onSubmit() {
    if(this.edit) {
      if (confirm("Da li ste sigurni da želite ažurirat projekciju")) {
        this.loading = true;
        let startDate = moment(this.eventForm.value['date']);
        startDate.set({hour: this.eventForm.value['startHour'], minutes: this.eventForm.value['startMinute'], second: 0})
        let subscription = this.movieService.updateEvent({id: this.eventForm.value['id'], date:  startDate,  price:  this.eventForm.value['price']})
          .subscribe({
            next: () => {
               this.loading = false;
               this.errorMessage = "";
                this.router.navigate(['/administration/movies']).then();
                this.loading = false;
            },
            error: () => {
              this.loading = false;
              this.errorMessage = "Ažuriranje projekcije nije bilo uspješno";
            }
          })
          this.subscription.add(subscription);
      }
    } else {
      if (confirm("Da li ste sigurni da želite spremiti projekciju")) {
        this.loading = true;
        let startDate = moment(this.eventForm.value['date']);
        startDate.set({hour: this.eventForm.value['startHour'], minutes: this.eventForm.value['startMinute'], second: 0})
        let subscription = this.movieService.addEvent({date: startDate, idMovie: this.idMovie, idCinemaHall: this.eventForm.value['idCinemaHall'], price:  this.eventForm.value['price']})
        .subscribe({
          next: () => {
            this.errorMessage = "";
            this.router.navigate(['/administration/movies']).then();
            this.loading = false;

          },
          error: () => {
            this.loading = false;
            this.errorMessage = "Kreiranje projekcije nije bilo uspješno";
          }
        })
        this.subscription.add(subscription);
      }
    }
  }

  getCinemaHallName(id: string) {
    return this.cinemaHalls.find((cinemaHall: CinemaHall) => cinemaHall.id == id)?.name
  }

}
