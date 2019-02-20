package com.meowbox.progressions.controllers

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.meowbox.fourpillars.Branch
import com.meowbox.progressions.R
import com.meowbox.progressions.datastore.CurrentChart
import com.meowbox.progressions.store
import com.meowbox.progressions.views.ChartHouseView
import kotlinx.android.synthetic.main.activity_view_chart.*
import org.rekotlin.StoreSubscriber

class ViewChartActivity : AppCompatActivity(), StoreSubscriber<CurrentChart.State> {
    fun openChartHouse(branch: Branch) {}

    fun clickRat(view: Any) = openChartHouse(Branch.Rat)
    fun clickOx(view: View) = openChartHouse(Branch.Ox)
    fun clickTiger(view: View) = openChartHouse(Branch.Tiger)
    fun clickRabbit(view: View) = openChartHouse(Branch.Rabbit)
    fun clickDragon(view: View) = openChartHouse(Branch.Dragon)
    fun clickSnake(view: View) = openChartHouse(Branch.Snake)
    fun clickHorse(view: View) = openChartHouse(Branch.Horse)
    fun clickGoat(view: View) = openChartHouse(Branch.Goat)
    fun clickMonkey(view: View) = openChartHouse(Branch.Monkey)
    fun clickRooster(view: View) = openChartHouse(Branch.Rooster)
    fun clickDog(view: View) = openChartHouse(Branch.Dog)
    fun clickPig(view: View) = openChartHouse(Branch.Pig)

    val houseViews: Map<Branch, ChartHouseView> by lazy {
        mapOf(
            Branch.Rat to house_rat,
            Branch.Ox to house_ox,
            Branch.Tiger to house_tiger,
            Branch.Rabbit to house_rabbit,
            Branch.Dragon to house_dragon,
            Branch.Snake to house_snake,
            Branch.Horse to house_horse,
            Branch.Goat to house_goat,
            Branch.Monkey to house_monkey,
            Branch.Rooster to house_rooster,
            Branch.Dog to house_dog,
            Branch.Pig to house_pig
        )
    }

    override fun newState(state: CurrentChart.State) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_chart)

        store.subscribe(this) { subscription ->
            subscription.select { it.currentChart!! }
        }

    }
}
