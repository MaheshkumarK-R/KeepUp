package com.mahikr.keepup.ui.vm

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahikr.keepup.common.AppConstants.TASK_COUNT
import com.mahikr.keepup.domain.db.model.DomainTask
import com.mahikr.keepup.domain.db.usecase.GetTaskById
import com.mahikr.keepup.domain.store.usecase.GetAlarmTime
import com.mahikr.keepup.domain.store.usecase.GetDayId
import com.mahikr.keepup.domain.store.usecase.GetName
import com.mahikr.keepup.domain.store.usecase.SetAlarmTime
import com.mahikr.keepup.domain.store.usecase.SetDayId
import com.mahikr.keepup.ui.presentation.state.LoadingComponentState
import com.mahikr.keepup.ui.presentation.state.MainScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val TAG = "MainVm_TAG"

@HiltViewModel
class MainVm @Inject constructor(
    private val getName: GetName,
    private val getDayId: GetDayId,
    private val setDayId: SetDayId,
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

    private val _name: MutableStateFlow<String> = MutableStateFlow("")
    val name = _name.asStateFlow()

    var alarmTime = mutableStateOf("")
        private set

    private val onMoveToAcknowledgmentScreen = Channel<Boolean>()
    val onNavigateFlow = onMoveToAcknowledgmentScreen.receiveAsFlow()


    init {

        viewModelScope.launch(Dispatchers.Default) {
            Log.d(TAG, "init : entry")
            updateLoadingState(LoadingComponentState.Loading("Loading tasks"))
            getAlarmTime().onEach {
                Log.d(TAG, "init : getAlarmTime $it")
                alarmTime.value =
                    if (it == 0L) "Please set reading remainder"
                    else SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it).toString()
            }.launchIn(this)
            getDayId().zip(getName()) { userDay, userName ->
                updateLoadingState(LoadingComponentState.Loading("Loading day $userDay task"))
                Log.d(TAG, "init userDay:$userDay => userName $userName")
                _name.update { userName }
                _currentDayIndexState.updateAndGet { userDay }.also { dayIndex ->
                    Log.d(TAG, "_currentDayIndexState.updateAndGet : $dayIndex")
                    _dayIndexState.update { dayIndex }
                }
                Pair(userName, userDay)
            }.collectLatest { stateIntPair ->
                Log.d(TAG, "init userDay:${stateIntPair.second} => userName ${stateIntPair.first}")
                if(stateIntPair.second > TASK_COUNT){
                    moveToAcknowledgmentScreen()
                }else {
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

    }

    fun onMainEvent(mainScreenEvent: MainScreenEvent) = viewModelScope.launch(Dispatchers.Default) {

        when (mainScreenEvent) {
            MainScreenEvent.OnCurrentTask -> {

                Log.d(TAG, "onMainEvent: OnCurrentTask pre dayIndex ${_dayIndexState.value} currentDayIndex ${_currentDayIndexState.value} ")
                getTaskByID(async { moveIndexToCurrentDay() }.await()).collectLatest {task ->
                    task?.let { domainTask ->
                        Log.d(TAG, "onMainEvent: OnCurrentTask getTaskByID completed : $domainTask of moveToCurrentTask")
                        _taskInfo.update { domainTask }
                    }
                    updateLoadingState(LoadingComponentState.Idle)
                }.also {
                    Log.d(TAG, "onMainEvent: OnCurrentTask post dayIndex ${_dayIndexState.value} currentDayIndex ${_currentDayIndexState.value} ")
                }
            }

            MainScreenEvent.OnNextTask -> {

                Log.d(TAG, "onMainEvent: OnNextTask pre dayIndex ${_dayIndexState.value} currentDayIndex ${_currentDayIndexState.value} ")
                getTaskByID(async { moveIndexToNextDay() }.await()).collectLatest {task ->
                    task?.let { domainTask ->
                        Log.d(TAG, "onMainEvent: OnNextTask getTaskByID completed : $domainTask of moveToCurrentTask")
                        _taskInfo.update { domainTask }
                    }
                    updateLoadingState(LoadingComponentState.Idle)
                }.also {
                    Log.d(TAG, "onMainEvent: OnNextTask post dayIndex ${_dayIndexState.value} currentDayIndex ${_currentDayIndexState.value} ")
                }
            }

            MainScreenEvent.OnTaskComplete -> {
                Log.d(TAG, "onMainEvent: pre OnTaskComplete ${_dayIndexState.value}  => => => ${_currentDayIndexState.value} ")
                coroutineScope {
                    async { moveCurrentDay() }.await().also {
                        Log.d(TAG, "onMainEvent: mid async >> $it")
                        launch {
                            setDayId(it)
                        }
                        launch {
                            if(it > TASK_COUNT) {
                                Log.d(TAG, "OnTaskComplete: exceeds TASK_COUNT $TASK_COUNT...")
                                moveToAcknowledgmentScreen()
                            }else getTaskByID(it).collectLatest {task ->
                                task?.let { domainTask ->
                                    Log.d(TAG, "OnTaskComplete getTaskByID completed : $domainTask of moveToCurrentTask $it")
                                    _taskInfo.update { domainTask }
                                }
                                updateLoadingState(LoadingComponentState.Idle)
                            }
                        }
                    }
                }
                Log.d(TAG, "onMainEvent: OnTaskComplete post dayIndex ${_dayIndexState.value} currentDayIndex ${_currentDayIndexState.value} ")
            }


            MainScreenEvent.OnPreviousTask -> {
                Log.d(TAG, "onMainEvent: OnPreviousTask pre dayIndex ${_dayIndexState.value} currentDayIndex ${_currentDayIndexState.value} ")
                getTaskByID( async { moveIndexToPreviousDay() }.await() ).collectLatest {task ->
                    task?.let { domainTask ->
                        Log.d(TAG, "onMainEvent: OnPreviousTask getTaskByID completed : $domainTask of moveToCurrentTask")
                        _taskInfo.update { domainTask }
                    }
                    updateLoadingState(LoadingComponentState.Idle)
                }.also {
                    Log.d(TAG, "onMainEvent: OnPreviousTask post dayIndex ${_dayIndexState.value} currentDayIndex ${_currentDayIndexState.value} ")
                }

            }
        }
    }

    suspend fun  moveToAcknowledgmentScreen() = withContext(Dispatchers.Default){
        Log.d(TAG, "moveToAcknowledgmentScreen:pre _dayIndexState ${_dayIndexState.value} _currentDayIndexState ${_currentDayIndexState.value}")
        _currentDayIndexState.updateAndGet { TASK_COUNT}.also {
            _dayIndexState.update { TASK_COUNT }
            getTaskByID( TASK_COUNT ).collectLatest {task ->
                task?.let { domainTask ->
                    Log.d(TAG, "onMainEvent: OnPreviousTask getTaskByID completed : $domainTask of moveToCurrentTask")
                    _taskInfo.update { domainTask }
                }
                updateLoadingState(LoadingComponentState.Idle)
            }
        }.also {
            onMoveToAcknowledgmentScreen.send(true)
            Log.d(TAG, "moveToAcknowledgmentScreen:post _dayIndexState ${_dayIndexState.value} _currentDayIndexState ${_currentDayIndexState.value}")
        }
    }

    fun onSaveAlarmTime(alarmTimeInMillis: Long) = viewModelScope.launch(Dispatchers.Default) {
        Log.d(TAG, "onSaveAlarmTime: $alarmTimeInMillis")
        setAlarmTime(alarmTimeInMillis)
        alarmTime.value =
            if (alarmTimeInMillis == 0L) "Alarm been removed..." else SimpleDateFormat("hh:mm a", Locale.getDefault()).format(alarmTimeInMillis).toString()
        Log.d(TAG, "onSaveAlarmTime : $alarmTimeInMillis  & ${alarmTime.value}")
        moveToAcknowledgmentScreen()
    }

    private suspend fun moveIndexToNextDay() = withContext(Dispatchers.Default){
        Log.d(TAG, "moveIndexNext: currentIndex ${_currentDayIndexState.value} dayIndex ${_dayIndexState.value}")
        if (_dayIndexState.value < _currentDayIndexState.value)
            _dayIndexState.update { it.plus(1)
        } else Log.d(TAG, "moveIndexNext:dayIndex>=currentIndex ")
        _dayIndexState.value
    }
    private suspend fun moveIndexToPreviousDay() = withContext(Dispatchers.Default){
        Log.d(TAG, "moveIndexPrevious: currentIndex ${_currentDayIndexState.value} dayIndex ${_dayIndexState.value}")
        if(_dayIndexState.value > TASK_COUNT) {
            Log.d(TAG, "moveIndexPrevious: dayIndex TASK_COUNT")
            _currentDayIndexState.updateAndGet { TASK_COUNT.plus(1) }.also {
                _dayIndexState.update { if(_dayIndexState.value > TASK_COUNT) TASK_COUNT.minus(1) else _dayIndexState.value.minus(1) }
            }
            return@withContext _dayIndexState.value
        }
        if (_dayIndexState.value >1)
            _dayIndexState.update { it.minus(1) }
        else Log.d(TAG, "moveIndexPrevious:dayIndex<=0 ")
        Log.d(TAG, "moveIndexPrevious: currentIndex ${_currentDayIndexState.value} dayIndex ${_dayIndexState.value}")
        _dayIndexState.value
    }
    private suspend fun moveIndexToCurrentDay() = withContext(Dispatchers.Default){
        Log.d(TAG, "moveIndexCurrentDay: currentIndex ${_currentDayIndexState.value} dayIndex ${_dayIndexState.value}")
        if(_currentDayIndexState.value > TASK_COUNT) _currentDayIndexState.update { TASK_COUNT }
        _dayIndexState.update { _currentDayIndexState.value }
        Log.d(TAG, "moveIndexCurrentDay: currentIndex ${_currentDayIndexState.value} dayIndex ${_dayIndexState.value}")
        _dayIndexState.value
    }

    private suspend fun moveCurrentDay() = withContext(Dispatchers.Default){
        Log.d(TAG, "moveCurrentDay: currentIndex ${_currentDayIndexState.value} dayIndex ${_dayIndexState.value}")
        _currentDayIndexState.updateAndGet { it.plus(1) }.also { updatedIndex: Int ->
            _dayIndexState.update { updatedIndex } }
        Log.d(TAG, "moveIndexCurrentDay: currentIndex ${_currentDayIndexState.value} dayIndex ${_dayIndexState.value}")
        _dayIndexState.value
    }


    private fun updateLoadingState(loadingComponentState: LoadingComponentState) {
        Log.d(TAG, "updateLoadingState: loadingState to $loadingComponentState from ${_loadingState.value}")
        if (_loadingState.value != loadingComponentState)
            _loadingState.update { loadingComponentState }
    }

}