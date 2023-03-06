package hr.tvz.android.qrscan.view

import hr.tvz.android.qrscan.model.Event

interface IEvents {
    interface IEventsActivity {
        fun setEventsByMovieId(events: MutableList<Event>)
        fun setFailure(errorMessage: String)
    }

    interface IEventsPresenter {
        fun onStart(token: String, idMovie: Int, ipv4: String)
    }

    interface IEventsList{
        interface Finished {
            fun setEventsSuccess(events: MutableList<Event>)
            fun setEventsFailure(errorMessage: String)
        }

        fun getEventByMovieId(listener: Finished, token: String, movieId: Int, ipv4: String)
    }
}