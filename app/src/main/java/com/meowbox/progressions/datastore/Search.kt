package com.meowbox.progressions.datastore

import com.meowbox.progressions.ChartRecord
import org.rekotlin.Action

class Search {
    data class State(
        val searchString: String = "",
        val showSearch: Boolean = false,
        val fetching: Boolean = false,
        val searchResults: List<ChartRecord> = listOf()
    )

    class ToggleSearchBarAction : Action
    class ChangeSearchStringAction(val searchString: String) : Action
    class SubmitSearchAction : Action
    class LoadSearchResultsAction(val chartRecords: List<ChartRecord>) : Action
    class ClearSearchAction : Action

    companion object {
        fun reducer(action: Action, state: State): State =
            when (action) {
                is ToggleSearchBarAction -> state.copy(showSearch = !state.showSearch)
                is ChangeSearchStringAction -> state.copy(searchString = action.searchString)
                is SubmitSearchAction -> state.copy(fetching = true)
                is LoadSearchResultsAction -> state.copy(
                    fetching = false,
                    searchResults = action.chartRecords
                )
                is ClearSearchAction -> state.copy(searchString = "")
                is NewChart.InsertChartRecordAction ->
                    state.copy(searchResults = state.searchResults.plus(action.chartRecord))
                else -> state
            }
    }
}
