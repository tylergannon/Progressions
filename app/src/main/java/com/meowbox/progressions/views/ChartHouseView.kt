package com.meowbox.progressions.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.meowbox.fourpillars.Branch
import com.meowbox.fourpillars.House
import com.meowbox.fourpillars.Star
import com.meowbox.progressions.R
import com.meowbox.progressions.store
import kotlinx.android.synthetic.main.layout_chart_house_view.view.*
import org.rekotlin.StoreSubscriber

class ChartHouseView(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs),
    StoreSubscriber<Map.Entry<House, List<Star>>> {

    override fun newState(state: Map.Entry<House, List<Star>>) {
        house_text_view.text = state.key.palace.name
        branch_text_view.text = state.key.branch.name
        with(stars_layout) {
            for (star in state.value)
                addView(TextView(context).apply {
                    text = star.english
                    id = star.ordinal
                })
        }
    }

    /**********************************************************
     * Loads the branch name from the style attributes given to this view
     * in the chart layout.
     *
     */
    init {
        View.inflate(context, R.layout.layout_chart_house_view, this)
        val branch = Branch.valueOf(tag.toString())

        store.subscribe(this) { subscription ->
            subscription.select { state ->
                val poop = state.currentChart!!
                val pop = poop.chart
                val dorth = pop.houses
                val kookle = dorth.entries
                Log.i("DORTH", "$branch $kookle")
                val dorgburgler = kookle.first {
                    val sniggle = it.key
                    sniggle.branch == branch
                }
                dorgburgler
//                state.currentChart!!.chart.houses.entries.first { it.key.branch == branch }
            }
        }
    }
}
//02-20 08:15:32.698 5399-5399/com.meowbox.progressions I/DORTH: Rabbit [
// House(palace=Youth, branch=Rooster)=[ZiWei, TanLang, TianKui, DiGong],
// House(palace=Children, branch=Goat)=[TianFu, YangRen, TianXing],
// House(palace=Partner, branch=Monkey)=[TianJi, TaiYin, WenChang],
// House(palace=Wealth, branch=Horse)=[TaiYang, WenQu, TianCun],
// House(palace=Health, branch=Snake)=[WuQu, PoJun, HuoXing, TuoLuo],
// House(palace=Career, branch=Dragon)=[TianTong, TianXi],
// House(palace=Property, branch=Ox)=[LianZhen, QiSha, DiJie],
// House(palace=Ancestors, branch=Pig)=[TianXiang, TianYao, TianYue],
// House(palace=Ming, branch=Dog)=[JuMen],
// House(palace=Pleasure, branch=Rat)=[TianLiang, LingXing, YouBi],
// House(palace=Superiors, branch=Tiger)=[ZuoFu]]
