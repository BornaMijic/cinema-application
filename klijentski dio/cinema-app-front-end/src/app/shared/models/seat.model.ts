export class Seat {
  id: string;
  seatNumber: number;
  rowNumber: number;
  gridPosition: number;
  exist: boolean;
  cinemaHallId: string;


  constructor(id: string, seatNumber: number, rowNumber: number, gridPosition: number, exist: boolean, cinemaHallId: string) {
    this.id = id;
    this.seatNumber = seatNumber;
    this.rowNumber = rowNumber;
    this.gridPosition = gridPosition;
    this.exist = exist;
    this.cinemaHallId = cinemaHallId;
  }
}
