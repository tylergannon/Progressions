package com.meowbox.progressions.states

import com.meowbox.progressions.datastore.CurrentChart
import com.meowbox.progressions.datastore.NewChart
import com.meowbox.progressions.datastore.Search
import org.rekotlin.StateType
import org.rekotlinrouter.HasNavigationState
import org.rekotlinrouter.NavigationState

data class ApplicationState(
    override var navigationState: NavigationState,
    val currentChart: CurrentChart.State? = null,
    val newChart: NewChart.State = NewChart.State(),
    val search: Search.State = Search.State()
) : HasNavigationState, StateType
