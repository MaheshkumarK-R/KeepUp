package com.mahikr.keepup.ui.presentation.state.login

sealed interface LoginEvent {
    data class OnRegister(val name:String = "", val phoneNumber:String = ""):LoginEvent
    data object OnExistingUser:LoginEvent
}