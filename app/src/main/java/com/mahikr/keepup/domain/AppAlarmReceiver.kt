package com.mahikr.keepup.domain

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.mahikr.keepup.KeepUpApp
import com.mahikr.keepup.MainActivity
import com.mahikr.keepup.R
import com.mahikr.keepup.domain.store.usecase.GetAlarmTime
import com.mahikr.keepup.domain.store.usecase.SetAlarmTime
import dagger.hilt.android.AndroidEntryPoint
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
import java.util.concurrent.CancellationException
import javax.inject.Inject

@AndroidEntryPoint
class AppAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var getAlarmTime: GetAlarmTime

    @Inject
    lateinit var setAlarmTime: SetAlarmTime

    companion object {
        const val DONE = "done"
        const val REJECT = "reject"
        const val REMAINDER = "remainder"
        const val NOTIFICATION_CODE = 402
        private const val DONE_CODE = 302

        private var nextAlarmTime: Long = 0L
        private var previousAlarmTime: Long = 0L

        private fun Context.buildNotification(): Notification {
            val donePendingIntent = PendingIntent.getBroadcast(
                this, DONE_CODE, Intent(this, AppAlarmReceiver::class.java).apply {
                    action = DONE
                    putExtra(REMAINDER, DONE)
                }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val contentPendingIntent = PendingIntent.getActivity(
                this, 987, Intent(this, MainActivity::class.java).apply {
                    action = Intent.ACTION_MAIN
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            return NotificationCompat.Builder(this, KeepUpApp.CHANNEL).apply {
                setSmallIcon(R.drawable.ic_schedule)
                setContentTitle("Skill up time")
                setContentText("Hey mate, It's time for you to grow your-self")
                addAction(R.drawable.ic_check, "Ok", donePendingIntent)
                setOngoing(true)
                setContentIntent(contentPendingIntent)
            }.build()
        }

        private var mediaPlayer: MediaPlayer? = null
    }

    private val TAG = "AppAlarmReceiver_TAG"

    override fun onReceive(context: Context, intent: Intent) {

        try {
            Log.d(TAG, "onReceive: ${intent.action}")
            intent.action?.let { action ->
                if (Intent.ACTION_BOOT_COMPLETED == action || Intent.ACTION_LOCKED_BOOT_COMPLETED == action) {
                    Log.d(TAG, "onReceive: ${intent.action}")
                    /*CoroutineScope(Dispatchers.Default).launch {
                        Log.d(TAG, "onReceive: ${intent.action}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "ACTION_BOOT_COMPLETED/ACTION_LOCKED_BOOT_COMPLETED",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        //delay(3_000L)
                        getAlarmTime().onEach {
                            Log.d(
                                TAG,
                                "onReceive: ${intent.action} and getAlarmTime time in millis: $it format alarm time ${
                                    SimpleDateFormat(
                                        "hh:mm a",
                                        Locale.getDefault()
                                    ).format(it)
                                }"
                            )
                        }.launchIn(this)
                    }*/

                    val coroutineExceptionHandler =
                        CoroutineExceptionHandler { _, throwable ->
                            Log.d(
                                TAG,
                                "APP BOOT-UP: coroutineExceptionHandler: ${throwable.localizedMessage}  ${throwable.stackTrace}"
                            )
                        }
                    getAlarmTime().onEach { alarmTime ->
                        Log.d(
                            TAG,
                            "BOOT-UP: getAlarmTime time in millis: $alarmTime format alarm time ${
                                SimpleDateFormat(
                                    "hh:mm a",
                                    Locale.getDefault()
                                ).format(alarmTime)
                            }"
                        )

                        val currentTime = Calendar.getInstance().timeInMillis
                        Log.d(
                            TAG,
                            "BOOT-UP getAlarmTime time in millis: $currentTime format alarm time ${
                                SimpleDateFormat(
                                    "hh:mm a",
                                    Locale.getDefault()
                                ).format(currentTime)
                            }"
                        )
                        if (currentTime > alarmTime) {
                            Log.d(
                                TAG,
                                "BOOT-UP APP init:KeepUpApp alarm missed @${
                                    SimpleDateFormat(
                                        "hh:mm a",
                                        Locale.getDefault()
                                    ).format(alarmTime)
                                }"
                            )
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                Log.d(TAG, "POST_NOTIFICATIONS:PERMISSION_GRANTED")
                                /*NotificationManagerCompat.from(context).notify(
                                    402, context.buildNotification(
                                        channelId = CHANNEL,
                                        iconId = R.drawable.login_background,
                                        contentTitle = "Skill up time",
                                        contentText = "Hey mate, It's time for you to grow your-self"
                                    ).build()
                                )*/
                            } else Log.d(TAG, "BOOT-UP POST_NOTIFICATIONS:!PERMISSION_GRANTED")

                        } else {
                            Log.d(TAG, "BOOT-UP: reschedule alarm")
                            //context.setupPeriodicAlarm(alarmTime)
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
                            NotificationManagerCompat.from(context).notify(
                                NOTIFICATION_CODE, context.buildNotification(
                                    channelId = KeepUpApp.CHANNEL,
                                    iconId = R.drawable.login_background,
                                    contentTitle = "Skill up time",
                                    contentText = "Hey mate, It's time for you to grow your-self"
                                ).addAction(
                                    R.drawable.ic_check, "Ok", PendingIntent.getBroadcast(
                                        context,
                                        DONE_CODE,
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
                            Log.d(
                                TAG,
                                "AlarmReceiver: currentTime time in millis: $currentTime format alarm time ${
                                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                                        currentTime
                                    )
                                }"
                            )
                            Log.d(
                                TAG,
                                "AlarmReceiver: nextAlarm time in millis: ${currentTime.plus(2L * 60L * 1000L)}" + " format alarm time ${
                                    SimpleDateFormat(
                                        "hh:mm a",
                                        Locale.getDefault()
                                    ).format(currentTime.plus(2L * 60L * 1000L))
                                }"
                            )
                            val coroutineExceptionHandler =
                                CoroutineExceptionHandler { _, throwable ->
                                    Log.d(
                                        TAG,
                                        "AlarmReceiver: coroutineExceptionHandler: ${throwable.localizedMessage}  ${throwable.stackTrace}"
                                    )
                                }
                            val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler).launch {
                                getAlarmTime().collectLatest {
                                    if(nextAlarmTime != it.plus(2L * 60L * 1000L)) {
                                        nextAlarmTime = it.plus(2L * 60L * 1000L)
                                    }
                                    Log.d(TAG, "onReceive:collectLatest AlarmTime $it formatted time: ${SimpleDateFormat(
                                        "hh:mm a",
                                        Locale.getDefault()
                                    ).format(it)}")
                                }
                            }
                            CoroutineScope(Dispatchers.Default + coroutineExceptionHandler).launch {
                                while (nextAlarmTime ==0L) {
                                    delay(1500L)
                                }
                                if(previousAlarmTime == 0L || previousAlarmTime != nextAlarmTime){
                                    setAlarmTime(nextAlarmTime)
                                    scope.cancel(CancellationException("close getAlarmTime scope"))
                                    Log.d(TAG, "onReceive:previousAlarmTime != nextAlarmTime nextAlarmTime $nextAlarmTime formatted time: ${SimpleDateFormat(
                                        "hh:mm a",
                                        Locale.getDefault()
                                    ).format(nextAlarmTime)}")
                                    Log.d(TAG, "onReceive:previousAlarmTime != nextAlarmTime previousAlarmTime $previousAlarmTime formatted time: ${SimpleDateFormat(
                                        "hh:mm a",
                                        Locale.getDefault()
                                    ).format(previousAlarmTime)}")
                                    previousAlarmTime = nextAlarmTime
                                    getAlarmTime().collectLatest {
                                        Log.d(TAG, "onReceive:collectLatest AlarmTime $it formatted time: ${SimpleDateFormat(
                                            "hh:mm a",
                                            Locale.getDefault()
                                        ).format(it)}")
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