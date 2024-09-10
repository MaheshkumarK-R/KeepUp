package com.mahikr.keepup.ui.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahikr.keepup.domain.store.usecase.GetName
import com.mahikr.keepup.domain.store.usecase.GetNumber
import com.mahikr.keepup.domain.store.usecase.SetName
import com.mahikr.keepup.domain.store.usecase.SetNumber
import com.mahikr.keepup.ui.presentation.state.LoadingComponentState
import com.mahikr.keepup.ui.presentation.state.login.LoginEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoadingVm_TAG"

@HiltViewModel
class LoadingVm @Inject constructor(
    private val getName: GetName,
    private val getNumber: GetNumber,
    private val saveName: SetName,
    private val saveNumber: SetNumber
) : ViewModel() {

    private val _loadingState: MutableStateFlow<LoadingComponentState> =
        MutableStateFlow(LoadingComponentState.Loading("Synchronizing data\nPlease wait"))
    val loadingState = _loadingState.asStateFlow()

    private val onMoveToMainScreen = Channel<Boolean>()
    val onNavigateFlow = onMoveToMainScreen.receiveAsFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            getName().zip(getNumber()) { userName: String, userPhoneNumber: Long ->
                Log.d(TAG, "init name :$userName phonenumber $userPhoneNumber")
                Pair(first = userName, second = userPhoneNumber.toString())
            }.collect { userInfo: Pair<String, String> ->
                Log.d(TAG, "init collect userInfo :${userInfo.first} phonenumber ${userInfo.second}")
                delay(300L)
                if (userInfo.first == "" && userInfo.second.length < 10)
                    updateLoadingState(LoadingComponentState.Idle)
                else onLoginEvent(LoginEvent.OnExistingUser)
            }
        }
    }

    fun onLoginEvent(onLoginEvent: LoginEvent) = viewModelScope.launch(Dispatchers.Default) {
        Log.d(TAG, "onLoginEvent: $onLoginEvent")
        when (onLoginEvent) {
            is LoginEvent.OnRegister -> {
                Log.d(TAG, "onLoginEvent:OnRegister ${onLoginEvent.name} & ${onLoginEvent.phoneNumber}")
                updateLoadingState(LoadingComponentState.Loading("Hey ${onLoginEvent.name}\nThanks for registering\\nPlease wait we are setting\\nBest content for you"))
                onLoginEvent.phoneNumber.toLongOrNull()?.let {
                    Log.d(TAG, "onLoginEvent.name ${onLoginEvent.name}  onLoginEvent.number $it")
                    saveName(name = onLoginEvent.name)
                    saveNumber(number = it)
                }
                delay(1000)
                onMoveToMainScreen.send(true)
            }

            LoginEvent.OnExistingUser -> {
                Log.d(TAG, "onMoveToMainScreen.send(true)")
                onMoveToMainScreen.send(true)
            }
        }
    }

    fun updateLoadingState(loadingComponentState: LoadingComponentState) {
        Log.d(
            TAG,
            "updateLoadingState: loadingState to $loadingComponentState from ${_loadingState.value}"
        )
        if (_loadingState.value != loadingComponentState)
            _loadingState.update { loadingComponentState }
    }

}