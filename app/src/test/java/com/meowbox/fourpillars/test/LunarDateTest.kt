package com.meowbox.fourpillars.test

//import android.content.Context
//import android.support.test.InstrumentationRegistry
import com.meowbox.fourpillars.Branch
import com.meowbox.fourpillars.Ephemeris
import com.meowbox.fourpillars.Pillar
import com.meowbox.fourpillars.Stem
import com.meowbox.progressions.db.Db
import org.joda.time.LocalDate
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.zip.ZipFile
import kotlin.test.assertEquals

class LunarDateTest {
    var ephemeris: Ephemeris? = null

//    var context: Context? = null

    @Before
    fun setupPooPoo() {
//        context.resources.openRawResource()
        ephemeris = File("./src/main/res/raw/ephemeris").inputStream().let {
            Ephemeris(it)
        }

        File("./src/main/res/raw/ephemeris").inputStream().readBytes().toList().apply {
            Db
        }

    }


//    @Test
//    fun `Can make chart for 1900-01-16T0800`() {
//        val chart = ephemeris!!.chart(LocalDateTime(1900, 2, 19, 8, 0))
////        val chart2 = Chart.forDateTime(LocalDateTime(1900,1, 16, 8, 0))
//    }
//
//    @Test
//    fun verifyPillarsFor_April_07_1978() {
//        val myBirthday = ephemeris!!.lunarDate(LocalDate(1978, 4, 7))
//        assertEquals(3, myBirthday.monthNumber)
//        assertEquals(55, myBirthday.yearInEpoch)
//        assertEquals(Pillar(Stem.YangEarth, Branch.Horse), myBirthday.yearPillar)
//        assertEquals(Pillar(Stem.YangFire, Branch.Dragon), myBirthday.monthPillar)
//        assertEquals(Pillar(Stem.YangWater, Branch.Monkey), myBirthday.hourPillar(Branch.Monkey))
//    }

    @Test
    fun verifyPillarsFor_Jun_18_1982() {
        val vanessaBirthday = ephemeris!!.lunarDate(LocalDate(1982, 6, 18))
        assertEquals(4, vanessaBirthday.monthNumber)
        assertEquals(27, vanessaBirthday.dayOfMonth)
        assertEquals(59, vanessaBirthday.yearInEpoch)
        assertEquals(Pillar(Stem.YangWater, Branch.Dog), vanessaBirthday.yearPillar)
        assertEquals(Pillar(Stem.YinWood, Branch.Snake), vanessaBirthday.monthPillar)
        assertEquals(Pillar(Stem.YangWater, Branch.Monkey), vanessaBirthday.dayPillar)
        assertEquals(Pillar(Stem.YangEarth, Branch.Monkey), vanessaBirthday.hourPillar(Branch.Monkey))

    }

//    @Test
//    fun checkBirthday() {
//        val myBirthday = org.joda.time.LocalDateTime(1978, 4, 7, 15, 30)
//        val chart = ephemeris!!.chart(myBirthday)
//        assertEquals(Pillar(Stem.YangEarth, Branch.Horse), chart.yearPillar)
//        assertEquals(Pillar(Stem.YangFire, Branch.Dragon), chart.monthPillar)
//        assertEquals(Pillar(Stem.YinEarth, Branch.Pig), chart.dayPillar)
//        assertEquals(Pillar(Stem.YangWater, Branch.Monkey), chart.hourPillar)
//    }
}

