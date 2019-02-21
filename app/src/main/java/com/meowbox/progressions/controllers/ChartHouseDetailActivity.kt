package com.meowbox.progressions.controllers

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.meowbox.fourpillars.Chart
import com.meowbox.fourpillars.Star
import com.meowbox.progressions.R
import com.meowbox.progressions.db.ChartData
import com.meowbox.progressions.store
import com.meowbox.progressions.viewmodels.ChartHouseModel
import kotlinx.android.synthetic.main.activity_chart_house_detail.*
import org.rekotlin.StoreSubscriber

class ChartHouseDetailActivity : AppCompatActivity(), StoreSubscriber<Chart> {
    fun List<Set<Star>>.stringRepresentation() = map {
        if (it.size <= 2) it.first().english
        else it.map { it.english }.toTypedArray().let { stars ->
            stars.sliceArray(0..stars.size - 2).joinToString() + ", or " + stars.last()
        }
    }.joinToString()

    override fun newState(chart: Chart) {
        star_detail.text = ChartData.getCommentsForPalace(chart, house!!.palace).map {
            """${it.key.name}\n\n""" +
                    it.value.map { starComment ->
                        starComment.inHouseWith.stringRepresentation() + "\n\n" +
                                starComment.comments
                    }
        }.joinToString("\n\n\n")

    }

    private var house: ChartHouseModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_house_detail)
        house = intent.getParcelableExtra("house")!!
        title = "${house!!.palace.name} Palace"

        store.subscribe(this) { subscription ->
            subscription.select { it.currentChart!!.chart }
        }

    }
}
