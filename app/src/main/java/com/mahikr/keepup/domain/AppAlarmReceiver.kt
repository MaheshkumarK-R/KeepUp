package com.mahikr.keepup.domain

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.mahikr.keepup.R
import com.mahikr.keepup.common.AppConstants.ALARM_INTERVAL
import com.mahikr.keepup.common.AppConstants.CHANNEL
import com.mahikr.keepup.common.AppConstants.DONE
import com.mahikr.keepup.common.AppConstants.DONE_CODE
import com.mahikr.keepup.common.AppConstants.NOTIFICATION_CODE
import com.mahikr.keepup.common.AppConstants.REMAINDER
import com.mahikr.keepup.domain.store.usecase.GetAlarmTime
import com.mahikr.keepup.domain.store.usecase.SetAlarmTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AppAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var getAlarmTime: GetAlarmTime

    @Inject
    lateinit var setAlarmTime: SetAlarmTime

    companion object {
        private var nextAlarmTime: Long = 0L
        private var previousAlarmTime: Long = 0L
        private var mediaPlayer: MediaPlayer? = null
        private val TAG = "AppAlarmReceiver_TAG"
    }

    override fun onReceive(context: Context, intent: Intent) {

        try {
            Log.d(TAG, "onReceive: ${intent.action}")
            intent.action?.let { action ->
                if (Intent.ACTION_BOOT_COMPLETED == action || Intent.ACTION_LOCKED_BOOT_COMPLETED == action) {
                    Log.d(TAG, "onReceive: ${intent.action}")
                    val coroutineExceptionHandler =
                        CoroutineExceptionHandler { _, throwable ->
                            Log.d(
                                TAG,
                                "APP BOOT-UP: coroutineExceptionHandler: ${throwable.localizedMessage}  ${throwable.stackTrace}"
                            )
                        }
                    CoroutineScope(Dispatchers.Default + coroutineExceptionHandler).launch {
                        getAlarmTime().collectLatest {
                            Log.d(TAG, "BOOT-UP DB time collectLatest: $it format alarm time ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it)}")
                        }
                    }
                    getAlarmTime().onEach { dbTime -> Log.d(TAG, "BOOT-UP: DB time in millis: $dbTime format alarm time ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(dbTime)}")
                        if(dbTime != 0L){
                            val currentTime = Calendar.getInstance().timeInMillis
                            Log.d(TAG, "BOOT-UP Calendar time in millis: $currentTime format alarm time ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(currentTime)}")
                            if (currentTime > dbTime) {
                                Log.d(TAG, "BOOT-UP APP init:KeepUpApp alarm missed @${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(dbTime)}")
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    Log.d(TAG, "POST_NOTIFICATIONS:PERMISSION_GRANTED")
                                    NotificationManagerCompat.from(context).notify(
                                        NOTIFICATION_CODE, context.buildNotification(
                                            channelId = CHANNEL,
                                            iconId = R.drawable.login_background,
                                            contentTitle = "Skill up time",
                                            contentText = "Hey mate, there is a missed skill up alert @$dbTime"
                                        ).build()
                                    )
                                } else Log.d(TAG, "BOOT-UP POST_NOTIFICATIONS:!PERMISSION_GRANTED")

                            }
                            Log.d(TAG, "BOOT-UP: reschedule alarm")
                            context.setupPeriodicAlarm(dbTime)
                        }
                    }.launchIn(CoroutineScope(Dispatchers.Default + coroutineExceptionHandler))

                }
            }
            if (intent.hasExtra(REMAINDER)) {

                Log.d(TAG, "onReceive: ${intent.action} >> ${intent.hasExtra(REMAINDER)}")
                when (intent.action ?: "NA") {
                    DONE -> {
                        runBlocking {
                            Log.d(
                                TAG,
                                "onReceive:DONE mediaPlayer $mediaPlayer isPlaying ${mediaPlayer?.isPlaying}"
                            )
                            mediaPlayer?.apply {
                                stop()
                                release()
                            }.also {
                                mediaPlayer = null
                            }
                            Log.d(TAG, "onReceive:DONE mediaPlayer $mediaPlayer")
                            NotificationManagerCompat.from(context).cancel(NOTIFICATION_CODE)
                        }
                    }

                    else -> {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
                                context, Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.d(TAG, "onReceive: ${intent.getStringExtra(REMAINDER)}")
                            NotificationManagerCompat.from(context).notify(
                                NOTIFICATION_CODE, context.buildNotification(
                                    channelId = CHANNEL,
                                    iconId = R.drawable.login_background,
                                    contentTitle = "Skill up time",
                                    contentText = "Hey mate, It's time for you to grow your-self"
                                ).addAction(
                                    R.drawable.ic_check, "Ok", PendingIntent.getBroadcast(context, DONE_CODE,
                                        Intent(context, AppAlarmReceiver::class.java).apply {
                                            action = DONE
                                            putExtra(REMAINDER, DONE)
                                        },
                                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                                    )
                                ).build()
                            )
                            if (mediaPlayer == null) mediaPlayer =
                                MediaPlayer.create(context, R.raw.alarm_music)
                            mediaPlayer?.let { player ->
                                Log.d(TAG, "onReceive:else mediaPlayer $mediaPlayer")
                                player.setOnCompletionListener {
                                    it.stop()
                                    it.release()
                                }.also {
                                    mediaPlayer = null
                                }
                                player.start()
                            }

                            val currentTime = Calendar.getInstance().timeInMillis
                            Log.d(TAG, "AlarmReceiver: Calendar time in millis: $currentTime format alarm time ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(currentTime)}")
                            Log.d(TAG, "AlarmReceiver: nextAlarmTime time in millis: $nextAlarmTime" + " format alarm time ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(nextAlarmTime)}")
                            Log.d(TAG, "AlarmReceiver: previousAlarmTime time in millis: $previousAlarmTime" + " format alarm time ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(previousAlarmTime)}")
                            val coroutineExceptionHandler =
                                CoroutineExceptionHandler { _, throwable ->
                                    Log.d(TAG, "AlarmReceiver: coroutineExceptionHandler: ${throwable.localizedMessage}  ${throwable.stackTrace}")
                                }
                            val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler).launch {
                                getAlarmTime().collectLatest {
                                    Log.d(TAG, "AlarmReceiver:DB AlarmTime $it formatted time: ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it)}")
                                    if(it != 0L && nextAlarmTime != it+ALARM_INTERVAL) {
                                        nextAlarmTime = it.plus(ALARM_INTERVAL)
                                    }
                                    Log.d(TAG, "AlarmReceiver:In nextAlarmTime time in millis: $nextAlarmTime" + " format alarm time ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(nextAlarmTime)}")
                                    Log.d(TAG, "AlarmReceiver:In previousAlarmTime time in millis: $previousAlarmTime" + " format alarm time ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(previousAlarmTime)}")
                                }
                            }
                            CoroutineScope(Dispatchers.Default + coroutineExceptionHandler).launch {
                                while (nextAlarmTime ==0L) {
                                    delay(500L)
                                }
                                Log.d(TAG, "AlarmReceiver:POST nextAlarmTime time in millis: $nextAlarmTime" + " format alarm time ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(nextAlarmTime)}")
                                Log.d(TAG, "AlarmReceiver:POST previousAlarmTime time in millis: $previousAlarmTime" + " format alarm time ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(previousAlarmTime)}")
                                if(previousAlarmTime == 0L || previousAlarmTime != nextAlarmTime){
                                    setAlarmTime(nextAlarmTime)
                                    scope.cancel(CancellationException("close getAlarmTime scope"))
                                    Log.d(TAG, "AlarmReceiver: Updated db with nextAlarmTime $nextAlarmTime formatted time: ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(nextAlarmTime)}")
                                    getAlarmTime().collectLatest {
                                        Log.d(TAG, "AlarmReceiver:After DB updated DB time $it formatted time: ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it)}")
                                        previousAlarmTime = it
                                        Log.d(TAG, "AlarmReceiver:After DB updated nextAlarmTime  $nextAlarmTime formatted time: ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(nextAlarmTime)}")
                                        Log.d(TAG, "AlarmReceiver:After DB updated previousAlarmTime $previousAlarmTime formatted time: ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(previousAlarmTime)}")
                                    }
                                }
                            }
                        }
                    }
                }

            }
        } catch (exception: Exception) {
            Log.d(TAG, "onReceive: ${exception.localizedMessage}")
            exception.printStackTrace()
        }
    }
}