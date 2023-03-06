import {Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MovieService} from "../../../shared/services/movie.service";
import {Router} from "@angular/router";
import {catchError, interval, of, Subscription, switchMap, take} from "rxjs";
import {AuthenticationService} from "../../../shared/services/authentication.service";
import {User} from "../../../shared/models/user";
import {EmailService} from "../../../shared/services/email.service";
import {PaymentService} from "../../../shared/services/payment.service";
import {Movie} from "../../../shared/models/movie.model";
import {Event} from "../../event.model";
import * as moment from "moment";

declare var paypal: any;

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent implements OnInit, OnDestroy {
  ticketInfo: any = {
    cinemaHallName: "",
    movie: new Movie('', '', '', "", 0, ''),
    event: new Event('', moment(), '', '', 0),
    reservationSeats: [],
    price: 0,
    amount: 0
  };
  transaction: boolean = false;
  buyingFailed: boolean = false;
  email: string = "";
  sendEmail: boolean = false;
  gmailRequired: boolean = false;
  orderId: string = "";
  errorMessagePayment: string = "";
  currentReservationIdAndUserId: { idReservation: string, idUser: string } = {idReservation: "", idUser: ""};
  idUser = ""
  readonly DATE_FORMAT: string = 'DD-MM-yyyy';
  readonly HOUR_FORMAT: string = "HH:mm Z z";
  readonly NUMBER_FORMAT: string = '1.2-2';
  created = false;
  private subscription: Subscription = new Subscription();
  timer: number = 0;
  private timerSubscription: Subscription = new Subscription();

  @ViewChild('paypalRef', {static: true}) private paypalRef: ElementRef | undefined;


  constructor(private authentication: AuthenticationService, private movieService: MovieService, private router: Router, private emailService: EmailService, private paymentService: PaymentService, private ngZone: NgZone) {
  }

  async createOrder() {
    return await this.paymentService.createOrder({price: this.ticketInfo.price * this.ticketInfo.amount});
  }

  async captureOrder(captureRequest: { idOrder: string, idReservation: string, idUser: string, emailInfo: { userEmail: string, cinemaHallName: string, title: string, dateString: string, hourString: string, price: number, userReservationSeats: { rowNumber: number, seatNumber: number, idReservationSeat: string }[] } }) {
    return await this.paymentService.captureOrder(captureRequest);
  }


  ngOnInit(): void {

    let subscription = this.movieService.currentReservationIdAndUserId.subscribe((currentReservationIdAndUserId) => {
      this.currentReservationIdAndUserId = currentReservationIdAndUserId;
    })

    let reservationTime = this.movieService.reservationTime;
    let timeLeft = 420;
    if (reservationTime != null) {
      timeLeft = timeLeft - moment.duration(moment.utc().diff(reservationTime)).asSeconds();
    }
    this.subscription.add(subscription);

    subscription = this.timerSubscription = interval(1000).pipe(
      switchMap((num: number) => {
        if (num >= timeLeft + 5) {
          return this.movieService.currentReservationIdAndUserId;
        } else {
          return of(num);
        }
      }),
      switchMap((value: { idReservation: string, idUser: string } | number) => {
        if (typeof value != 'number') {
          if (value.idReservation && value.idUser) {
            return this.movieService.deleteReservationByIdAndUserId(value.idReservation, value.idUser)
          }
          return of(426)
        } else {
          return of(value);
        }
      })
    ).subscribe({
      next: (value: any) => {
        if (typeof value != 'number') {
          this.movieService.setCurrentReservationIdAndUserId({idReservation: "", idUser: ""});
          alert("Isteklo je vrijeme za rezervaciju")
          this.router.navigate(['/']).then();
        } else {
          this.timer = timeLeft - value;
        }
      },
      error: () => {
        this.movieService.setCurrentReservationIdAndUserId({idReservation: "", idUser: ""});
        this.router.navigate(['/']).then();

      }
    });

    this.subscription.add(subscription);

    if (this.paypalRef != undefined) {
      paypal.Buttons({
        createOrder: async () => {

          return await this.createOrder().then((order: any) => {
            this.errorMessagePayment = ""
            this.orderId = order.orderId
            return order.orderId;
          }).catch(() => {
            this.errorMessagePayment = "Došlo je do pogreške. Pokušajte kasnije."
          })
        },

        onError: (err: any) => {
        },
        onCancel: (data: any) => {
        },

        onApprove: async (data: any, actions: any) => {
          this.timerSubscription.unsubscribe();
          if (this.timer <= 0) {
            this.buyingFailed = true;
            this.ngZone.run(() => {
              this.router.navigate(['error-occurred']);
            })
            return null;
          } else {
            let orderId = this.orderId;
            let idReservation = this.currentReservationIdAndUserId.idReservation;
            let idUser = this.currentReservationIdAndUserId.idUser;
            let ticketInfoes = this.getTicketsInfoForEmail(this.email);
            this.idUser = idUser;
            let captureRequest: { idOrder: string, idReservation: string, idUser: string, emailInfo: { userEmail: string, cinemaHallName: string, title: string, dateString: string, hourString: string, price: number, userReservationSeats: { rowNumber: number, seatNumber: number, idReservationSeat: string }[] } } = {
              idOrder: orderId,
              idReservation: idReservation,
              idUser: idUser,
              emailInfo: {
                userEmail: this.email,
                cinemaHallName: ticketInfoes.cinemaHallName,
                title: ticketInfoes.title,
                dateString: ticketInfoes.dateString,
                hourString: ticketInfoes.hourString,
                price: ticketInfoes.price,
                userReservationSeats: ticketInfoes.userReservationSeats
              }
            }
            if (this.orderId != "" && this.currentReservationIdAndUserId.idReservation != "" && this.currentReservationIdAndUserId.idUser != "") {
              this.transaction = true;
              return await this.captureOrder(captureRequest).then(() => {
                this.movieService.setCurrentReservationIdAndUserId({idReservation: "", idUser: ""})
                this.orderId = ""
                this.movieService.numberOfTemporaryReservations--;
                this.sendEmail = true;
                setTimeout(() => this.sendEmail = false, 15000)

              }).catch(() => {
                this.ngZone.run(() => {
                  this.router.navigate(['error-occurred']);
                })
              })
            } else {
              this.ngZone.run(() => {
                this.router.navigate(['error-occurred']);
              })
              return null;
            }

          }
        },
      }).render(this.paypalRef.nativeElement).then(() => {
        this.created = true
      }).catch(() => this.router.navigate(['/']))
    }

    subscription = this.authentication.currentUser.subscribe(
      (user: User | null) => {
        if (user) {
          if (user.email) {
            this.email = user.email;
          } else {
            this.email = "";
          }
        } else {
          this.email = "";
        }
      }
    )

    this.subscription.add(subscription);

    this.ticketInfo = this.movieService.getTicketBuying();
    if (this.ticketInfo.movie.id == '') {
      this.router.navigate(['/']).then();
    }
  }


  ngOnDestroy(): void {
    this.movieService.clearTicketBuying();
    this.movieService.setCurrentReservationIdAndUserId({idReservation: "", idUser: ""});
    this.timerSubscription.unsubscribe();
    this.subscription.unsubscribe();
  }

  sendMeTickets(email: string, newEmail: boolean) {
    if (newEmail == true) {
      if (email.endsWith("@gmail.com")) {
        this.gmailRequired = false;
        this.sendEmail = true;
        let ticketInfoes = this.getTicketsInfoForEmail(email)
        let subscription = this.emailService.sendMailWithTickets(ticketInfoes).subscribe();
        this.subscription.add(subscription)
        setTimeout(() => this.sendEmail = false, 15000)
      } else {
        this.gmailRequired = true;
      }
    } else {
      this.gmailRequired = false;
      this.sendEmail = true;
      let ticketInfoes = this.getTicketsInfoForEmail(this.email)
      let subscription = this.emailService.sendMailWithTickets(ticketInfoes).subscribe();
      this.subscription.add(subscription)
      setTimeout(() => this.sendEmail = false, 15000)
    }
  }


  getTicketsInfoForEmail(email: String) {
    let currentReservationSeats: { rowNumber: number, seatNumber: number, idReservationSeat: string }[] = this.setReservations();

    let dateString = moment(this.ticketInfo.event.date).format('DD-MM-yyyy').toString();
    let hourString = moment(this.ticketInfo.event.date).format('HH:mm Z z').toString();

    return {
      userEmail: email,
      cinemaHallName: this.ticketInfo.cinemaHallName,
      title: this.ticketInfo.movie.title,
      dateString: dateString,
      hourString: hourString,
      price: this.ticketInfo.event.price,
      userReservationSeats: currentReservationSeats
    };
  }

  getTicketsInfo() {
    let currentReservationSeats: { rowNumber: number, seatNumber: number, idReservationSeat: string }[] = this.setReservations();

    let dateString = moment(this.ticketInfo.event.date).format('DD-MM-yyyy').toString();
    let hourString = moment(this.ticketInfo.event.date).format('HH:mm Z z').toString();

    return {
      cinemaHallName: this.ticketInfo.cinemaHallName,
      title: this.ticketInfo.movie.title,
      dateString: dateString,
      hourString: hourString,
      price: this.ticketInfo.event.price,
      userReservationSeats: currentReservationSeats
    };
  }

  setReservations() {
    let currentReservations: { rowNumber: number, seatNumber: number, idReservationSeat: string }[] = [];
    for (let reservation of this.ticketInfo.reservations) {
      currentReservations.push({
        rowNumber: reservation.rowNumber,
        seatNumber: reservation.seatNumber,
        idReservationSeat: reservation.id
      })
    }
    return currentReservations;
  }


  downloadTickets() {
    let ticketInfoes = this.getTicketsInfo()
    let subscribe = this.movieService.downloadPdf(ticketInfoes, this.idUser).subscribe({
        next: (pdf: any) => {
          this.movieService.getPdf(pdf)
        }
      }
    )

    this.subscription.add(subscribe)
  }

  goBack() {
    let subscription = this.movieService.currentReservationIdAndUserId.pipe(
      (take(1)),
      switchMap((value: { idReservation: string, idUser: string } | number) => {
        if (typeof value != 'boolean' && typeof value != 'number') {
          return this.movieService.deleteReservationByIdAndUserId(value.idReservation, value.idUser)
        } else {
          return of(true);
        }
      })
    ).pipe(catchError(() => {
      return of(true)
    })).subscribe((value: any) => {
      this.movieService.clearTicketBuying();
      this.movieService.setCurrentReservationIdAndUserId({idReservation: "", idUser: ""});
      this.router.navigate(['/calendar/', this.ticketInfo.movie.title, this.ticketInfo.event.id]).then();
    })

    this.subscription.add(subscription)
  }
}
