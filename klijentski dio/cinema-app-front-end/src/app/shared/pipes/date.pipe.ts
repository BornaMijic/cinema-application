import {Pipe, PipeTransform} from '@angular/core';
import {Moment} from "moment";
import * as moment from "moment";

@Pipe({
  name: 'datePipe'
})
export class DatePipe implements PipeTransform {

  transform(date: Moment, pattern: string): unknown {
    return moment(date).format(pattern);
  }

}
