package com.mahikr.keepup.ui.presentation.state

sealed interface MainScreenEvent {
    data object OnTaskComplete : MainScreenEvent
    data object OnNextTask : MainScreenEvent
    data object OnPreviousTask : MainScreenEvent
    data object OnCurrentTask : MainScreenEvent
}
