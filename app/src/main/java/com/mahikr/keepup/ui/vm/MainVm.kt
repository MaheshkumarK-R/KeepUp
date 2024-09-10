package com.mahikr.keepup.ui.vm

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahikr.keepup.domain.db.model.DomainTask
import com.mahikr.keepup.domain.db.usecase.GetTaskById
import com.mahikr.keepup.domain.db.usecase.GetTasks
import com.mahikr.keepup.domain.store.usecase.GetAlarmTime
import com.mahikr.keepup.domain.store.usecase.GetDayId
import com.mahikr.keepup.domain.store.usecase.GetName
import com.mahikr.keepup.domain.store.usecase.SetAlarmTime
import com.mahikr.keepup.domain.store.usecase.SetDayId
import com.mahikr.keepup.ui.presentation.screen.MainScreenEvent
import com.mahikr.keepup.ui.presentation.state.LoadingComponentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val TAG = "MainVm_TAG"

@HiltViewModel
class MainVm @Inject constructor(
    private val getName: GetName,
    private val getDayId: GetDayId,
    private val setDayId: SetDayId,
    private val getTasks: GetTasks,
    private val getTaskByID: GetTaskById,
    private val getAlarmTime: GetAlarmTime,
    private val setAlarmTime: SetAlarmTime
) : ViewModel() {

    private val _loadingState: MutableStateFlow<LoadingComponentState> =
        MutableStateFlow(LoadingComponentState.Loading("Synchronizing data\nPlease wait"))

    val loadingState = _loadingState.asStateFlow()

    private val _currentDayIndexState: MutableStateFlow<Int> = MutableStateFlow(1)
    val currentDayIndexState = _currentDayIndexState.asStateFlow()

    private val _dayIndexState: MutableStateFlow<Int> = MutableStateFlow(1)
    val dayIndexState = _dayIndexState.asStateFlow()

    private val _taskInfo: MutableStateFlow<DomainTask> = MutableStateFlow(DomainTask())
    val taskInfo = _taskInfo.asStateFlow()

    var name = mutableStateOf("")
        private set

    var alarmTime = mutableStateOf("")
        private set

    private val onMoveToAcknowledgmentScreen = Channel<Boolean>()
    val onNavigateFlow = onMoveToAcknowledgmentScreen.receiveAsFlow()


    init {

        viewModelScope.launch {
            Log.d(TAG, "init : entry")
            updateLoadingState(LoadingComponentState.Loading("Loading tasks"))
            getAlarmTime().onEach {
                Log.d(TAG, "init : getAlarmTime $it")
                alarmTime.value =
                    if (it == 0L) "Please set reading remainder"
                    else SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it).toString()
            }.launchIn(viewModelScope)
            getDayId().zip(getName()) { userDay, userName ->
                updateLoadingState(LoadingComponentState.Loading("Loading day $userDay task"))
                Log.d(TAG, "init userDay:$userDay => userName $userName")
                name.value = userName
                _currentDayIndexState.update { userDay }
                _dayIndexState.update { userDay }
                Pair(name, userDay)
            }.collect { stateIntPair ->
                Log.d(TAG, "init userDay:${stateIntPair.second} => userName ${stateIntPair.first}")
                getTaskByID(stateIntPair.second).onEach { task ->
                    Log.d(TAG, "getTaskByID : $task")
                    task?.let { domainTask ->
                        Log.d(TAG, "getTaskByID completed : $domainTask")
                        _taskInfo.update { domainTask }
                    }
                    updateLoadingState(LoadingComponentState.Idle)
                }.launchIn(this)
            }

        }

    }

    fun onMainEvent(mainScreenEvent: MainScreenEvent) = viewModelScope.launch {

        when (mainScreenEvent) {
            MainScreenEvent.OnCurrentTask -> {
                Log.d(
                    TAG,
                    "onMainEvent: pre OnCurrentTask ${_dayIndexState.value}  => => => ${_currentDayIndexState.value} "
                )
                moveToCurrentTask()
                Log.d(
                    TAG,
                    "onMainEvent: post OnCurrentTask ${_dayIndexState.value}  => => => ${_currentDayIndexState.value} "
                )
            }

            MainScreenEvent.OnNextTask -> {
                Log.d(
                    TAG,
                    "onMainEvent: pre OnNextTask/OnTaskComplete ${_dayIndexState.value}  => => => ${_currentDayIndexState.value} "
                )
                moveToNextTask()
                Log.d(
                    TAG,
                    "onMainEvent: post OnNextTask/OnTaskComplete ${_dayIndexState.value}  => => => ${_currentDayIndexState.value} "
                )
            }

            MainScreenEvent.OnTaskComplete -> {
                Log.d(
                    TAG,
                    "onMainEvent: pre OnNextTask/OnTaskComplete ${_dayIndexState.value}  => => => ${_currentDayIndexState.value} "
                )
                moveToNextDayCompleted()
                Log.d(
                    TAG,
                    "onMainEvent: post OnNextTask/OnTaskComplete ${_dayIndexState.value}  => => => ${_currentDayIndexState.value} "
                )
            }


            MainScreenEvent.OnPreviousTask -> {
                Log.d(
                    TAG,
                    "onMainEvent: pre OnPreviousTask ${_dayIndexState.value}  => => => ${_currentDayIndexState.value} "
                )
                moveToPreviousTask()
                Log.d(
                    TAG,
                    "onMainEvent: post OnPreviousTask ${_dayIndexState.value}  => => => ${_currentDayIndexState.value} "
                )
            }
        }
    }

    fun onSaveAlarmTime(alarmTimeInMillis: Long) = viewModelScope.launch {
        Log.d(TAG, "onSaveAlarmTime: $alarmTimeInMillis")
        setAlarmTime(alarmTimeInMillis)
        alarmTime.value = if(alarmTimeInMillis == 0L) "Alarm been removed..." else SimpleDateFormat("hh:mm a", Locale.getDefault()).format(alarmTimeInMillis).toString()
        Log.d(TAG, "onSaveAlarmTime : $alarmTimeInMillis  & ${alarmTime.value}")
    }

    private fun moveToCurrentTask() = viewModelScope.launch {
        _dayIndexState.update { _currentDayIndexState.value }
        getTaskByID(_currentDayIndexState.value).onEach { task ->
            Log.d(TAG, "getTaskByID : $task of moveToCurrentTask ${_currentDayIndexState.value}")
            task?.let { domainTask ->
                Log.d(
                    TAG,
                    "getTaskByID completed : $domainTask of moveToCurrentTask ${_currentDayIndexState.value}"
                )
                _taskInfo.update { domainTask }
            }
            updateLoadingState(LoadingComponentState.Idle)
        }.launchIn(this)
    }

    private fun moveToNextDayCompleted() = viewModelScope.launch {
        Log.d(TAG, "moveToNextTask: ${_currentDayIndexState.value}")
        _currentDayIndexState.updateAndGet { it.plus(1) }.also {
            _dayIndexState.update { _currentDayIndexState.value }
        }
        if (_dayIndexState != _currentDayIndexState) moveToCurrentTask()
        if (_currentDayIndexState.value < 4) {
            _currentDayIndexState.update { it.plus(1) }
            launch {
                setDayId(_currentDayIndexState.value)
            }
            launch {
                _dayIndexState.update { _currentDayIndexState.value }
                getTaskByID(_currentDayIndexState.value).onEach { task ->
                    Log.d(
                        TAG,
                        "getTaskByID : $task of moveToNextTask ${_currentDayIndexState.value}"
                    )
                    task?.let { domainTask ->
                        Log.d(
                            TAG,
                            "getTaskByID completed : $domainTask of moveToNextTask ${_currentDayIndexState.value}"
                        )
                        _taskInfo.update { domainTask }
                    }
                    updateLoadingState(LoadingComponentState.Idle)
                }.launchIn(this)
            }
        } else {
            Log.d(TAG, "moveToNextTask: exceeds 30...")
            onMoveToAcknowledgmentScreen.send(true)
        }

    }

    private fun moveToNextTask() = viewModelScope.launch {
        Log.d(TAG, "moveToNextTask: ${_dayIndexState.value}  <=> ${_currentDayIndexState.value}")
        if (_dayIndexState.value < 4) {
            _dayIndexState.update { it.plus(1) }
            getTaskByID(_dayIndexState.value).onEach { task ->
                Log.d(TAG, "getTaskByID : $task of moveToNextTask ${_dayIndexState.value}")
                task?.let { domainTask ->
                    Log.d(
                        TAG,
                        "getTaskByID completed : $domainTask of moveToNextTask ${_dayIndexState.value}"
                    )
                    _taskInfo.update { domainTask }
                }
                updateLoadingState(LoadingComponentState.Idle)
            }.launchIn(this)
        }

        if (_currentDayIndexState.value > _dayIndexState.value) {
            Log.d(TAG, "moveToNextTask: exceeds 30...")
            onMoveToAcknowledgmentScreen.send(true)
        }

    }

    private fun moveToPreviousTask() = viewModelScope.launch {
        Log.d(TAG, "moveToPreviousTask: ${_dayIndexState.value}")
        if (_dayIndexState.value > 1) {
            _dayIndexState.update { it.minus(1) }
            getTaskByID(_dayIndexState.value).onEach { task ->
                Log.d(TAG, "getTaskByID : $task of moveToPreviousTask ${_dayIndexState.value}")
                task?.let { domainTask ->
                    Log.d(
                        TAG,
                        "getTaskByID completed : $domainTask of moveToPreviousTask ${_dayIndexState.value}"
                    )
                    _taskInfo.update { domainTask }
                }
                updateLoadingState(LoadingComponentState.Idle)
            }.launchIn(this)
        } else Log.d(TAG, "moveToNextTask: limit is 1...")
    }


    private fun updateLoadingState(loadingComponentState: LoadingComponentState) {
        Log.d(
            TAG,
            "updateLoadingState: loadingState to $loadingComponentState from ${_loadingState.value}"
        )
        if (_loadingState.value != loadingComponentState)
            _loadingState.update { loadingComponentState }
    }

}