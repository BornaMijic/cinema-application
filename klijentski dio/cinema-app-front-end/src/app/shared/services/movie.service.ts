import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {Event} from "../../calendar/event.model";
import {Movie} from "../models/movie.model";
import {Reservation} from "../models/reservation.model";
import {DataStorageService} from "./data-storage.service";
import {CinemaHall} from "../models/cinema-hall.model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import * as moment from "moment";

@Injectable({
  providedIn: 'root'
})
export class MovieService {
  movieSubject: Subject<Movie[]> = new Subject<Movie[]>();

  eventSubject: Subject<Event[]> = new Subject<Event[]>();

  currentReservationIdAndUserId: BehaviorSubject<{ idReservation: string, idUser: string }> = new BehaviorSubject({
    idReservation: "",
    idUser: ""
  });

  numberOfTemporaryReservations: number = 0;

  loadReservation: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  loadManageReservations: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  reservationTime: moment.Moment | null = null;


  selectedMovie: BehaviorSubject<Movie> = new BehaviorSubject<Movie>(new Movie('', '', '', '', 0, ''));

  ticketBuying: any = {
    cinemaHallName: "",
    movie: new Movie('', '', '', '', 0, ''),
    event: new Event('', moment(), '', '', 0),
    reservations: [],
    price: 0,
    amount: 0
  }

  hours: string[] = ["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
    "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"];
  minutes: string[] = ["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
    "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24",
    "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36",
    "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48",
    "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"];

  constructor(private dataStorageService: DataStorageService) {
  }

  findFiveMoviesPerPage(pageNumber: number): Observable<{ count: number; moviesDTOList: Movie[] }> {
    return this.dataStorageService.findFiveMoviesPerPage(pageNumber);
  }

  findFiveMoviesPerPageAdmin(pageNumber: number): Observable<{ count: number; moviesDTOList: Movie[] }> {
    return this.dataStorageService.findFiveMoviesPerPageAdmin(pageNumber);
  }

  findFiveMoviesPerPageWhichContainsSearchText(pageNumber: number, searchText: string): Observable<{ count: number; moviesDTOList: Movie[] }> {
    return this.dataStorageService.findFiveMoviesPerPageWhichContainsSearchText(pageNumber, searchText);
  }

  findFiveMoviesPerPageWhichContainsSearchTextAdmin(pageNumber: number, searchText: string): Observable<{ count: number; moviesDTOList: Movie[] }> {
    return this.dataStorageService.findFiveMoviesPerPageWhichContainsSearchTextAdmin(pageNumber, searchText);
  }


  getLatestMovies(): Observable<Movie[]> {
    return this.dataStorageService.getLatestMovies()
  }

  getSoonStartingMovies(): Observable<Movie[]> {
    return this.dataStorageService.getSoonStartingMovies();
  }

  setSelectedMovie(movie: Movie) {
    this.selectedMovie.next(movie);
  }

  clearSelectedMovie() {
    this.selectedMovie = new BehaviorSubject<Movie>(new Movie('', '', '', '', 0, ''));
  }


  setTicketBuying(cinemaHallName: string, movie: Movie, event: Event, price: number, amount: number, currentReservation: { id: string, seatId: string, seatNumber: number, rowNumber: number }[]) {
    this.ticketBuying.cinemaHallName = cinemaHallName;
    this.ticketBuying.movie = movie;
    this.ticketBuying.event = event;
    this.ticketBuying.reservations = currentReservation.sort((a, b) => {
      if (a.seatNumber == undefined) {
        return 1
      }
      if (b.seatNumber == undefined) {
        return -1
      }
      return a.seatNumber - b.seatNumber;
    });
    this.ticketBuying.price = price;
    this.ticketBuying.amount = amount;
  }

  getTicketBuying() {
    return this.ticketBuying;
  }

  clearTicketBuying() {
    this.ticketBuying = {
      cinemaHallName: "",
      movie: new Movie('', '', '', '', 0, ''),
      event: new Event('', moment(), '', '', 0),
      reservations: [],
      price: 0,
      amount: 0
    }

  }

  getReservationsForSpecificEvent(eventId: string): Observable<Reservation[]> {
    return this.dataStorageService.getReservationsByEventId(eventId)
  }

  getReservationsForSpecificUser(userId: string, pageNumber: number): Observable<{ count: number, reservationsWithSeatEventMovieAndCinemaHall: { idReservation: Reservation, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[], event: Event, movie: Movie, cinemaHall: CinemaHall }[] }> {
    return this.dataStorageService.getReservationsByUserId(userId, pageNumber);
  }

  getEventsWhichWillHappenInNext30Days(): Observable<{ currentDate: Date, eventAndMovie: { event: Event, movie: Movie }[] }> {
    return this.dataStorageService.getEventsWhichWillHappenInNext30Days();
  }

  getEventWithMovieAndCinemaHallById(id: string): Observable<{ event: Event, movie: Movie, cinemaHall: CinemaHall }> {
    return this.dataStorageService.getEventWithMovieAndCinemaHallById(id)
  }

  getEventWithMovieCinemaHallAndReservationById(id: string): Observable<{ event: Event, movie: Movie, cinemaHall: CinemaHall, reservationWithSeatInfoList: { id: string, userId: string, idReservationSeat: string, eventId: string, seatId: string, seatNumber: number, rowNumber: number, name: string, surname: string }[] }> {
    return this.dataStorageService.getEventWithMovieCinemaHallAndReservationById(id)
  }

  getEventsBySpecificMovieId(idMovie: string): Observable<{ id: string, date: moment.Moment, price: number, cinemaHallName: string }[]> {
    return this.dataStorageService.getEventsByMovieId(idMovie);
  }

  getEventsBySpecificMovieIdAdmin(idMovie: string): Observable<{ id: string, date: moment.Moment, price: number, cinemaHallName: string }[]> {
    return this.dataStorageService.getEventsByMovieIdAdmin(idMovie);
  }

  getEventByIdAdmin(id: string): Observable<Event> {
    return this.dataStorageService.getEventByIdAdmin(id);
  }

  getMovieByIdMovie(idMovie: string): Observable<Movie> {
    return this.dataStorageService.getMovieById(idMovie);
  }

  getMovieByIdMovieAdmin(idMovie: string): Observable<Movie> {
    return this.dataStorageService.getMovieByIdAdmin(idMovie);
  }

  getCinemaHalls(): Observable<CinemaHall[]> {
    return this.dataStorageService.getCinemaHalls();
  }

  addMovie(movie: { title: string, summary: string, duration: number }, posterImage: any): Observable<{ idMovie: string }> {
    return this.dataStorageService.addMovie(movie, posterImage);
  }

  addEvent(event: { date: moment.Moment, idMovie: string, idCinemaHall: string, price: number }): Observable<Event> {
    return this.dataStorageService.addEvent(event);
  }

  addEvents(events: { date: moment.Moment, idMovie: string, idCinemaHall: string, price: number }[]): Observable<Event> {
    return this.dataStorageService.addEvents(events);
  }

  deleteMovie(id: string, imageName?: string) {
    return this.dataStorageService.deleteMovie(id, imageName);
  }

  updateMovie(updateMovie: { title: string, summary: string, duration: string, id: string }, posterImage: any, oldImage: string | undefined): Observable<Movie> {
    return this.dataStorageService.updateMovie(updateMovie, posterImage, oldImage);
  }

  updateEvent(updateEvent: { id: string, date: moment.Moment, price: number }) {
    return this.dataStorageService.updateEvent(updateEvent);
  }

  updateReservation(reservationSeat: { id: string, seatId: string }) {
    return this.dataStorageService.updateReservation(reservationSeat);
  }

  deleteEvent(id: string) {
    return this.dataStorageService.deleteEvent(id);
  }

  addReservation(reservation: Reservation | { userId: string | null, eventId: string, seatId: string }): Observable<{ id: string, userId: string, idReservationSeat: string, eventId: string, seatId: string, seatNumber: number, rowNumber: number, name: string, surname: string }[]> {
    return this.dataStorageService.addReservation(reservation);
  }

  addReservations(eventDate: moment.Moment, reservations: { userId: string | null, eventId: string, reservationSeats: { eventId: string, seatId: string }[] }): Observable<{ id: string, userId: string, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }> {
    return this.dataStorageService.addReservations(eventDate, reservations);
  }

  deleteReservation(id: string, idReservationSeat: string) {
    return this.dataStorageService.deleteReservation(id, idReservationSeat)
  }

  setLoadReservation(value: boolean) {
    this.loadReservation.next(value);
  }

  setCurrentReservationIdAndUserId(reservation: { idReservation: string, idUser: string }) {
    this.currentReservationIdAndUserId.next(reservation);
  }

  downloadPdf(ticketInfoes: any, idUser: string): Observable<any> {
    return this.dataStorageService.downloadPdf(ticketInfoes, idUser);
  }

  downloadPdfAdmin(ticketInfoes: any): Observable<any> {
    return this.dataStorageService.downloadPdfAdmin(ticketInfoes);
  }

  getPdf(pdf: any) {
    let binary = window.atob(pdf.pdfString);
    let bytes = Uint8Array.from(binary, element => element.charCodeAt(0))
    const pdfBlob = new Blob([bytes], {type: 'application/pdf'});
    const downloadTicketsPdf = document.createElement('a');
    downloadTicketsPdf.href = window.URL.createObjectURL(pdfBlob);
    downloadTicketsPdf.download = 'tickets';
    downloadTicketsPdf.click();
  }

  getEventFormWhenAdding() {
    return new FormGroup({
      'id': new FormControl(null),
      'date': new FormControl(null, [Validators.required]),
      'idCinemaHall': new FormControl(null, Validators.required),
      'startHour': new FormControl(null, Validators.required),
      'startMinute': new FormControl(null, Validators.required),
      'price': new FormControl(null, [Validators.required, Validators.pattern("[0-9]*.?[0-9]{1,2}"), this.positiveNumber.bind(this), this.maxNumberPrice.bind(this)]),
    })
  }

  positiveNumber(control: FormControl): { wrongNumber: boolean } | null {
    const duration = control.value;
    if (duration >= 0) {
      return null;
    } else {
      return {wrongNumber: true};
    }
  }


  maxNumberDuration(control: FormControl): { maxNumberDuration: boolean } | null {
    const duration = control.value;
    if (duration <= 10000) {
      return null;
    } else {
      return {maxNumberDuration: true};
    }
  }

  maxNumberPrice(control: FormControl): { maxNumberPrice: boolean } | null {
    const duration = control.value;
    if (duration <= 2147483647) {
      return null;
    } else {
      return {maxNumberPrice: true};
    }
  }

  deleteReservationByIdAndUserId(id: string, idUser: string) {
    return this.dataStorageService.deleteReservationByIdAndUserId(id, idUser);
  }

  confirmReservation(id: string) {
    return this.dataStorageService.confirmReservation(id)
  }

  addCinemaHall(cinemaHall: { name: string; rows: number; columns: number; seats: { seatNumber: number | null; rowNumber: number; gridPosition: number }[] }) {
    return this.dataStorageService.addCinemaHall(cinemaHall);
  }

  findTenCinemaHallsPerPage(pageNumber: number): any {
    return this.dataStorageService.findTenCinemaHallsPerPage(pageNumber);
  }

  findTenCinemaPerPageWhichContainsSearchText(pageNumber: number, searchText: string): any {
    return this.dataStorageService.findTenCinemaPerPageWhichContainsSearchText(pageNumber, searchText);
  }

  deleteCinemaHallById(id: string) {
    return this.dataStorageService.deleteCinemaHallById(id);
  }

  getReservationByIdEventAndIdUserAndUncomplete(idUser: string, idEvent: string): Observable<{ idReservation: string, reservationTime: moment.Moment, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }> {
    return this.dataStorageService.getReservationByIdEventAndIdUserAndUncomplete(idUser, idEvent);
  }

  getAllReservationByIdUserWhichAreUncomplete(idUser: string): Observable<{ idReservation: string, reservationTime: moment.Moment, movie: Movie, event: Event, cinemaHall: CinemaHall, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }[]> {
    return this.dataStorageService.getAllReservationByIdUserWhichAreUncomplete(idUser);
  }

  setReservationTime(reservationTime: moment.Moment | null) {
    this.reservationTime = reservationTime;
  }

  clearReservationTime() {
    this.reservationTime = null;
  }

  getCountOfUncompleteReservations(idUser: string): Observable<{ countUncompleteReservations: number }> {
    return this.dataStorageService.getCountOfUncompleteReservations(idUser);
  }
}
