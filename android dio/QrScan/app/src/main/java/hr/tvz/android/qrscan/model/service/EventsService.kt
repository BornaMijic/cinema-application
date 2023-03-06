package hr.tvz.android.qrscan.model.service

import hr.tvz.android.qrscan.model.Event
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface EventsService {
    @GET("/events/active/")
    fun getEventsByMovieId(@Header("Authorization") token: String, @Query("idMovie") idMovie: Int): retrofit2.Call<MutableList<Event>>
}