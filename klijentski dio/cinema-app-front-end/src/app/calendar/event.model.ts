import * as moment from "moment";

export class Event {
  date: moment.Moment;
  id: string;
  idMovie: string;
  idCinemaHall: string
  price: number;


  constructor(id: string, date: moment.Moment, idMovie: string, idCinemaHall: string, price: number) {
    this.id = id;
    this.date = date;
    this.idMovie = idMovie;
    this.idCinemaHall = idCinemaHall;
    this.price = price;
  }
}
