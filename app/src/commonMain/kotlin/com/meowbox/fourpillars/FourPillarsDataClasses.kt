package com.meowbox.fourpillars

import kotlinx.serialization.Serializable


open class Loop<T>(val all: Array<T>) {
    fun num(n: Int): T {
        val rem = n.rem(all.size)
        return all[if (rem < 0) rem + all.size else rem]
    }
}

interface Loopable<T> {
    fun advance(num: Int): T
    operator fun plus(num: Int): T = advance(num)
    operator fun plus(loopable: Loopable<T>): Int = ordinal + loopable.ordinal
    operator fun unaryPlus(): T = advance(1)
    operator fun unaryMinus(): T = advance(-1)
    operator fun minus(num: Int): T = advance(-num)
    operator fun minus(loopable: Loopable<T>): Int = ordinal - loopable.ordinal
    val all: Array<T>
    val ordinal: Int
    private val count: Int get() = all.size
    val diametric: T get() = advance(count / 2)
    val next: T get() = advance(1)
    val prev: T get() = advance(count - 1)
}

enum class Stem(val element: Element) : Loopable<Stem> {
    YangWood(Element.Wood),
    YinWood(Element.Wood),
    YangFire(Element.Fire),
    YinFire(Element.Fire),
    YangEarth(Element.Earth),
    YinEarth(Element.Earth),
    YangMetal(Element.Metal),
    YinMetal(Element.Metal),
    YangWater(Element.Water),
    YinWater(Element.Water);

    companion object : Loop<Stem>(Stem.values())

    override val all: Array<Stem>
        get() = Stem.all

    override fun advance(num: Int): Stem {
        return Stem.num(num + ordinal)
    }
}

enum class Branch(val nativeStem: Stem) : Loopable<Branch> {
    Rat(Stem.YangWater), // 0, 1
    Ox(Stem.YinEarth), // 2, 3
    Tiger(Stem.YangWood), // 4, 5
    Rabbit(Stem.YinWood), // 6, 7
    Dragon(Stem.YangEarth), // 8, 9
    Snake(Stem.YinFire), // 10, 11
    Horse(Stem.YangFire), // 12, 13
    Goat(Stem.YinEarth), // 14, 15
    Monkey(Stem.YangMetal), // 16, 17
    Rooster(Stem.YinMetal), // 18, 19
    Dog(Stem.YangEarth), // 20, 21
    Pig(Stem.YinWater); // 22, 23

    companion object : Loop<Branch>(Branch.values())

    override val all: Array<Branch>
        get() = Branch.all

    override fun advance(num: Int): Branch {
        return Branch.num(num + ordinal)
    }
}

@Serializable
data class Pillar(val stem: Stem, val branch: Branch) : Loopable<Pillar> {
    override val ordinal: Int
        get() {
            var diff = ((stem.ordinal - branch.ordinal) / 2).rem(6)
            if (diff < 0) diff += 6
            return diff * 10 + stem.ordinal
        }

    companion object : Loop<Pillar>(Array(60) { Pillar(Stem.all[it % 10], Branch.all[it % 12]) })

    override val all: Array<Pillar>
        get() = Pillar.all

    override fun advance(num: Int): Pillar {
        return Pillar.num(num + ordinal)
    }
}

@Serializable
data class LunarDate(val yearInEpoch: Byte, val monthNumber: Byte, val dayOfMonth: Byte, val dayPillarNumber: Byte) {
    val yearPillar = Pillar.all[yearInEpoch - 1]
    val monthPillar = Pillar(
        Stem.all[(2 * yearInEpoch + monthNumber - 1) % 10],
        Branch.all[(monthNumber + 1) % 12]
    )
    val dayPillar = Pillar.all[dayPillarNumber.toInt()]

    fun hourPillar(branch: Branch): Pillar {
        return Pillar(Stem.all[(2 * dayPillar.ordinal + branch.ordinal) % 10], branch)
    }

    fun asInt() = dayPillarNumber * 1 + dayOfMonth * 100 + monthNumber * 1_00_00 + yearInEpoch * 1_00_00_00

    companion object {
        fun fromInt(intEncoding: Int): LunarDate = LunarDate(
            (intEncoding / 1_00_00_00).toByte(),
            ((intEncoding % 1_00_00_00) / 1_00_00).toByte(),
            ((intEncoding % 1_00_00) / 1_00).toByte(),
            (intEncoding % 100).toByte()
        )
    }
}

enum class Element {
    Wood, Fire, Earth, Metal, Water;

    companion object {
        val innerElements = arrayOf(
            arrayOf(Water, Fire, Earth, Wood, Metal),
            arrayOf(Fire, Earth, Wood, Metal, Water),
            arrayOf(Wood, Metal, Water, Fire, Earth),
            arrayOf(Earth, Wood, Metal, Water, Fire),
            arrayOf(Metal, Water, Fire, Earth, Wood),
            arrayOf(Fire, Earth, Wood, Metal, Water)
        )
        val elementScoreTable = arrayOf(
            Metal, Fire, Wood, Earth, Metal, Fire, Wood, Earth, Metal, Wood,
            Water, Earth, Fire, Wood, Water, Metal, Fire, Wood, Earth, Metal,
            Fire, Water, Earth, Metal, Wood, Water, Earth, Fire, Wood, Water
        )
    }
}

