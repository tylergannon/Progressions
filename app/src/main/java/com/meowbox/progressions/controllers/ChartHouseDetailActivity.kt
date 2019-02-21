package com.meowbox.progressions.controllers

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.meowbox.fourpillars.Star
import com.meowbox.progressions.R
import com.meowbox.progressions.datastore.route
import com.meowbox.progressions.db.ChartData
import com.meowbox.progressions.routes.ChartListRoutable
import com.meowbox.progressions.store
import com.meowbox.progressions.viewmodels.ChartHouseModel
import kotlinx.android.synthetic.main.activity_chart_house_detail.*

class ChartHouseDetailActivity : AppCompatActivity() {
    fun List<Set<Star>>.stringRepresentation() = map {
        if (it.size <= 2) it.first().english
        else it.map { it.english }.toTypedArray().let { stars ->
            stars.sliceArray(0..stars.size - 2).joinToString() + ", or " + stars.last()
        }
    }.joinToString()

    private var house: ChartHouseModel? = null

    override fun onOptionsItemSelected(item: MenuItem?) =
        if (item?.itemId == 16908332)
            false.also { store.route(ChartListRoutable.id) }
                .also { Log.d(javaClass.simpleName, "Routing UP.") }
        else
            super.onOptionsItemSelected(item).also {
                Log.d(javaClass.simpleName, "DUMB ${item?.itemId}")
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_house_detail)
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        house = intent.getParcelableExtra("house")!!
        title = "${house!!.palace.name} Palace"

        star_detail.text = ChartData.getCommentsForPalace(house!!.palace, house!!.stars).map {
            """${it.key.name}\n\n""" +
                    it.value.map { starComment ->
                        starComment.inHouseWith.stringRepresentation() + "\n\n" +
                                starComment.comments
                    }
        }.joinToString("\n\n\n")
    }
}
