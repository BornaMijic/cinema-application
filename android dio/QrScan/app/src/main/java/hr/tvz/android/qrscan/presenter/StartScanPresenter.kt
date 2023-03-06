package hr.tvz.android.qrscan.presenter

import hr.tvz.android.qrscan.model.*
import hr.tvz.android.qrscan.view.IStartScan

class StartScanPresenter(private var iStartScanActivity: IStartScan.IStartScanActivity, private var scanModel: ScanModel): IStartScan.IStartScanPresenter, IStartScan.IScanModel.Finished{

    override fun getReservationSeatByIdSuccess(reservationSeat: ReservationSeat) {
        iStartScanActivity.setReservationSeat(reservationSeat)
    }

    override fun getReservationSeatByIdFailure(message: String) {
        iStartScanActivity.setNotFound(message)
    }

    override fun checkReservation(token: String, id: Long, idEvent: Long, ipv4: String) {
        scanModel.getReservationSeatByIdAndIdEvent(this, token, id, idEvent, ipv4)
    }

}