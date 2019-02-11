package com.meowbox.progressions.datastore

import com.meowbox.DateTime
import com.meowbox.fourpillars.Branch
import com.meowbox.fourpillars.Chart
import com.meowbox.fourpillars.Palace
import com.meowbox.progressions.ChartRecord
import com.meowbox.progressions.StarComment
import com.meowbox.progressions.db.ChartData
import com.meowbox.progressions.db.Db
import com.meowbox.progressions.db.loadChart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.rekotlin.*

data class ProgressionsAppState(
    val currentChart: CurrentChart.State? = null,
    val newChart: NewChart.State = NewChart.State(),
    val search: Search.State = Search.State()
) : StateType

class CurrentChart {
    data class State(
        val chart: Chart,
        val chartRecord: ChartRecord,
        val progressedTo: Branch? = null,
        val selectedPalace: Palace? = null,
        val palaceComments: List<StarComment> = listOf()
    )

    class SelectCurrentChartAction(val chartRecord: ChartRecord, val chart: Chart) : Action
    class SelectChartProgression(val progressTo: Branch?) : Action
    class SelectFocusPalaceAction(val palace: Palace) : Action
    class LoadStarCommentsAction(val starComments: List<StarComment>) : Action

    companion object {
        fun reducer(action: Action, state: ProgressionsAppState) = state.currentChart.let { currentChart ->
            when (action) {
                is SelectCurrentChartAction ->
                    state.copy(currentChart = State(action.chart, action.chartRecord))
                is SelectChartProgression ->
                    state.copy(currentChart = currentChart?.copy(progressedTo = action.progressTo))
                is SelectFocusPalaceAction ->
                    state.copy(currentChart = currentChart?.copy(selectedPalace = action.palace))
                is LoadStarCommentsAction ->
                    state.copy(currentChart = currentChart?.copy(palaceComments = action.starComments))
                else -> state
            }
        }
    }
}


class Search {
    data class State(
        val searchString: String? = null,
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
        fun reducer(action: Action, state: ProgressionsAppState): ProgressionsAppState =
            state.search.let { searchState ->
                when (action) {
                    is ToggleSearchBarAction -> searchState.copy(showSearch = !searchState.showSearch)
                    is ChangeSearchStringAction -> searchState.copy(searchString = action.searchString)
                    is SubmitSearchAction -> searchState.copy(fetching = true)
                    is LoadSearchResultsAction -> searchState.copy(
                        fetching = false,
                        searchResults = action.chartRecords
                    )
                    is ClearSearchAction -> searchState.copy(searchString = null)
                    else -> null
                }?.let { state.copy(search = it) } ?: state
            }
    }
}


// New Chart Record Actions
class NewChart {
    data class State(
        val name: String = "",
        val dob: DateTime = defaultDate
    )

    class ChangeNameAction(val name: String) : Action
    class ChangeDobAction(val dob: DateTime) : Action
    class InsertChartRecordAction(val chartRecord: ChartRecord) : Action

    companion object {
        val defaultDate = DateTime(1990, 1, 1, 12, 0)

        fun reducer(action: Action, state: ProgressionsAppState): ProgressionsAppState =
            state.newChart.let { newChartState ->
                when (action) {
                    is ChangeNameAction -> state.copy(newChart = newChartState.copy(name = action.name))
                    is ChangeDobAction -> state.copy(newChart = newChartState.copy(dob = action.dob))
                    is InsertChartRecordAction -> state.copy(newChart = State())
                    else -> state
                }
            }
    }
}


typealias ComponentReducer = (Action, ProgressionsAppState) -> ProgressionsAppState

fun composeReducers(vararg reducers: ComponentReducer): Reducer<ProgressionsAppState> = { action, state ->
    var newState: ProgressionsAppState = state ?: ProgressionsAppState()
    for (reducer in reducers) newState = reducer(action, newState)
    newState
}

val store =
    Store(
        composeReducers(
            CurrentChart.Companion::reducer,
            Search.Companion::reducer,
            NewChart.Companion::reducer
        ),
        ProgressionsAppState()
    )

object AsyncActions {
    fun insertChartRecordAction(chartRecord: ChartRecord): AsyncActionCreator<ProgressionsAppState, Store<ProgressionsAppState>> =
        { _, _, callback ->
            GlobalScope.launch {
                callback { _, _ ->
                    Db.instance.chartRecordQueries.insertOne(
                        chartRecord.ephemerisPointId,
                        chartRecord.name,
                        chartRecord.dob,
                        chartRecord.yearBranch,
                        chartRecord.yearStem,
                        chartRecord.hourBranch,
                        chartRecord.hourStem,
                        chartRecord.ming,
                        chartRecord.ziWei,
                        chartRecord.tianFu
                    )
                    NewChart.InsertChartRecordAction(chartRecord)
                }
            }
        }


    fun loadSelectCurrentChartAction(chartRecord: ChartRecord): AsyncActionCreator<ProgressionsAppState, Store<ProgressionsAppState>> =
        { _, _, callback ->
            GlobalScope.launch {
                callback { _, _ ->
                    CurrentChart.SelectCurrentChartAction(chartRecord, loadChart(chartRecord.dob))
                }
            }
        }

    fun loadSearchResultsAction(): AsyncActionCreator<ProgressionsAppState, Store<ProgressionsAppState>> =
        { state, _, callback ->
            GlobalScope.launch {
                callback { _, _ ->
                    Search.LoadSearchResultsAction(
                        Db.instance.chartRecordQueries.search("%${state.search.searchString}%").executeAsList()
                    )
                }
            }
        }

    fun loadStarCommentsAction(): AsyncActionCreator<ProgressionsAppState, Store<ProgressionsAppState>> =
        { state, _, callback ->
            GlobalScope.launch {
                callback { _, _ ->
                    state.currentChart!!.let { chartState ->
                        CurrentChart.LoadStarCommentsAction(
                            ChartData.getCommentsForPalace(
                                chartState.chart, chartState.selectedPalace!!
                            ).values.flatten()
                        )
                    }
                }
            }
        }
}
