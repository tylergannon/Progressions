package com.meowbox.progressions.controllers

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.meowbox.progressions.R
import com.meowbox.progressions.datastore.NewChart
import com.meowbox.progressions.store
import org.rekotlin.StoreSubscriber

class NewChartActivity : AppCompatActivity(), StoreSubscriber<NewChart.State> {
    override fun newState(state: NewChart.State) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chart)

        store.subscribe(this) { subscription ->
            subscription.select { it.newChart }
        }
    }
}
