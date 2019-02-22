package com.meowbox.progressions.datastore

import org.rekotlin.Action
import org.rekotlinrouter.NavigationReducer
import org.rekotlinrouter.NavigationState
import org.rekotlinrouter.SetRouteAction
import java.util.*

class RewindableNavigationState {
    data class State(
        val routingState: NavigationState,
        val history: Stack<NavigationState> = Stack()
    )

    class NavigateBackAction : Action

    companion object {
        fun reducer(action: Action, state: State): State =
            (state.routingState ?: NavigationReducer.handleAction(action, null)).let { navigationState ->
                state.copy(
                    routingState = when (action) {
                        is NavigateBackAction -> state.history.pop()
                        else -> NavigationReducer.reduce(action, navigationState)
                    },
                    history = state.history.apply {
                        if (action is SetRouteAction)
                            push(navigationState.copy())
                    }
                )
            }
    }
}
