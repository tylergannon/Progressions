package com.meowbox.progressions.db

import com.meowbox.DateTime
import com.meowbox.fourpillars.*
import com.meowbox.progressions.ChartRecord
import com.meowbox.progressions.Database
import com.meowbox.progressions.EphemerisPoint
import com.meowbox.progressions.StarComment
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.serialization.*
import kotlinx.serialization.internal.EnumSerializer
import kotlinx.serialization.json.Json

inline class SolarDate(val id: Int)

inline class ChartRecordId(val id: Int)

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

private val branchListSerializer = EnumSerializer(Branch::class).list
private val starSetListSerializer = EnumSerializer(Star::class).set.list


fun createQueryWrapper(driver: SqlDriver) = Database(
    driver = driver,
    starCommentAdapter = StarComment.Adapter(
        starAdapter = EnumColumnAdapter(),
        palaceAdapter = EnumColumnAdapter(),
        auspicesAdapter = EnumColumnAdapter(),
        branchAdapter = object : ColumnAdapter<List<Branch>, String> {
            override fun encode(value: List<Branch>) = Json.stringify(branchListSerializer, value)
            override fun decode(databaseValue: String) =
                println(databaseValue).let { Json.parse(branchListSerializer, databaseValue) }
        },
        inHouseWithAdapter = object : ColumnAdapter<List<Set<Star>>, String> {
            override fun encode(value: List<Set<Star>>) = Json.stringify(starSetListSerializer, value)
            override fun decode(databaseValue: String) =
                println(databaseValue).let { Json.parse(starSetListSerializer, databaseValue) }
        }
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

expect fun loadChart(dob: DateTime): Chart

object ChartData {
    fun loadMyChartRecords() = Db.instance.chartRecordQueries.mine().executeAsList()
    fun searchChartRecords(search: String) = Db.instance.chartRecordQueries.search("%$search%").executeAsList()
//    fun createChartRecord(name: String, dob: DateTime, yearPillar: Pillar, monthPillar: Pillar, dayPillar: Pillar, hourPillar: Pillar) =
//            Db.instance.chartRecordQueries.insertOne(SolarDate())

    fun getEphemerisPoint(solarDate: SolarDate) =
        Db.instance.ephemerisPointQueries.getBySolarDate(solarDate).executeAsOne()

    fun getCommentsForPalace(chart: Chart, palace: Palace): Map<Star, List<StarComment>> =
        chart.houses.getValue(palace).let { stars ->
            stars.map { star ->
                star to Db.instance.starCommentQueries.getByPalace(star, palace).executeAsList().filter { starComment ->
                    starComment.inHouseWith.all { it.intersect(stars).isNotEmpty() }
                }
            }.toMap()
        }

//    fun findCommentsForStarPalace(palace: Palace, stars: List<Star>)
//            = stars.map { star ->
//        Db.instance.starCommentQueries.getByPalace(star, palace).executeAsList().filter { starComment ->
//            starComment.inHouseWith.all { it.intersect(stars).isNotEmpty() }
//        }
//    }.flatten()
//
//    fun findCommentsForStarPalace(chartHouse: Map.Entry<House, List<Star>>)
//            = findCommentsForStarPalace(chartHouse.key.palace, chartHouse.value).sortedBy {
//        it.star.Rank
//    }

}