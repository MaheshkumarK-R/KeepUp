package com.mahikr.keepup.ui.presentation.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.EditCalendar
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahikr.keepup.R
import com.mahikr.keepup.domain.cancelAlarm
import com.mahikr.keepup.domain.setupPeriodicAlarm
import com.mahikr.keepup.ui.presentation.component.LoadingComponent
import com.mahikr.keepup.ui.presentation.component.PlainTextComponent
import com.mahikr.keepup.ui.presentation.state.LoadingComponentState
import com.mahikr.keepup.ui.vm.MainVm
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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

    SideEffect {
        Log.d(
            TAG,
            "MainScreen:" + "\ntitle ${taskInfo.title}" + "\ndayIndex ${taskInfo.dayIndex}" + "\nleetCodeQuestions ${taskInfo.leetCodeQuestions}" + "\nsystemDesignTopic ${taskInfo.systemDesignTopic}" + "\ncommunicationExercise ${taskInfo.communicationExercise} alarmTime: $alarmTime"
        )
    }
    val onNavigateFlow by mainVm.onNavigateFlow.collectAsStateWithLifecycle(initialValue = false)
    LaunchedEffect(key1 = onNavigateFlow, block = {
        Log.d(TAG, "MainScreen: onNavigateFlow $onNavigateFlow")
        if (onNavigateFlow) {
            context.cancelAlarm()
            mainVm.onSaveAlarmTime(0L)
            onNavigate()
        }
    })


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
                                dayIndex = taskInfo.dayIndex,
                                title = taskInfo.title,
                                leetCodeQuestions = taskInfo.leetCodeQuestions,
                                systemDesignTopic = taskInfo.systemDesignTopic,
                                communicationExercise = taskInfo.communicationExercise,
                                userName = mainVm.name.value
                            )

                            Button(
                                onClick = {
                                    mainVm.onMainEvent(MainScreenEvent.OnTaskComplete)
                                },
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .fillParentMaxWidth(0.85f)
                                    .clip(MaterialTheme.shapes.medium)
                            ) {
                                Text(text = "Task completed")
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

}


private const val TAG = "Content_TAG"

@Composable
fun MainContent(
    dayIndex: Int,
    title: String,
    leetCodeQuestions: List<String>,
    systemDesignTopic: String,
    communicationExercise: String,
    userName: String
) {
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.days_images),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
            PlainTextComponent(
                text = "Hey: $userName, Welcome to The",
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.SemiBold,
                color = Color(0x85D36A6A),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(
                    Alignment.TopCenter
                )
            )
            PlainTextComponent(
                text = "Day: $dayIndex  $title",
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(
                    Alignment.BottomStart
                ),
                color = Color.Red.copy(alpha = 0.5f)
            )
        }

        val handlerUrl = LocalUriHandler.current

        HyperLinkText(leetCodeQuestions = leetCodeQuestions, onClick = {
            Log.d("_TAG", "MainContent: Clicked on $it")
            handlerUrl.openUri(it)
        })

        Topic(R.drawable.system_integration, "System Design Topic: ")
        BulletText(systemDesignTopic)
        Topic(R.drawable.taskdone, "Communication Exercise: ")
        BulletText(communicationExercise)
    }
}

@Composable
fun BulletText(content: String) {
    val textColour = MaterialTheme.colorScheme.onSurface
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
    val annotatedString = androidx.compose.ui.text.buildAnnotatedString {
        withStyle(style = paragraphStyle) {
            withStyle(
                style = SpanStyle(
                    color = textColour
                )
            ) {
                append(Typography.bullet)
                append("\t\t")
            }
            withStyle(
                style = SpanStyle(
                    color = textColour
                )
            ) {
                append(content)
            }
        }
    }
    Text(text = annotatedString, modifier = Modifier.padding(horizontal = 25.dp))
}


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
fun HyperLinkText(
    leetCodeQuestions: List<String>,
    title: String = "LeetCode Questions: ",
    onClick: (String) -> Unit,
    painterId: Int? = R.drawable.leetcode
) {
    SideEffect {
        Log.d(TAG, "HyperLinkText: $leetCodeQuestions")
    }
    val textColour = MaterialTheme.colorScheme.onSurface
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            painterId?.let {
                Image(
                    painter = painterResource(id = painterId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            PlainTextComponent(
                text = title, fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        }
        leetCodeQuestions.forEach {
            val strings = it.split(": ")
            Log.d(TAG, "HyperLinkText: $it\nsplit strings $strings")
            val annotatedString = buildAnnotatedString(
                textColour, "link", annotation = strings[1], content = strings[0]
            )
            ClickableText(text = annotatedString, onClick = { offset ->
                annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.let { span ->
                    Log.d("_TAG", "MainContent: Clicked on ${span.item}")
                    onClick(span.item)
                }
            }, modifier = Modifier.padding(horizontal = 25.dp))
        }
    }
}


fun buildAnnotatedString(
    textColour: Color, tag: String, annotation: String, content: String
): AnnotatedString {
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
    return androidx.compose.ui.text.buildAnnotatedString {
        withStyle(style = paragraphStyle) {
            withStyle(
                style = SpanStyle(
                    color = textColour
                )
            ) {
                append(Typography.bullet)
                append("\t\t")
            }
            withStyle(
                style = SpanStyle(
                    color = textColour
                )
            ) {
                append(content)
            }
            append(" ")
            withStyle(
                style = SpanStyle(
                    color = Color(0xFFB5BCDD), textDecoration = TextDecoration.Underline
                )
            ) {
                pushStringAnnotation(tag = tag, annotation = annotation)
                append(tag)
            }
        }
    }
}


sealed interface MainScreenEvent {
    data object OnTaskComplete : MainScreenEvent
    data object OnNextTask : MainScreenEvent
    data object OnPreviousTask : MainScreenEvent
    data object OnCurrentTask : MainScreenEvent
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogCompose(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {

    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false
    )

    /** Determines whether the time picker is dial or input */
    var showDial by remember { mutableStateOf(true) }

    /** The icon used for the icon button that switches from dial to input */
    val toggleIcon = if (showDial) {
        Icons.Filled.EditCalendar
    } else {
        Icons.Filled.AccessTime
    }

    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = {
            onConfirm(timePickerState)
            Log.d("TAGi", "timePickerState.hour: ${timePickerState.hour}")
            Log.d("TAGi", "timePickerState.minute: ${timePickerState.minute}")
            Log.d("TAGi", "timePickerState.is24hour: ${timePickerState.is24hour}")
        },
        toggle = {
            IconButton(onClick = { showDial = !showDial }) {
                Icon(
                    imageVector = toggleIcon,
                    contentDescription = "Time picker type toggle",
                )
            }
        },
    ) {
        if (showDial) {
            TimePicker(
                state = timePickerState,
            )
        } else {
            TimeInput(
                state = timePickerState,
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}
