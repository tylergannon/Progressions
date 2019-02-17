package com.meowbox.progressions.routes

import android.content.Context
import android.content.Intent

object RouteHelper {
    fun createNewChartRoutable(context: Context) =
        Intent(context, NewChartRoutable::class.java).let { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            NewChartRoutable(context)
        }

    fun createViewChartRoutable(context: Context) =
        Intent(context, ViewChartRoutable::class.java).let { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            ViewChartRoutable(context)
        }
}