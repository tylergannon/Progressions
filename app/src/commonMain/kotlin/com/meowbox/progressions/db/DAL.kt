package com.meowbox.progressions.db

import com.meowbox.fourpillars.Branch
import com.meowbox.fourpillars.Pillar
import com.meowbox.fourpillars.Stem
import com.meowbox.progressions.ChartRecord
import com.meowbox.progressions.Database
import com.meowbox.progressions.EphemerisPoint
import com.meowbox.progressions.StarComment
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import kotlinx.serialization.stringify

inline class SolarDate(val id: Int)

inline class ChartRecordId(val id: Int)

private fun Int.pow(p: Int) : Int =
    if (p == 1)
        this
    else
        this * this.pow(p - 1)

/*********************************************************
 * Bits 00..07 (8) : 0       : Year value 0..199 in years from 1900..2099
 * Bits 08..11 (4) : 256     : Month value 1..12
 * Bits 12..16 (5) : 4096    : Day of month 1..31
 * Bits 17..21 (5) : 131072  : Hour of day 0..23
 * Bits 22..27 (6) : 4194304 : Minute of hour 0..59
 *
 *
 *
 */
inline class DateTime(val id: Int) {
    constructor(year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int, minuteOfHour: Int)
            : this((year - firstYear) +
            monthOfYear * monthMultiplier +
            dayOfMonth * dayMultiplier +
            hourOfDay * hourMultiplier +
            minuteOfHour * minuteMultiplier)

    val year: Int get() = firstYear + id.rem(monthMultiplier)
    val monthOfYear: Int get() = id.rem(dayMultiplier) / monthMultiplier
    val dayOfMonth: Int get() = id.rem(hourMultiplier) / dayMultiplier
    val hourOfDay: Int get() = id.rem(minuteMultiplier) / hourMultiplier
    val minuteOfHour: Int get() = id / minuteMultiplier

    companion object {
        private const val firstYear = 1900
        private const val yearBits = 8
        private const val monthBits = 4
        private const val dayBits = 5
        private const val hourBits = 5
        private const val yearMultiplier = 1
        private val monthMultiplier = yearMultiplier * 2.pow(yearBits)
        private val dayMultiplier = monthMultiplier * 2.pow(monthBits)
        private val hourMultiplier = dayMultiplier * 2.pow(dayBits)
        private val minuteMultiplier = hourMultiplier * 2.pow(hourBits)
    }
}

inline class LunarDate(val id: Short) {

    val dayOfMonth: Int
        get() = id / dayOfMonthBit

    val yearPillar: Pillar get() = Pillar.all[yearInEpoch - 1]

    val monthPillar: Pillar
        get() = Pillar(
            Stem.all[(2 * yearInEpoch + monthNumber - 1) % 10],
            Branch.all[(monthNumber + 1) % 12]
        )

    val monthNumber: Int
        get() = id.rem(dayOfMonthBit) / monthNumberBit


    val yearInEpoch: Int
        get() = id.rem(monthNumberBit)

    companion object {
        private const val monthNumberBit: Short = 64
        private const val dayOfMonthBit: Short = 1024
        private const val initialDayPillarNum = 10
    }
}

@UseExperimental(ImplicitReflectionSerializer::class)
inline fun <reified T : Any> serializedColumnAdapter() = object : ColumnAdapter<T, String> {
    override fun encode(value: T) = Json.stringify(value)
    override fun decode(databaseValue: String) = Json.parse<T>(databaseValue)
}


fun createQueryWrapper(driver: SqlDriver) = Database(
    driver = driver,
    starCommentAdapter = StarComment.Adapter(
        starAdapter = EnumColumnAdapter(),
        palaceAdapter = EnumColumnAdapter(),
        auspicesAdapter = EnumColumnAdapter(),
        branchAdapter = serializedColumnAdapter(),
        inHouseWithAdapter = serializedColumnAdapter()
    ),
    ephemerisPointAdapter = EphemerisPoint.Adapter(
        solarDateAdapter = object : ColumnAdapter<SolarDate, Long> {
            override fun encode(value: SolarDate): Long = value.id.toLong()
            override fun decode(databaseValue: Long) = SolarDate(databaseValue.toInt())
        },
        lunarDateAdapter = object : ColumnAdapter<LunarDate, Long> {
            override fun encode(value: LunarDate): Long = value.id.toLong()
            override fun decode(databaseValue: Long) = LunarDate(databaseValue.toShort())
        }
    ),
    chartRecordAdapter = ChartRecord.Adapter(
        idAdapter = object : ColumnAdapter<ChartRecordId, Long> {
            override fun decode(databaseValue: Long) = ChartRecordId(databaseValue.toInt())
            override fun encode(value: ChartRecordId) = value.id.toLong()
        },
        ephemerisPointIdAdapter = object : ColumnAdapter<SolarDate, Long> {
            override fun encode(value: SolarDate): Long = value.id.toLong()
            override fun decode(databaseValue: Long) = SolarDate(databaseValue.toInt())
        },
        dobAdapter = object : ColumnAdapter<DateTime, Long> {
            override fun decode(databaseValue: Long) = DateTime(databaseValue.toInt())
            override fun encode(value: DateTime) = value.id.toLong()
        },
        yearBranchAdapter = EnumColumnAdapter(),
        yearStemAdapter = EnumColumnAdapter(),
        hourBranchAdapter = EnumColumnAdapter(),
        hourStemAdapter = EnumColumnAdapter(),
        mingAdapter = EnumColumnAdapter(),
        ziWeiAdapter = EnumColumnAdapter(),
        tianFuAdapter = EnumColumnAdapter(),
        roddenRatingAdapter = EnumColumnAdapter()
    )
)
//
//fun Database.loadEphemerisPoints(bytes: List<Byte>) = bytes.chunked(4)
//    .mapIndexed { index, chunk ->
//        Pair(SolarDate(index), LunarDate((chunk[1] + chunk[2] * 64 + chunk[3] * 1024).toShort()))
//    }
//    .forEach {
//        ephemerisPointQueries.insertItem(it.first, it.second)
//    }
//


object Db {
    private var driverRef: SqlDriver? = null
    private var dbRef: Database? = null

    val ready: Boolean get() = dbRef != null
    val instance: Database get() = dbRef!!

    internal fun dbClear() {
        if (driverRef != null)
            driverRef!!.close()

        dbRef = null
        driverRef = null
    }

    fun dbSetup(driver: SqlDriver) {
        val db = createQueryWrapper(driver)
        driverRef = driver
        dbRef = db
    }
}

//expect fun Db.loadChart(dob: DateTime) : Chart
