package com.meowbox.progressions

import android.app.Application
import android.content.Context
import android.util.Log
import com.meowbox.progressions.datastore.CurrentChart
import com.meowbox.progressions.datastore.NewChart
import com.meowbox.progressions.datastore.Search
import com.meowbox.progressions.db.Db
import com.meowbox.progressions.db.loadDatabase
import com.meowbox.progressions.routes.RootRoutable
import com.meowbox.progressions.states.ApplicationState
import com.squareup.leakcanary.LeakCanary
import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.rekotlin.Action
import org.rekotlin.Middleware
import org.rekotlin.Reducer
import org.rekotlin.Store
import org.rekotlinrouter.NavigationReducer
import org.rekotlinrouter.Router

typealias ComponentReducer = (Action, ApplicationState) -> ApplicationState


private val applicationReducer: Reducer<ApplicationState> = { action, oldState ->
    with(
        oldState ?: ApplicationState(
            navigationState = NavigationReducer.handleAction(action, oldState?.navigationState)
        )
    ) {
        copy(
            navigationState = NavigationReducer.reduce(action, navigationState),
            currentChart = CurrentChart.reducer(action, currentChart),
            search = Search.reducer(action, search),
            newChart = NewChart.reducer(action, newChart)
        )
    }.also { if (action is CurrentChart.SelectCurrentChartAction) Log.i("Reducer", "$it") }
}


val logMiddleware: Middleware<ApplicationState> = { dispatch, getState ->
    { next ->
        { action ->
            Log.e("Progressions", "$action")
            next(action)
        }
    }
}

val store =
    Store(
        applicationReducer,
        state = null,
        middleware = listOf(logMiddleware),
        automaticallySkipRepeats = true
    )

var router: Router<ApplicationState>? = null
    private set

private const val dbName = "progressions.sqlite3"

class AppController : Application() {
    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        loadDatabase(applicationContext, dbName)
        Db.dbSetup(AndroidSqliteDriver(Database.Schema, applicationContext, dbName))

        instance = this

        router = Router(store, RootRoutable(applicationContext)) { subscription ->
            subscription.select { state -> state.navigationState }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        Db.dbClear()
    }


    companion object {

        //Getting tag it will be used for displaying log and it is optional
        val TAG = AppController::class.java.simpleName


        //Creating class object
        //Public static method to get the instance of this class
        @get:Synchronized
        var instance: AppController? = null
            private set

        fun getAppController(context: Context): AppController {
            if (instance == null) {
                //Create instance
                instance = AppController()
            }

            return instance as AppController
        }
    }
}

