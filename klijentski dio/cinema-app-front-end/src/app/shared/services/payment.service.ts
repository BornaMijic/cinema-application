import {Injectable} from '@angular/core';
import {DataStorageService} from "./data-storage.service";

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  constructor(private dataStorageService: DataStorageService) {
  }

  async createOrder(priceObj: any) {
    return await this.dataStorageService.createOrder(priceObj)
  }

  async captureOrder(captureRequest: { idOrder: string, idReservation: string, idUser: string, emailInfo: { userEmail: string, cinemaHallName: string, title: string, dateString: string, hourString: string, price: number, userReservationSeats: { rowNumber: number, seatNumber: number, idReservationSeat: string }[] } }) {
    return await this.dataStorageService.captureOrder(captureRequest)
  }
}
