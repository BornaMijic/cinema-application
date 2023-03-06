package hr.tvz.android.qrscan.model

import hr.tvz.android.qrscan.model.service.EventsService
import hr.tvz.android.qrscan.model.service.ServiceRetrofit
import hr.tvz.android.qrscan.view.IEvents
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsList: IEvents.IEventsList {
    override fun getEventByMovieId(listener: IEvents.IEventsList.Finished, token: String, movieId: Int, ipv4: String) {
        ServiceRetrofit().createAndGetService(EventsService::class.java, ipv4).getEventsByMovieId("Bearer $token", movieId).enqueue(object : Callback<MutableList<Event>> {
            override fun onResponse(call: Call<MutableList<Event>>, response: Response<MutableList<Event>>) {
                if(response.isSuccessful) {
                    val events: MutableList<Event> = response.body()!!
                    if(events.isEmpty()) {
                        listener.setEventsFailure("Nema projekcija")
                    } else {
                        listener.setEventsSuccess(response.body()!!)
                    }
                } else {
                    listener.setEventsFailure("Nismo mogli očitati projekcije")
                }

            }

            override fun onFailure(call: Call<MutableList<Event>>, t: Throwable) {
                listener.setEventsFailure("Pogreška")
            }
        })
    }
}