package com.mahikr.keepup

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.mahikr.keepup.domain.setupPeriodicAlarm
import com.mahikr.keepup.domain.store.usecase.GetAlarmTime
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class KeepUpApp : Application() {
    companion object {
        const val CHANNEL = "channel"
        const val NAME = "name"
    }

    @Inject
    lateinit var getAlarmTime: GetAlarmTime

    init {
        Log.d("KeepUpApp_TAG", " init : ")
    }

    override fun onCreate() {
        super.onCreate()

        getAlarmTime().onEach {
            getAlarmTime().onEach {
                Log.d(
                    "KeepUpApp_TAG",
                    "init: getAlarmTime time in millis: $it format alarm time ${
                        SimpleDateFormat(
                            "hh:mm a",
                            Locale.getDefault()
                        ).format(it)
                    }"
                )
            }.launchIn(
                CoroutineScope(Dispatchers.Default)
            )
            /*PendingIntent.getBroadcast(this, 204, Intent(this, AppAlarmReceiver::class.java).apply {
                    action = "REBOOT"
                    putExtra("RESTART_TIME", it.toString())
                }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT).send()*/

            val currentTime = Calendar.getInstance().timeInMillis
            val alarmTime = it
            if(currentTime > it) Log.d("KeepUpApp_TAG", "onCreate:KeepUpApp alarm missed @${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it)}")
            else Log.d("KeepUpApp_TAG", "onCreate:KeepUpApp reschedule alarm")
            this.setupPeriodicAlarm(alarmTime)
        }.launchIn(CoroutineScope(Dispatchers.Default))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL, NAME, NotificationManager.IMPORTANCE_HIGH)
            val notificationMgr =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationMgr.createNotificationChannel(notificationChannel)
        }
    }

}