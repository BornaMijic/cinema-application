package hr.tvz.android.qrscan.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import hr.tvz.android.qrscan.R
import hr.tvz.android.qrscan.model.Event
import hr.tvz.android.qrscan.view.StartScanActivity
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class EventsAdapter(context: Context, resource: Int, events: MutableList<Event>) :
    ArrayAdapter<Event>(
        context,
        resource,
        events
    ) {

    private val eventsResource: Int = resource

    private class EventViewItem (
        val cinemaHallNameView: TextView,
        val dateView: TextView,
        val timeView: TextView
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val cinemaHallName = getItem(position)?.cinemaHallName
        val date = getItem(position)?.date?.replace("T", " ")

        if(convertView != null) {
            return convertView
        } else {
            val view = LayoutInflater.from(context).inflate(eventsResource, parent, false)

            val eventViewItem = EventViewItem(view.findViewById(R.id.cinema_hall_name) as TextView, view.findViewById(R.id.date_text) as TextView, view.findViewById(R.id.time_text) as TextView)

            eventViewItem.cinemaHallNameView.text = cinemaHallName


            try {
                val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val hourPattern = DateTimeFormatter.ofPattern("HH:mm:ss")
                val zoneId: ZoneId = ZoneId.of("UTC");
                val zoneDateTimeUtc: ZonedDateTime = LocalDateTime.parse(date , pattern).atZone(zoneId)
                val zoneDateTimeLocal: ZonedDateTime = zoneDateTimeUtc.withZoneSameInstant(ZoneId.systemDefault())
                eventViewItem.dateView.text = zoneDateTimeLocal.format(datePattern)
                eventViewItem.timeView.text = zoneDateTimeLocal.format(hourPattern)

            } catch (exception: Exception) {
                eventViewItem.dateView.text = "Nepoznati datum"
                eventViewItem.timeView.text = "Nepoznati sati"
            }

            view.setOnClickListener {
                val intent = Intent(context, StartScanActivity::class.java)
                intent.putExtra("selectedEventId", getItem(position)?.id.toString())
                context.startActivity(intent)
            }

            return view
        }
    }
}