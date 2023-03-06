package hr.tvz.android.qrscan.model

import hr.tvz.android.qrscan.model.service.ReservationSeatService
import hr.tvz.android.qrscan.model.service.ServiceRetrofit
import hr.tvz.android.qrscan.view.IStartScan
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanModel : IStartScan.IScanModel {

    override fun getReservationSeatByIdAndIdEvent(listener: IStartScan.IScanModel.Finished, token: String, id: Long, idEvent: Long, ipv4: String) {
        ServiceRetrofit().createAndGetService(ReservationSeatService::class.java, ipv4).getReservationSeatByIdAndIdEvent("Bearer $token", id, idEvent)
            .enqueue(object : Callback<ReservationSeat> {
                override fun onResponse(
                    call: Call<ReservationSeat>,
                    response: Response<ReservationSeat>
                ) {
                    if (response.errorBody() != null) {
                        listener.getReservationSeatByIdFailure("Rezervacija ne postoji za ovu projekciju filma")
                        return;
                    }
                    if (response.isSuccessful) {
                        val reservationSeat: ReservationSeat = response.body()!!
                        if (reservationSeat.eventId == idEvent) {
                            listener.getReservationSeatByIdSuccess(reservationSeat)
                        } else {
                            listener.getReservationSeatByIdFailure("Rezervacija ne postoji za ovu projekciju filma")
                        }
                    } else {
                        listener.getReservationSeatByIdFailure("Rezervacija ne postoji za ovu projekciju filma")
                    }
                }

                override fun onFailure(call: Call<ReservationSeat>, t: Throwable) {
                    listener.getReservationSeatByIdFailure("Došlo je do pogreške")
                }
            })
    }
}

