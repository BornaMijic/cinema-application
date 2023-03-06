package hr.tvz.android.qrscan.view

import hr.tvz.android.qrscan.model.ReservationSeat

interface IStartScan {
    interface IStartScanActivity {
        fun setReservationSeat(reservationSeat: ReservationSeat)
        fun setNotFound(notFoundMessage: String)
    }

    interface IStartScanPresenter {
        fun checkReservation(token: String, id: Long, idEvent: Long, url: String)
    }

    interface IScanModel {
        interface Finished {
            fun getReservationSeatByIdSuccess(reservationSeat: ReservationSeat)
            fun getReservationSeatByIdFailure(message: String)
        }

        fun getReservationSeatByIdAndIdEvent(listener: Finished, token: String, id: Long, idEvent: Long, url: String)
    }
}