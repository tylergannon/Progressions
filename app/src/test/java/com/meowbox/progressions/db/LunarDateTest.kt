package com.meowbox.progressions.db

import android.content.Context
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.meowbox.fourpillars.Ephemeris
import com.meowbox.progressions.Database
import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.joda.time.LocalDate
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.assertEquals



@RunWith(RobolectricTestRunner::class)
@Config(minSdk = Build.VERSION_CODES.LOLLIPOP_MR1, maxSdk = Build.VERSION_CODES.LOLLIPOP_MR1)
class LunarDateTest {
    companion object {
        val dbName = "test.sqlite3"
    }

    var context: Context? = null

    @Test fun boopSauce() {
        assertEquals(73018, Db.instance.ephemerisPointQueries.count().executeAsOne().toInt())
    }

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

    @Test
    fun solarDateCalibration() {
        val initialDate = SolarDate(LocalDate(1900, 1, 1))
        var ephemeris = Ephemeris(File("./src/main/res/raw/ephemeris").inputStream())

        fun testDate(d: LocalDate) {
            Db.instance.ephemerisPointQueries.getBySolarDate(SolarDate(d)).executeAsOne().also { actual ->
                val expected = ephemeris.lunarDate(d)
                assertEquals(
                    expected.yearInEpoch.toInt(), actual.lunarDate.yearInEpoch,
                    "Year for ${d.year}-${d.monthOfYear}-${d.dayOfMonth} should be ${expected.yearInEpoch}"
                )
                assertEquals(
                    expected.dayOfMonth.toInt(), actual.lunarDate.dayOfMonth,
                    "Day of month for ${d.year}-${d.monthOfYear}-${d.dayOfMonth} should be ${expected.dayOfMonth}"
                )
                assertEquals(
                    expected.monthPillar, actual.lunarDate.monthPillar,
                    "MonthPillar for ${d.year}-${d.monthOfYear}-${d.dayOfMonth} should be ${expected.monthPillar}"
                )
                assertEquals(
                    expected.dayPillar, actual.dayPillar,
                    "DayPillar for ${d.year}-${d.monthOfYear}-${d.dayOfMonth} should be ${expected.dayPillar}"
                )
            }
        }


        assertEquals(0, initialDate.id)

        testDate(LocalDate(1900, 1, 1))
        testDate(LocalDate(1978, 4, 7))
    }
}