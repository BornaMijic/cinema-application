package hr.tvz.android.qrscan.presenter

import hr.tvz.android.qrscan.model.Event
import hr.tvz.android.qrscan.model.EventsList
import hr.tvz.android.qrscan.view.IEvents

class EventsPresenter(private var iEventsActivity: IEvents.IEventsActivity, private var eventsList: EventsList): IEvents.IEventsPresenter, IEvents.IEventsList.Finished {

    override fun setEventsSuccess(events: MutableList<Event>) {
        iEventsActivity.setEventsByMovieId(events)
    }

    override fun setEventsFailure(errorMessage: String) {
        iEventsActivity.setFailure(errorMessage)
    }

    override fun onStart(token: String, idMovie: Int, ipv4: String) {
        eventsList.getEventByMovieId(this, token, idMovie, ipv4)
    }
}