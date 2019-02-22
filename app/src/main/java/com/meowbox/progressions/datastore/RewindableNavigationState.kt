package com.meowbox.progressions.datastore

import android.util.Log
import org.rekotlin.Action
import org.rekotlinrouter.NavigationReducer
import org.rekotlinrouter.NavigationState
import org.rekotlinrouter.SetRouteAction

class RewindableNavigationState {
    data class State(
        val routingState: NavigationState,
        val history: List<NavigationState> = emptyList()
    )

    class NavigateBackAction : Action

    companion object {
        fun reducer(action: Action, state: State): State =
            state.routingState.copy().let { oldRoutingState ->
                state.copy(
                    routingState = when (action) {
                        is NavigateBackAction -> state.history.last().also {
                            Log.d("RewindableNavState", "$it")
                        }
                        else -> NavigationReducer.reduce(action, state.routingState)
                    },
                    history = when (action) {
                        is SetRouteAction -> state.history.plus(oldRoutingState)
                        is NavigateBackAction -> with(state.history) {
                            when (size) {
                                0 -> emptyList()
                                1 -> emptyList()
                                else -> subList(0, size - 2)
                            }
                        }
                        else -> state.history
                    }
                )
            }.apply {
                when (action) {
                    is SetRouteAction ->
                        Log.d(javaClass.simpleName, "SetRouteAction -- ${this.history}")
                    is NavigateBackAction ->
                        Log.d(javaClass.simpleName, "NavigateBackAction -- ${this.history}")
                }
            }
    }
}
