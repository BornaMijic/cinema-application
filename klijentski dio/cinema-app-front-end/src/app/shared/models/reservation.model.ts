export class Reservation {
  id: string;
  seatNumber?: number;
  userId: string;
  eventId: string;
  rowNumber?: number;
  seatId?: string


  constructor(id: string, userId: string, eventId: string,seatId?: string, seatNumber?: number, rowNumber?: number) {
    this.id = id;
    if(seatNumber) {
      this.seatNumber = seatNumber;
    }
    this.userId = userId;
    this.eventId = eventId;
    if(rowNumber) {
      this.rowNumber = rowNumber;
    }
    if(seatId) {
      this.seatId = seatId;
    }
  }
}
