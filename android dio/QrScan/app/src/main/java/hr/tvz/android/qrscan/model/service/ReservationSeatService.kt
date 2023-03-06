package hr.tvz.android.qrscan.model.service

import hr.tvz.android.qrscan.model.ReservationSeat
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ReservationSeatService {
    @GET("/reservation-seats/getSpecificReservationSeat/{id}/{idEvent}")
    fun getReservationSeatByIdAndIdEvent(@Header("Authorization") token: String, @Path("id") id: Long, @Path("idEvent") idEvent: Long): retrofit2.Call<ReservationSeat>
}