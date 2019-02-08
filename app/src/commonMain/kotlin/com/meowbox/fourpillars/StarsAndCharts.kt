package com.meowbox.fourpillars

import kotlinx.serialization.Serializable
import kotlinx.serialization.internal.EnumSerializer

@Serializable(with = EnumSerializer::class)
enum class Palace {
    Ming, Youth, Partner, Children, Wealth, Health, Career, Assistants, Superiors, Property, Pleasure, Ancestors;

    companion object : Loop<Palace>(Palace.values())
}

data class House(val palace: Palace, val branch: Branch) : Comparable<House> {
    override fun compareTo(other: House): Int {
        return branch.compareTo(other.branch)
    }

    constructor(starPlacement: Branch, mingLocation: Branch) :
            this(Palace.num(mingLocation - starPlacement), starPlacement)
}

fun Map<House, List<Star>>.getValue(palace: Palace) = mapKeys { it.key.palace }.getValue(palace)

interface ChartParameters {
    val yearPillar: Pillar
    val monthPillar: Pillar
    val dayPillar: Pillar
    val hourPillar: Pillar
    val dayOfMonth: Int
    val ming: Branch
    val innerElement: Element
    val elementScores: ElementScores
}

class Chart(
    override val yearPillar: Pillar,
    override val monthPillar: Pillar,
    override val dayPillar: Pillar,
    override val hourPillar: Pillar,
    override val dayOfMonth: Int,
    val progressTo: Branch? = null
) : ChartParameters {
    override val ming = Branch.num(monthPillar.branch.ordinal - hourPillar.branch.ordinal)

    override val innerElement = Element.innerElements[(ming.ordinal) / 2][yearPillar.stem.ordinal % 5]
    override val elementScores: ElementScores
        get() {
            val scores = ElementScores()
            for (pillar in arrayOf(yearPillar, monthPillar, dayPillar, hourPillar)) {
                scores.score(pillar.stem.element)
                scores.score(pillar.branch.nativeStem.element)
                scores.score(Element.elementScoreTable[pillar.ordinal / 2])
            }
            return scores
        }


    val houses = Star.all.groupBy { House(it.findIn(this), progressTo ?: ming) }
}

class ElementScores {
    class ElementScore(val element: Element) {
        var score: Int = 0
    }

    val scores: List<ElementScore> = Element.values().map { ElementScore(it) }
    fun score(element: Element) {
        scores[element.ordinal].score++
    }
}

enum class Auspices { Exalted, Positive, Neutral, Mixed, Negative, Haunted }
