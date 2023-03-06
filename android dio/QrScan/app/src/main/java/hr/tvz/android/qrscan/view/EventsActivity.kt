package hr.tvz.android.qrscan.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import hr.tvz.android.qrscan.R
import hr.tvz.android.qrscan.databinding.ActivityEventsBinding
import hr.tvz.android.qrscan.model.Event
import hr.tvz.android.qrscan.model.EventsList
import hr.tvz.android.qrscan.presenter.EventsPresenter
import hr.tvz.android.qrscan.view.adapter.EventsAdapter


class EventsActivity : AppCompatActivity(), IEvents.IEventsActivity {

    private lateinit var binding: ActivityEventsBinding
    lateinit var presenter: EventsPresenter
    private var token = ""
    private var ipv4 = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ipv4 = getSharedPreferences("ipAddress", AppCompatActivity.MODE_PRIVATE).getString("ipAddress", "")!!

        token = getSharedPreferences("tokenPreference", AppCompatActivity.MODE_PRIVATE).getString("token", "")!!

        presenter = EventsPresenter(this, EventsList())

        val extras = intent.extras
        if (extras != null) {
            val movieId = extras.getString("selectedMovieId")?.toInt()
            if (movieId != null) {
                presenter.onStart(token, movieId, ipv4)
            }
        }

    }

    override fun setEventsByMovieId(events: MutableList<Event>) {
        binding.errorMessage.text = ""
        binding.errorMessage.visibility = View.GONE
        binding.list.visibility = View.VISIBLE
        val adapter = EventsAdapter(this, R.layout.event_item, events as ArrayList<Event>)
        binding.list.adapter = adapter
    }


    override fun setFailure(errorMessage: String) {
        binding.errorMessage.visibility = View.VISIBLE
        binding.list.visibility = View.GONE
        binding.errorMessage.text = errorMessage
    }


}