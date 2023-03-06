package hr.tvz.android.qrscan.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanActivity: AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var scannerView: ZXingScannerView;
    private var eventId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        if(intent.hasExtra("selectedEventId")) {
            eventId = intent.getStringExtra("selectedEventId")!!
        }
        setContentView(scannerView)
    }

    override fun handleResult(result: Result) {
        val intent = Intent(applicationContext, StartScanActivity::class.java)
        intent.putExtra("scanResultAndSelectedEventId", arrayOf(result.toString(),eventId))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }



}