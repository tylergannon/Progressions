package com.meowbox.progressions.datastore

import com.meowbox.DateTime
import com.meowbox.progressions.ChartRecord
import org.rekotlin.Action

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

        fun reducer(action: Action, state: State): State =
            when (action) {
                is ChangeNameAction -> state.copy(name = action.name)
                is ChangeDobAction -> state.copy(dob = action.dob)
                is InsertChartRecordAction -> State()
                else -> state
            }
    }
}
