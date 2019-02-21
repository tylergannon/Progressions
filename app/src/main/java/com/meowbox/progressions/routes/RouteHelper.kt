package com.meowbox.progressions.routes

import android.content.Context
import android.content.Intent
import com.meowbox.progressions.controllers.*
import com.meowbox.progressions.store
import com.meowbox.progressions.viewmodels.ChartHouseModel

object RouteHelper {
    //    fun createViewChartRoutable(context: Context) =
    fun createNewChartRoutable(context: Context) = NewChartRoutable(context).also {
        Intent(context, NewChartActivity::class.java).let { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    fun createNewChartYearRoutable(context: Context) = NewChartYearRoutable(context).also {
        with(Intent(context, NewChartYearActivity::class.java)) {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(this)
        }
    }

    fun createNewChartDobRoutable(context: Context) = NewChartDobRoutable(context).also {
        with(Intent(context, NewChartDobActivity::class.java)) {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(this)
        }
    }

    fun createViewChartRoutable(context: Context) = ViewChartRoutable(context).also {
        Intent(context, ViewChartActivity::class.java).let { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    fun createNewChartTimeRoutable(context: Context) = NewChartTimeRoutable(context).also {
        with(Intent(context, NewChartTimeActivity::class.java)) {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(this)
        }
    }

    fun createChartHouseDetailRoutable(context: Context) = ChartHouseDetailRoutable(context).also {
        with(Intent(context, ChartHouseDetailActivity::class.java)) {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val currentRoute = store.state.navigationState.route
            val intentData = store.state.navigationState.getRouteSpecificState<ChartHouseModel>(currentRoute)
            putExtra("house", intentData)
            context.startActivity(this)
        }
    }
}