package com.mahikr.keepup.ui.presentation.screen

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mahikr.keepup.R
import com.mahikr.keepup.common.AppConstants
import com.mahikr.keepup.domain.cancelAlarm
import com.mahikr.keepup.domain.setupPeriodicAlarm
import com.mahikr.keepup.ui.presentation.component.LoadingComponent
import com.mahikr.keepup.ui.presentation.component.MainContent
import com.mahikr.keepup.ui.presentation.component.PermissionInfo
import com.mahikr.keepup.ui.presentation.component.PermissionRequestInfo
import com.mahikr.keepup.ui.presentation.component.PlainTextComponent
import com.mahikr.keepup.ui.presentation.component.TimePickerDialogCompose
import com.mahikr.keepup.ui.presentation.state.LoadingComponentState
import com.mahikr.keepup.ui.presentation.state.MainScreenEvent
import com.mahikr.keepup.ui.vm.MainVm
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@ExperimentalPermissionsApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(mainVm: MainVm = hiltViewModel(), onNavigate: () -> Unit) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isTimePickerVisible by remember { mutableStateOf(false) }

    val loadingState by mainVm.loadingState.collectAsStateWithLifecycle()
    val currentDayIndexState by mainVm.currentDayIndexState.collectAsStateWithLifecycle()
    val dayIndexState by mainVm.dayIndexState.collectAsStateWithLifecycle()
    val taskInfo by mainVm.taskInfo.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val formatTime = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    }

    val alarmTime = mainVm.alarmTime.value

    val onNavigateFlow by mainVm.onNavigateFlow.collectAsStateWithLifecycle(initialValue = false)
    LaunchedEffect(key1 = onNavigateFlow, block = {
        Log.d(TAG, "MainScreen: onNavigateFlow $onNavigateFlow")
        if (onNavigateFlow) {
            context.cancelAlarm()
            mainVm.onSaveAlarmTime(0L)
            mainVm.moveToAcknowledgmentScreen()
            onNavigate()
        }
    })


    val permissionList = mutableSetOf<String>().also {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            it.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            it.add(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
        }*/
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = permissionList.toList()
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    Log.d(TAG, "PermissionHandler: event ${event.name} $permissionList")
                    permissionsState.launchMultiplePermissionRequest()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )
    SideEffect {
        Log.d(TAG, "permissionsState.permissions.all: ${permissionsState.permissions.all { it.status.isGranted }}")
        Log.d(TAG, "MainScreen:" + "\ntitle ${taskInfo.title}" + "\ndayIndex ${taskInfo.dayIndex}" + "\nleetCodeQuestions ${taskInfo.leetCodeQuestions}" + "\nsystemDesignTopic ${taskInfo.systemDesignTopic}" + "\ncommunicationExercise ${taskInfo.communicationExercise} alarmTime: $alarmTime")
    }
    if (permissionsState.permissions.all { it.status.isGranted } || permissionList.isEmpty()) {

        ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(40.dp)
                ) {
                    Spacer(modifier = Modifier.height(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Icon(painterResource(id = R.drawable.ic_victory), contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    PlainTextComponent(
                        text = "KeepUp:",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red.copy(alpha = 0.56f),
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                }
                Spacer(modifier = Modifier.height(22.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth(0.65f))

                DrawerItem(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = mainVm.name.value
                )

                DrawerItem(
                    modifier1 = Modifier
                        .padding(10.dp)
                        .clickable {
                            isTimePickerVisible = !isTimePickerVisible
                            Log.d(TAG, "DrawerItem: clicked $isTimePickerVisible")
                        }
                        .fillMaxWidth(0.65f),
                    imageVector = Icons.Filled.Alarm,
                    contentDescription = "Set Alarm",
                )
                PlainTextComponent(text = "Skill up Reminder", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold)
                PlainTextComponent(text = alarmTime, modifier = Modifier.padding(12.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth(0.65f))

                if (isTimePickerVisible)
                    TimePickerDialogCompose(
                        onConfirm = {
                            val calender = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, it.hour)
                                set(Calendar.MINUTE, it.minute)
                            }

                            Log.d(
                                TAG,
                                "TimePickerDialogCompose: ${it.hour}:${it.minute} =>  ${it.is24hour}\n " +
                                        "HOUR_OF_DAY: ${calender.get(Calendar.HOUR_OF_DAY)} MINUTE: ${
                                            calender.get(
                                                Calendar.MINUTE
                                            )
                                        }" +
                                        "formatTime timeInMillis: ${formatTime.format(calender.timeInMillis)}" +
                                        "timeInMillis: ${calender.timeInMillis}"
                            )
                            //alarmTime = formatTime.format(calender.timeInMillis)
                            mainVm.onSaveAlarmTime(calender.timeInMillis)
                            context.setupPeriodicAlarm(calender.timeInMillis)
                            isTimePickerVisible = false
                        },
                        onDismiss = { isTimePickerVisible = false })
            }
        }, content = {
            Scaffold(topBar = {
                TopAppBar(navigationIcon = {
                    IconButton(onClick = {
                        if (!drawerState.isOpen) {
                            scope.launch { drawerState.open() }
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                    }
                }, title = {
                    Text(text = "Habit Tracker ♨️", textAlign = TextAlign.Center)
                })
            }) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    item {
                        when (val state = loadingState) {
                            LoadingComponentState.Idle -> {
                                MainContent(
                                    dayIndex = taskInfo.dayIndex.toLong(),
                                    title = taskInfo.title,
                                    leetCodeQuestions = taskInfo.leetCodeQuestions,
                                    systemDesignTopic = taskInfo.systemDesignTopic,
                                    communicationExercise = taskInfo.communicationExercise,
                                    userName = mainVm.name.value
                                )

                                Button(
                                    onClick = {
                                        if(currentDayIndexState > AppConstants.TASK_COUNT) onNavigate()
                                        else mainVm.onMainEvent(MainScreenEvent.OnTaskComplete)
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .fillParentMaxWidth(0.85f)
                                        .clip(MaterialTheme.shapes.medium)
                                ) {
                                    Text(text = if(currentDayIndexState > AppConstants.TASK_COUNT) "Accomplishment Screen" else "Task completed")
                                }


                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .fillParentMaxWidth(0.85f)
                                ) {
                                    if (currentDayIndexState > 1) Button(
                                        onClick = {
                                            if (dayIndexState > 1) mainVm.onMainEvent(
                                                MainScreenEvent.OnPreviousTask
                                            )
                                            else mainVm.onMainEvent(MainScreenEvent.OnCurrentTask)
                                        },
                                        modifier = Modifier
                                            .weight(2f)
                                            .fillMaxSize(0.85f)
                                            .clip(MaterialTheme.shapes.medium)
                                    ) {
                                        Text(text = if (dayIndexState > 1) "previous task" else "today's day")
                                    }
                                    if (dayIndexState != currentDayIndexState && dayIndexState < currentDayIndexState) {
                                        Spacer(modifier = Modifier.weight(.35f))
                                        Button(
                                            onClick = {
                                                mainVm.onMainEvent(MainScreenEvent.OnNextTask)
                                            },
                                            modifier = Modifier
                                                .weight(2f)
                                                .fillMaxSize(0.85f)
                                                .clip(MaterialTheme.shapes.medium)
                                        ) {
                                            Text(text = if (dayIndexState > currentDayIndexState) "Acknowledgement screen" else "next task")
                                        }
                                    }
                                }

                            }

                            is LoadingComponentState.Loading -> {
                                LoadingComponent(
                                    state.message, modifier = Modifier.fillParentMaxSize()
                                )
                            }

                        }

                    }

                }
            }
        })


    } else
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            permissionsState.permissions.forEach { permis ->
                when (permis.permission) {
                    Manifest.permission.POST_NOTIFICATIONS -> {
                        PermissionInfo(
                            permis,
                            PermissionRequestInfo("Notification", "Notification feature")
                        )
                    }

                    Manifest.permission.SCHEDULE_EXACT_ALARM -> {
                        PermissionInfo(permis, PermissionRequestInfo("Alarm", "Alarm"))
                    }
                }
            }
        }
}


private const val TAG = "Content_TAG"


@Composable
fun Topic(imageId: Int, topic: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        PlainTextComponent(text = topic, fontSize = MaterialTheme.typography.titleLarge.fontSize)
    }
}


@Composable
fun DrawerItem(
    modifier1: Modifier = Modifier.padding(10.dp),
    modifier: Modifier = Modifier.fillMaxWidth(0.65f),
    imageVector: ImageVector,
    contentDescription: String = "",
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    size: Dp = 25.dp,
    fontWeight: FontWeight = FontWeight.SemiBold
) {
    Row(
        modifier = modifier1,
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.size(size)
        )
        Spacer(Modifier.width(12.dp))
        PlainTextComponent(
            text = contentDescription,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = fontWeight
        )
        Spacer(Modifier.width(12.dp))
    }
    HorizontalDivider(modifier = modifier)
}
