package com.meowbox.progressions.db

import android.content.Context
import com.meowbox.fourpillars.Branch
import com.meowbox.fourpillars.Pillar
import com.meowbox.fourpillars.Stem
import com.meowbox.progressions.EphemerisPoint
import com.meowbox.progressions.R
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import java.io.File
import java.util.zip.ZipInputStream

val EphemerisPoint.dayPillarNum: Int get() = (solarDate.id + 10).rem(60)
val EphemerisPoint.dayPillar: Pillar get() = Pillar.num(dayPillarNum)

fun EphemerisPoint.hourPillar(branch: Branch) =
    Pillar(Stem.all[(2 * dayPillar.ordinal + branch.ordinal) % 10], branch)

fun SolarDate(date: LocalDate) = SolarDate(Days.daysBetween(startDate, date).days)
fun SolarDate.toLocalDate() = startDate.plusDays(id)

private val startDate = LocalDate(1900, 1, 1)
private const val initialDayPillarNum = 10


private fun File.createRecursive(block: ((File) -> Unit)? = null) {
    if (!parentFile.exists())
        parentFile.mkdirs()

    if (createNewFile() && block != null)
        block(this)
}


fun loadDatabase(context: Context, dbName: String) =
    File(context.getDatabasePath(dbName).toURI()).createRecursive { file ->
        ZipInputStream(context.resources.openRawResource(R.raw.ephemerisdb)).also { zip ->
            zip.nextEntry
            var len: Int
            val buffer = ByteArray(10000)
            val out = file.outputStream()
            while (true) {
                len = zip.read(buffer)
                if (len == -1)
                    break
                out.write(buffer, 0, len)
            }
        }
    }


fun DateTime.toLocalDateTime() = LocalDateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour)

//actual fun Db.loadChart(dob: DateTime): Chart {
//    val adjustedDob = dob.toLocalDateTime().plusHours(1)
//    val ephemerisPoint = instance.ephemerisPointQueries.getBySolarDate(
//        SolarDate(adjustedDob.toLocalDate())
//    ).executeAsOne()
//    return Chart(ephemerisPoint.lunarDate.yearPillar, ephemerisPoint.lunarDate.monthPillar,
//        ephemerisPoint.dayPillar, ephemerisPoint.hourPillar(Branch.num(adjustedDob.hourOfDay / 2)),
//        ephemerisPoint.lunarDate.dayOfMonth)
//}
