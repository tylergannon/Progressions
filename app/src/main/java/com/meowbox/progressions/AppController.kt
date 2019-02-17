package com.meowbox.progressions

import android.app.Application
import android.content.Context
import com.meowbox.progressions.datastore.CurrentChart
import com.meowbox.progressions.datastore.NewChart
import com.meowbox.progressions.datastore.Search
import com.meowbox.progressions.routes.RootRoutable
import com.meowbox.progressions.states.ApplicationState
import com.squareup.leakcanary.LeakCanary
import org.rekotlin.Action
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
    }
}

val store =
    Store(
        applicationReducer,
        state = null,
        automaticallySkipRepeats = true
    )

var router: Router<ApplicationState>? = null
    private set

class AppController : Application() {
    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        instance = this

        router = Router(store, RootRoutable(applicationContext)) { subscription ->
            subscription.select { state -> state.navigationState }
        }
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