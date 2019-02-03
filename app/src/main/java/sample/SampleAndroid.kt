package sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.meowbox.fourpillars.Ephemeris
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.LocalDateTime

actual class Sample {
    actual fun checkMe() = 44
}

actual object Platform {
    actual val name: String = "Android"
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hello()
        Sample().checkMe()
        setContentView(R.layout.activity_main)
        val ephemeris = Ephemeris(applicationContext)
        dorthBurgle.text = ephemeris.chart(LocalDateTime(1978, 4, 7, 15, 30)).dayPillar.branch.name
    }
}