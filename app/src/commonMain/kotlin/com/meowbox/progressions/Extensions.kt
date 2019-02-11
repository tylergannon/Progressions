package com.meowbox.progressions

import com.meowbox.fourpillars.House
import com.meowbox.fourpillars.Palace
import com.meowbox.fourpillars.Star

val EphemerisPoint.yearPillar get() = lunarDate.yearPillar
val EphemerisPoint.monthPillar get() = lunarDate.monthPillar

fun Map<House, List<Star>>.getValue(palace: Palace) = mapKeys { it.key.palace }.getValue(palace)
