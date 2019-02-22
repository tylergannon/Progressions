package com.meowbox.progressions.controllers

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.meowbox.fourpillars.Branch
import com.meowbox.fourpillars.Chart
import com.meowbox.progressions.R
import com.meowbox.progressions.datastore.CurrentChart
import com.meowbox.progressions.datastore.RewindableNavigationState
import com.meowbox.progressions.datastore.route
import com.meowbox.progressions.getValue
import com.meowbox.progressions.routes.ChartHouseDetailRoutable
import com.meowbox.progressions.routes.ChartListRoutable
import com.meowbox.progressions.routes.ViewChartRoutable
import com.meowbox.progressions.store
import com.meowbox.progressions.viewmodels.ChartHouseModel
import kotlinx.android.synthetic.main.activity_view_chart.*
import org.rekotlin.StoreSubscriber
import org.rekotlinrouter.SetRouteAction
import org.rekotlinrouter.SetRouteSpecificData


class ViewChartActivity : AppCompatActivity(), StoreSubscriber<CurrentChart.State> {

    fun clickHouse(view: View) {
        val branch = Branch.valueOf(view.tag.toString())
        Log.e("ViewChartActivity", "Clicked $branch")
        val route = arrayListOf(ChartListRoutable.id, ViewChartRoutable.id, ChartHouseDetailRoutable.id)
        store.dispatch(
            SetRouteSpecificData(
                route, ChartHouseModel(
                    branch, chart!!.progressTo ?: chart!!.ming,
                    chart!!.houses.getValue(branch)
                )
            )
        )
        store.dispatch(SetRouteAction(route))
    }

    override fun onBackPressed() {
        Log.d(javaClass.simpleName, "Going back")
        store.dispatch(RewindableNavigationState.NavigateBackAction())
        super.onBackPressed()
    }

    private var chart: Chart? = null

    override fun newState(state: CurrentChart.State) {
        this.chart = state.chart
        title = state.chartRecord.name
    }

    override fun onDestroy() {
        super.onDestroy()
        store.unsubscribe(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
        if (item?.itemId == 16908332)
            false.also { store.route(ChartListRoutable.id) }
                .also { Log.d(javaClass.simpleName, "Routing UP.") }
        else
            super.onOptionsItemSelected(item).also {
                Log.d(javaClass.simpleName, "DUMB ${item?.itemId}")
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(javaClass.simpleName, "Creating ViewChartActivity.")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_chart)

        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


//        if (actionBar == null)
//            Log.d(javaClass.simpleName, "I don't have action bar.")
//        else
//            actionBar.setDisplayHomeAsUpEnabled(true)

        store.subscribe(this) { subscription ->
            subscription.select { it.currentChart!! }
        }

    }
}
