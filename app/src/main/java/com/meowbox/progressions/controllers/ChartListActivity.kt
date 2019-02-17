package com.meowbox.progressions.controllers

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.meowbox.progressions.ChartRecord
import com.meowbox.progressions.R
import com.meowbox.progressions.datastore.Search
import com.meowbox.progressions.datastore.route
import com.meowbox.progressions.routes.ChartListRoutable
import com.meowbox.progressions.routes.NewChartRoutable
import com.meowbox.progressions.routes.ViewChartRoutable
import com.meowbox.progressions.store

import kotlinx.android.synthetic.main.activity_chart_list.*
import org.rekotlin.StoreSubscriber

class ChartListActivity : AppCompatActivity(), StoreSubscriber<Search.State> {

    private var chartList: List<ChartRecord> = emptyList()
    private val listViewAdapter: ChartListAdapter = ChartListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_list)

        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        store.subscribe(this) {
            it.select { state -> state.search }.skipRepeats { old, new ->
                old.showSearch == new.showSearch &&
                        old.fetching == new.fetching &&
                        old.searchString == new.searchString
            }
        }


        with(chart_list_view) {
            adapter = listViewAdapter
        }

        initRoute()
    }


    override fun newState(newState: Search.State) {
        if (!newState.fetching) {
            chartList = newState.searchResults
            listViewAdapter.notifyDataSetChanged()
        }
    }

    private fun initRoute() =
        store.route(ChartListRoutable.id)

    private fun startNewChartActivity() =
        store.route(ChartListRoutable.id, NewChartRoutable.id)

    private fun startViewChartActivity() =
        store.route(ChartListRoutable.id, ViewChartRoutable.id)


    override fun onDestroy() {
        super.onDestroy()
        chart_list_view
        store.unsubscribe(this)
    }

    internal inner class ViewHolder(val personName_TextView: TextView, chartRecord: ChartRecord) {
        var chartRecord: ChartRecord = chartRecord
            set(it) {
                field = it
                personName_TextView.text = it.name
            }

        constructor(view: View, chartRecord: ChartRecord) :
                this(view.findViewById<TextView>(R.id.personName_TextField), chartRecord)
    }

    private inner class ChartListAdapter internal constructor() : BaseAdapter() {
        private val layoutInflater: LayoutInflater by lazy {
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        private fun inflateLayout() = layoutInflater.inflate(R.layout.layout_chart_list_item, null)

        /**********************************************************************
         * If existingView is not null, update its associated ViewHolder
         *      (stored in the tag property) with the ChartRecord at *position*.
         * Else inflate a new view from our layout and populate it with the same
         *      ChartRecord object.
         */
        override fun getView(position: Int, existingView: View?, parent: ViewGroup?): View =
            existingView?.apply { (tag as ViewHolder).chartRecord = chartList[position] }
                ?: inflateLayout().apply { tag = ViewHolder(this, chartList[position]) }

        override fun getItem(position: Int): Any = chartList[position]

        override fun getItemId(position: Int) = chartList[position].id.id.toLong()

        override fun getCount() = chartList.size
    }
}
