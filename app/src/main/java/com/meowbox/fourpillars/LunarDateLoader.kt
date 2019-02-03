package com.meowbox.fourpillars

import android.content.Context
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import sample.R
import java.io.InputStream

/*****************************************
 *
 * An Array<LunarDate>, for each date from Jan 1, 1900,
 * To Dec 31, 2099.
 *
 * They are read from the coded ephemeris data.  Every four bytes
 * in the ephemeris represents one date.
 *
 * Byte 0: Epoch number (not used)
 * Byte 1: Year in epoch (0..59)
 * Byte 2: Month number (0..12)
 * Byte 3: Day of lunar month (1..30)
 *
 ****************************************/


actual class Ephemeris(private val dates: Array<Short>) {
    constructor (inputStream: InputStream) : this(inputStream.readBytes()
        .toList()
        .chunked(4)
        .map {
            EphemerisRecord(it[1], it[2], it[3]).data
        }.toTypedArray()
    )

    constructor(context: Context) : this(context.resources.openRawResource(R.raw.ephemeris))

    fun lunarDate(date: LocalDate): LunarDate = Days.daysBetween(startDate, date).days.let { i ->
        dates[i].let { data ->
            LunarDate(data.yearInEpoch, data.monthNumber, data.dayOfMonth, ((initialDayPillarNum + i) % 60).toByte())
        }
    }

    actual fun lunarDate(
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ): LunarDate = lunarDate(LocalDate(year, monthOfYear, dayOfMonth))

    actual fun chart(
        lunarDate: LunarDate,
        hourOfDay: Int,
        minuteOfHour: Int
    ): Chart {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun chart(dateTime: LocalDateTime) =
        dateTime.plusHours(1).let {
            Chart.forLunarDateAndTime(this.lunarDate(it.toLocalDate()), it.toLocalTime())
        }


    internal class EphemerisRecord(val data: Short) {

        constructor(yearInEpoch: Byte, monthNumber: Byte, dayOfMonth: Byte) :
                this((yearInEpoch + monthNumber * monthNumberBit + dayOfMonth * dayOfMonthBit).toShort())

        fun toLunarDate(pillarNumber: Byte) =
            LunarDate(yearInEpoch, monthNumber, dayOfMonth, ((pillarNumber) % 60).toByte())

        private val dayOfMonth: Byte
            get() {
                return (data / dayOfMonthBit).toByte()
            }

        private val monthNumber: Byte
            get() {
                return (data.rem(dayOfMonthBit) / monthNumberBit).toByte()
            }

        private val yearInEpoch: Byte
            get() {
                return data.rem(monthNumberBit).toByte()
            }

        companion object {
            private const val monthNumberBit: Short = 64
            private const val dayOfMonthBit: Short = 1024
        }

    }

    private val monthNumberBit: Short = 64
    private val dayOfMonthBit: Short = 1024

    private val Short.dayOfMonth: Byte
        get() {
            return (this / dayOfMonthBit).toByte()
        }

    private val Short.monthNumber: Byte
        get() {
            return (this.rem(dayOfMonthBit) / monthNumberBit).toByte()
        }

    private val Short.yearInEpoch: Byte
        get() {
            return this.rem(monthNumberBit).toByte()
        }

    private fun Short.Companion.fromLunarDate(yearInEpoch: Short, monthNumber: Short, dayOfMonth: Short): Short =
        (yearInEpoch + monthNumber * monthNumberBit + dayOfMonth * dayOfMonthBit).toShort()


    private fun Chart.Companion.forLunarDateAndTime(lunarDate: LunarDate, time: LocalTime): Chart = Chart(
        lunarDate.yearPillar,
        lunarDate.monthPillar,
        lunarDate.dayPillar,
        lunarDate.hourPillar(Branch.num(time.hourOfDay / 2)),
        lunarDate.dayOfMonth.toInt()
    )

    companion object {
        private val startDate = LocalDate(1900, 1, 1)
        private const val initialDayPillarNum = 10
    }
}

//actual val starCommentData: List<StarComment> =
//    StarComment::class.java.getResource("/starData.json").readText().let { data ->
//        JSON.parse(StarComment.serializer().list, data)
//    }
