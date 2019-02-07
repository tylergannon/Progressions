package com.meowbox.progressions.db

import kotlin.test.Test
import kotlin.test.assertEquals

class TypesTest {

    @Test fun shouldBe32Bits() {
        assertEquals(32, Int.SIZE_BITS)
    }

    @Test fun shouldHaveGoodSizeMaxValue() {
        assertEquals(2147483647, Int.MAX_VALUE)
    }

    @Test fun testDateTime() {
        val aDateTime = DateTime(1978, 4, 7, 15, 30)
        println(aDateTime.id)
        println(aDateTime.year)
        println(aDateTime.id.rem(256))
        assertEquals(1978, aDateTime.year)
        assertEquals(4, aDateTime.monthOfYear)
        assertEquals(7, aDateTime.dayOfMonth)
        assertEquals(15, aDateTime.hourOfDay)
        assertEquals(30, aDateTime.minuteOfHour)
    }
}