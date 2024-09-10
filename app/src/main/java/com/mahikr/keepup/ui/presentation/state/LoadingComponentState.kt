package com.mahikr.keepup.ui.presentation.state

sealed interface LoadingComponentState {
    data class Loading(val message:String) : LoadingComponentState
    data object Idle : LoadingComponentState
}