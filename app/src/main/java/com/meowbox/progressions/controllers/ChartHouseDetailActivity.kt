package com.meowbox.progressions.controllers

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import com.meowbox.fourpillars.Star
import com.meowbox.progressions.R
import com.meowbox.progressions.StarComment
import com.meowbox.progressions.datastore.route
import com.meowbox.progressions.db.ChartData
import com.meowbox.progressions.routes.ChartListRoutable
import com.meowbox.progressions.store
import com.meowbox.progressions.viewmodels.ChartHouseModel
import com.meowbox.util.fancyJoin
import kotlinx.android.synthetic.main.activity_chart_house_detail.*

class ChartHouseDetailActivity : AppCompatActivity() {

    private var house: ChartHouseModel? = null

    override fun onOptionsItemSelected(item: MenuItem?) =
        if (item?.itemId == 16908332)
            false.also { store.route(ChartListRoutable.id) }
        else
            super.onOptionsItemSelected(item).also {
                Log.d(javaClass.simpleName, "I don't recognize ${item?.itemId}")
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_house_detail)
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        house = intent.getParcelableExtra("house")!!
        title = "${house!!.palace.name} Palace"

        ChartData.getCommentsForPalace(house!!.palace, house!!.stars).forEach {
            addCommentsForStar(it.key, it.value)
        }
    }

    override fun onBackPressed() {

        super.onBackPressed()

    }

    private fun List<Set<Star>>.names() =
        fancyJoin("and") {
            it.fancyJoin("or") { star -> star.english }
        }

    private val addCommentsForStar = { star: Star, comments: List<StarComment> ->
        for (starComment in comments) {
            val view = layoutInflater.inflate(R.layout.layout_star_commentary, null)
            view.findViewById<TextView>(R.id.title_textView).text = star.name
            view.findViewById<TextView>(R.id.additionalStars_textView).text =
                if (starComment.inHouseWith.isEmpty())
                    "alone in the ${house!!.palace} Palace"
                else
                    "with ${starComment.inHouseWith.names()}"
            view.findViewById<TextView>(R.id.commentary_textView).text =
                starComment.comments
            this.starComments_LinearLayout.addView(view)
        }
    }
}
