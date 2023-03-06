import {Injectable} from '@angular/core';
import {DataStorageService} from "./data-storage.service";

@Injectable({
  providedIn: 'root'
})
export class EmailService {

  constructor(private dataStorageService: DataStorageService) {
  }

  sendMailWithTickets(ticketInfoes: any) {
    return this.dataStorageService.sendMailWithTickets(ticketInfoes);
  }
}
