import {Seat} from "./seat.model";

export class CinemaHall {
  id: string;
  name: string;
  gridRowsNumber: number;
  gridColumnsNumber: number;
  seats: Seat[];

  constructor(id: string, name: string, rows: number, gridColumnsNumber: number, seats: Seat[]) {
    this.id = id;
    this.name = name;
    this.gridRowsNumber = rows;
    this.gridColumnsNumber = gridColumnsNumber;
    this.seats = seats;
  }

}
