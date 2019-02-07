package sample

import android.content.Context
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.meowbox.fourpillars.*
import com.meowbox.progressions.Database
import com.meowbox.progressions.datastore.RoddenRating
import com.meowbox.progressions.db.*
import com.meowbox.progressions.hello
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.serialization.json.json
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.joda.time.LocalDate
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(minSdk = Build.VERSION_CODES.LOLLIPOP_MR1, maxSdk = Build.VERSION_CODES.LOLLIPOP_MR1)
class SampleTestsAndroid {
    companion object {
        val dbName = "test.sqlite3"
    }

    var context: Context? = null

    @Before
    fun setup() {
        println("Setup database")
        context = InstrumentationRegistry.getInstrumentation().context
        loadDatabase(context!!, dbName)
        Db.dbSetup(AndroidSqliteDriver(Database.Schema, context!!, dbName))
    }

    @After
    fun teardown() {
        Db.dbClear()
    }

    private fun Chart.whichHouse(star: Star) = houses.filter { it.value.contains(star) }.entries.first().key.palace

    @Test
    fun importShit() {

        FileReader(File("/Users/tyler/src/DB Backup/scraper.chartRecord.csv")).use { reader ->
            BufferedReader(reader).use { buffered ->
                CSVParser(buffered, CSVFormat.DEFAULT.withFirstRecordAsHeader()).use { parser ->
                    for (record in parser.records) {
                        val solarDate = SolarDate(LocalDate(record["year"].toInt(), record["monthOfYear"].toInt(), record["dayOfMonth"].toInt()))
                        val dob = DateTime(record["year"].toInt(), record["monthOfYear"].toInt(), record["dayOfMonth"].toInt(), record["hourOfDay"].toInt(), record["minuteOfHour"].toInt())

                        Db.instance.chartRecordQueries.insertTheirs(
                            ephemerisPointId = solarDate,
                            name = record["name"],
                            dob = dob,
                            isDst = (record["dst"] == "true"),
                            yearBranch = Branch.valueOf(record["yearBranch"]),
                            yearStem = Stem.valueOf(record["yearStem"]),
                            hourBranch = Branch.valueOf(record["hourBranch"]),
                            hourStem = Stem.valueOf(record["hourStem"]),
                            ming = Branch.valueOf(record["ming"]),
                            ziWei = Palace.valueOf(record["ziWei"]),
                            tianFu = Palace.valueOf(record["tianFu"]),
                            url = record["url"],
                            roddenRating = RoddenRating.valueOf(record["roddenRating"])
                        )
                    }
                }
            }
        }

        Db.dbClear()
        File(context!!.getDatabasePath(dbName).toURI()).copyTo(File("/Users/tyler/db.sqlite3"), overwrite = true)
    }
}