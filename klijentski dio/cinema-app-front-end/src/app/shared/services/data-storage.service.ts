import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Movie} from "../models/movie.model";
import {Event} from "../../calendar/event.model";
import {Reservation} from "../models/reservation.model";
import {environment} from "../../../environments/environment";
import {CinemaHall} from "../models/cinema-hall.model";
import {lastValueFrom, Observable} from "rxjs";
import {User} from "../models/user";
import * as moment from "moment";

@Injectable({
  providedIn: 'root'
})
export class DataStorageService {
  constructor(private http: HttpClient) {
  }

  public findFiveMoviesPerPage(pageNumber: number): Observable<{ count: number; moviesDTOList: Movie[] }> {
    return this.http.get<{ count: number; moviesDTOList: Movie[] }>(`${environment.backend}movies/page/${pageNumber}`);
  }

  public findFiveMoviesPerPageAdmin(pageNumber: number): Observable<{ count: number; moviesDTOList: Movie[] }> {
    return this.http.get<{ count: number; moviesDTOList: Movie[] }>(`${environment.backend}movies/admin/page/${pageNumber}`);
  }

  public findFiveMoviesPerPageWhichContainsSearchText(pageNumber: number, searchText: string): Observable<{ count: number; moviesDTOList: Movie[] }> {
    return this.http.get<{ count: number; moviesDTOList: Movie[] }>(`${environment.backend}movies/page/${pageNumber}/${searchText}`);
  }

  public findFiveMoviesPerPageWhichContainsSearchTextAdmin(pageNumber: number, searchText: string): Observable<{ count: number; moviesDTOList: Movie[] }> {
    return this.http.get<{ count: number; moviesDTOList: Movie[] }>(`${environment.backend}movies/admin/page/${pageNumber}/${searchText}`);
  }

  public getCinemaHalls(): Observable<CinemaHall[]> {
    return this.http.get<CinemaHall[]>(`${environment.backend}cinema-halls`)
  }

  public deleteNewsItem(newsItemId: string) {
    return this.http.delete(`${environment.backend}news/` + newsItemId);
  }

  public getLatestMovies(): Observable<Movie[]> {
    return this.http.get<Movie[]>(`${environment.backend}movies/latest`)
  }

  getSoonStartingMovies(): Observable<Movie[]> {
    return this.http.get<Movie[]>(`${environment.backend}movies/soon-starting`)
  }

  public getMovieById(id: String): Observable<Movie> {
    return this.http.get<Movie>(`${environment.backend}movies/specificMovie/${id}`);
  }

  public getMovieByIdAdmin(id: String): Observable<Movie> {
    return this.http.get<Movie>(`${environment.backend}movies/admin/specificMovie/${id}`);
  }

  public addMovie(movie: { title: string, summary: string, duration: number }, posterImage: any): Observable<{ idMovie: string }> {
    let imageData = this.createFormData(movie, posterImage)
    imageData.append("image", posterImage, posterImage.name);

    return this.http.post<{ idMovie: string }>(`${environment.backend}movies`, imageData);
  }

  public updateMovie(movie: { title: string, summary: string, duration: string, id: string }, posterImage: any, oldImage: string | undefined): Observable<Movie> {
    if (posterImage) {
      let imageData = this.createFormData(movie, posterImage)
      imageData.append("oldImage", movie.id + "-" + oldImage!)
      imageData.append("id", movie.id)
      imageData.append("image", posterImage, posterImage.name);
      return this.http.put<Movie>(`${environment.backend}movies/update/image`, imageData);
    } else {
      return this.http.put<Movie>(`${environment.backend}movies/update`, movie, {params: {'oldImage': oldImage!}});
    }
  }

  createFormData(movie: { title: string, summary: string, duration: string, id: string } | { title: string, summary: string, duration: number }, posterImage: any) {
    let imageData = new FormData();
    imageData.append("title", movie.title);
    imageData.append("summary", movie.summary.toString());
    imageData.append("duration", movie.duration.toString());
    return imageData
  }

  public deleteMovie(movieId: string, imageName?: string) {
    if (imageName) {
      return this.http.delete(`${environment.backend}movies/` + movieId + "/" + imageName);
    } else {
      return this.http.delete(`${environment.backend}movies/` + movieId);
    }
  }

  public getEventsWhichWillHappenInNext30Days(): Observable<{ currentDate: Date, eventAndMovie: { event: Event, movie: Movie }[] }> {
    return this.http.get<{ currentDate: Date, eventAndMovie: { event: Event, movie: Movie }[] }>(`${environment.backend}events/30daysEvents`);
  }

  public getEventWithMovieAndCinemaHallById(id: String): Observable<{ event: Event, movie: Movie, cinemaHall: CinemaHall }> {
    return this.http.get<{ event: Event, movie: Movie, cinemaHall: CinemaHall }>(`${environment.backend}events/movie-and-cinema-hall/${id}`);
  }

  public getEventWithMovieCinemaHallAndReservationById(id: String): Observable<{ event: Event, movie: Movie, cinemaHall: CinemaHall, reservationWithSeatInfoList: { id: string, userId: string, idReservationSeat: string, eventId: string, seatId: string, seatNumber: number, rowNumber: number, name: string, surname: string }[] }> {
    return this.http.get<{ event: Event, movie: Movie, cinemaHall: CinemaHall, reservationWithSeatInfoList: { id: string, userId: string, idReservationSeat: string, eventId: string, seatId: string, seatNumber: number, rowNumber: number, name: string, surname: string }[] }>(`${environment.backend}events/with-movie-cinema-hall-and-reservations/${id}`);
  }

  public getEventsByMovieId(idMovie: String): Observable<{ id: string, date: moment.Moment, price: number, cinemaHallName: string }[]> {
    return this.http.get<{ id: string, date: moment.Moment, price: number, cinemaHallName: string }[]>(`${environment.backend}events/specificEvents/?idMovie=${idMovie}`);
  }

  public getEventsByMovieIdAdmin(idMovie: String): Observable<{ id: string, date: moment.Moment, price: number, cinemaHallName: string }[]> {
    return this.http.get<{ id: string, date: moment.Moment, price: number, cinemaHallName: string }[]>(`${environment.backend}events/admin/${idMovie}`);
  }

  public getEventByIdAdmin(id: string): Observable<Event> {
    return this.http.get<Event>(`${environment.backend}events/admin/specificEvent/${id}`);
  }

  public addEvent(event: { date: moment.Moment, idMovie: string, idCinemaHall: string, price: number }): Observable<Event> {
    return this.http.post<Event>(`${environment.backend}events`, event);
  }

  public addEvents(events: { date: moment.Moment, idMovie: string, idCinemaHall: string, price: number }[]): Observable<Event> {
    return this.http.post<Event>(`${environment.backend}events/addAll`, events);
  }

  public updateEvent(event: { id: string, date: moment.Moment, price: number }) {
    return this.http.put(`${environment.backend}events/update`, event);
  }

  public updateReservation(reservationSeat: { id: string, seatId: string }) {
    return this.http.put<{ id: string, userId: string, idReservationSeat: string, eventId: string, seatId: string, seatNumber: number, rowNumber: number, name: string, surname: string }>(`${environment.backend}reservation-seats`, reservationSeat);
  }


  public deleteEvent(eventId: string) {
    return this.http.delete(`${environment.backend}events/?id=${eventId}`);
  }

  public getReservationsByEventId(idEvent: string): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${environment.backend}reservations/?idEvent=${idEvent}`);
  }

  public getReservationsByUserId(idUser: string, pageNumber: number): Observable<{ count: number, reservationsWithSeatEventMovieAndCinemaHall: { idReservation: Reservation, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[], event: Event, movie: Movie, cinemaHall: CinemaHall }[] }> {
    return this.http.get<{ count: number, reservationsWithSeatEventMovieAndCinemaHall: { idReservation: Reservation, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[], event: Event, movie: Movie, cinemaHall: CinemaHall }[] }>(`${environment.backend}reservations/my-reservations/page/${pageNumber}/${idUser}`);
  }

  public addReservation(reservation: Reservation | { userId: string | null, eventId: string, seatId: string }): Observable<{ id: string, userId: string, idReservationSeat: string, eventId: string, seatId: string, seatNumber: number, rowNumber: number, name: string, surname: string }[]> {
    return this.http.post<{ id: string, userId: string, idReservationSeat: string, eventId: string, seatId: string, seatNumber: number, rowNumber: number, name: string, surname: string }[]>(`${environment.backend}reservations`, reservation);
  }

  public addReservations(eventDate: moment.Moment, reservations: { userId: string | null, eventId: string, reservationSeats: { eventId: string, seatId: string }[] }): Observable<{ id: string, userId: string, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }> {
    return this.http.post<{ id: string, userId: string, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }>(`${environment.backend}reservations/save-all`, reservations, {
      params: {
        eventDateString: eventDate.toISOString()
      }
    });
  }

  public deleteReservation(id: string, idReservationSeat: string) {
    return this.http.delete<boolean>(`${environment.backend}reservations/admin/${id}/${idReservationSeat}`)
  }

  sendMailWithTickets(ticketInfoes: any): Observable<any> {
    return this.http.post(`${environment.backend}mail`, ticketInfoes)
  }

  public downloadPdf(ticketInfoes: any, idUser: string): Observable<any> {
    return this.http.post(`${environment.backend}pdf/export/${idUser}`, ticketInfoes)
  }

  public downloadPdfAdmin(ticketInfoes: any): Observable<any> {
    return this.http.post(`${environment.backend}pdf/admin/export`, ticketInfoes)
  }

  async createOrder(priceObj: any) {
    return await lastValueFrom(this.http.post(`${environment.backend}payment/order`, priceObj))
  }

  async captureOrder(captureRequest: { idOrder: string, idReservation: string, idUser: string, emailInfo: { userEmail: string, cinemaHallName: string, title: string, dateString: string, hourString: string, price: number, userReservationSeats: { rowNumber: number, seatNumber: number, idReservationSeat: string }[] } }) {
    return await lastValueFrom(this.http.post(`${environment.backend}payment/approve`, captureRequest))
  }

  public getUserById(id: string) {
    return this.http.get<{ id: string, name: string, surname: string }>(`${environment.backend}api/user/${id}`);
  }

  public login(user: { username: string, password: string }): Observable<{ username: string, email: string, token: string, verified: boolean, codeToVerifyEmail: string, active: boolean }> {
    return this.http.post<{ username: string, email: string, token: string, verified: boolean, codeToVerifyEmail: string, active: boolean }>(`${environment.backend}api/login`, user).pipe();
  }

  public register(registrationUser: { username: string, password: string, email: string, name: string, surname: string }): Observable<string> {
    return this.http.post<string>(`${environment.backend}api/register`, registrationUser);
  }

  public validateToken(token: String) {
    const httpHeaders = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: "Bearer " + token
      })
    };
    return this.http.get<User>(`${environment.backend}api/validate/${token}`, httpHeaders);
  }

  public verifyAccount(verificationInfo: { username: String, codeToVerifyAccount: String }): Observable<{ text: string }> {
    return this.http.put<{ text: string }>(`${environment.backend}api/verify`, verificationInfo);
  }

  public sendVerificationLink(userForVerifying: { email: String, username: String, link: String, codeToVerifyEmail: String }) {
    return this.http.post(`${environment.backend}mail/verification-email`, userForVerifying);
  }

  public changeEmailAndSendVerificationCode(userForVerifying: { email: String, username: String, link: String, codeToVerifyEmail: String }) {
    return this.http.put(`${environment.backend}api/change-email`, userForVerifying);
  }

  public findTenUsersPerPageAdmin(pageNumber: number): Observable<{ usersAmount: number, usersList: User[] }> {
    return this.http.get<{ usersAmount: number, usersList: User[] }>(`${environment.backend}api/users/page/admin/${pageNumber}`);
  }

  public findTenUsersPerPageSearchUsernameAdmin(pageNumber: number, searchUsername: string): Observable<{ usersAmount: number, usersList: User[] }> {
    return this.http.get<{ usersAmount: number, usersList: User[] }>(`${environment.backend}api/users/page/admin/${pageNumber}/${searchUsername}`);
  }

  changeVerified(id: string, username: string, verified: boolean) {
    let user = {id: id, username: username, verified: verified}
    return this.http.put(`${environment.backend}api/user/admin/verified`, user);
  }

  changeActive(id: string, username: string, active: boolean) {
    let user = {id: id, username: username, active: active}
    return this.http.put(`${environment.backend}api/user/admin/active`, user);
  }

  deleteReservationByIdAndUserId(id: string, idUser: string) {
    return this.http.delete(`${environment.backend}reservations/user/${id}/${idUser}`)
  }

  confirmReservation(id: string) {
    return this.http.put(`${environment.backend}reservations/confirm-reservation`, id)

  }

  addCinemaHall(cinemaHall: { name: string; rows: number; columns: number; seats: { seatNumber: number | null; rowNumber: number; gridPosition: number }[] }) {
    return this.http.post(`${environment.backend}cinema-halls`, cinemaHall)
  }

  findTenCinemaHallsPerPage(pageNumber: number): any {
    return this.http.get<any>(`${environment.backend}cinema-halls/admin/page/${pageNumber}`);
  }

  findTenCinemaPerPageWhichContainsSearchText(pageNumber: number, searchText: string): any {
    return this.http.get<any>(`${environment.backend}cinema-halls/admin/page/${pageNumber}/${searchText}`);
  }

  deleteCinemaHallById(id: string) {
    return this.http.delete(`${environment.backend}cinema-halls/${id}`);
  }

  getReservationByIdEventAndIdUserAndUncomplete(idUser: string, idEvent: string): Observable<{ idReservation: string, reservationTime: moment.Moment, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }> {
    return this.http.get<{ idReservation: string, reservationTime: moment.Moment, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }>(`${environment.backend}reservations/currentReservation/uncomplete/${idUser}/${idEvent}`);
  }

  getAllReservationByIdUserWhichAreUncomplete(idUser: string): Observable<{ idReservation: string, reservationTime: moment.Moment, movie: Movie, event: Event, cinemaHall: CinemaHall, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }[]> {
    return this.http.get<{ idReservation: string, reservationTime: moment.Moment, movie: Movie, event: Event, cinemaHall: CinemaHall, reservationSeats: { id: string, seatId: string, seatNumber: number, rowNumber: number }[] }[]>(`${environment.backend}reservations/user/all/uncomplete/${idUser}`);
  }

  getCountOfUncompleteReservations(idUser: string): Observable<{ countUncompleteReservations: number }> {
    return this.http.get<{ countUncompleteReservations: number }>(`${environment.backend}reservations/user/count/uncomplete/${idUser}`);
  }
}
