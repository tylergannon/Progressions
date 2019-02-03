package com.meowbox.progressions.datastore

import com.meowbox.fourpillars.*
import org.rekotlin.Action
import org.rekotlin.StateType

enum class RoddenRating { A, AA, B }

data class ChartRecord(
    val year: Int,
    val monthOfYear: Int,
    val dayOfMonth: Int,
    val hourOfDay: Int,
    val minuteOfHour: Int,
    val name: String,
    val isDst: Boolean = false,
    val yearBranch: Branch,
    val yearStem: Stem,
    val hourBranch: Branch,
    val hourStem: Stem,
    val ming: Branch,
    val ziWei: Palace,
    val tianFu: Palace,
    val url: String,
    val roddenRating: RoddenRating,
    val id: String
)

data class SearchState(
    val searchString: String? = null,
    val showSearch: Boolean = false,
    val searchResults: List<ChartRecord> = listOf()
)

data class ProgressionsAppState(
    val ephemeris: Ephemeris,
    val currentChart: Chart? = null,
    val search: SearchState = SearchState()
) : StateType


class ToggleSearchBarAction : Action
class ChangeSearchStringAction(val searchString: String) : Action
class SubmitSearchAction : Action
class SelectCurrentChartAction(val chartRecord: ChartRecord) : Action
class ClearSearchResultsAction : Action
