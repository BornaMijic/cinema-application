package hr.tvz.android.qrscan.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import hr.tvz.android.qrscan.R
import hr.tvz.android.qrscan.databinding.ActivityStartScanBinding
import hr.tvz.android.qrscan.model.ReservationSeat
import hr.tvz.android.qrscan.model.ScanModel
import hr.tvz.android.qrscan.presenter.StartScanPresenter


class StartScanActivity : AppCompatActivity(), IStartScan.IStartScanActivity {

    private lateinit var binding: ActivityStartScanBinding
    lateinit var presenter: StartScanPresenter
    private var token = ""
    private var ipv4 = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityStartScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.reservationError.visibility = View.GONE
        binding.rowNumber.visibility = View.GONE
        binding.seatNumber.visibility = View.GONE
        binding.imageIsFound.visibility = View.GONE
        binding.permissionError.visibility = View.GONE

        ipv4 = getSharedPreferences("ipAddress", AppCompatActivity.MODE_PRIVATE).getString("ipAddress", "")!!

        token = getSharedPreferences("tokenPreference", AppCompatActivity.MODE_PRIVATE).getString("token", "")!!

        presenter = StartScanPresenter(this, ScanModel())

        if(intent.hasExtra("scanResultAndSelectedEventId")) {
            val scanResultAndSelectedEventId = intent.getStringArrayExtra("scanResultAndSelectedEventId")
            val reservationId = scanResultAndSelectedEventId?.get(0)?.toLong()
            val eventId = scanResultAndSelectedEventId?.get(1)?.toLong()
            if (reservationId != null && eventId != null && eventId != 0L) {
                presenter.checkReservation(token, reservationId, eventId, ipv4)
            }
            binding.scan.setOnClickListener {

                val intent = Intent(this, ScanActivity::class.java)
                intent.putExtra("selectedEventId", eventId)
                startActivity(intent)
            }

        } else {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
            }
            val extras = intent.extras
            val eventId = extras?.getString("selectedEventId") as String


            binding.scan.setOnClickListener {

                val intent = Intent(this, ScanActivity::class.java)
                intent.putExtra("selectedEventId", eventId)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setReservationSeat(reservationSeat: ReservationSeat) {
        binding.reservationError.visibility = View.VISIBLE
        binding.imageIsFound.visibility = View.VISIBLE
        Picasso.get()
            .load(R.drawable.found)
            .into(binding.imageIsFound)
        binding.scan.text = "Ponovi"
        binding.rowNumber.text = "Broj reda: " + reservationSeat.rowNumber
        binding.seatNumber.text = "Broj stolice: " + reservationSeat.seatNumber
        foundVisibility()

    }

    override fun setNotFound(notFoundMessage: String) {
        binding.imageIsFound.visibility = View.VISIBLE
        Picasso.get()
            .load(R.drawable.not_found)
            .into(binding.imageIsFound)
        binding.scan.text = "Ponovi"
        binding.reservationError.text = notFoundMessage
        errorVisibility()
    }

    private fun errorVisibility() {
        binding.rowNumber.visibility = View.GONE
        binding.seatNumber.visibility = View.GONE
        binding.reservationError.visibility = View.VISIBLE
    }

    private fun foundVisibility() {
        binding.rowNumber.visibility = View.VISIBLE
        binding.seatNumber.visibility = View.VISIBLE
        binding.reservationError.visibility = View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 100) {
            binding.scan.visibility = View.VISIBLE
            binding.permissionError.visibility = View.GONE
        } else {
            binding.scan.visibility = View.GONE
            binding.rowNumber.visibility = View.GONE
            binding.seatNumber.visibility = View.GONE
            binding.reservationError.visibility = View.GONE
            binding.permissionError.visibility = View.VISIBLE
        }
    }
}


