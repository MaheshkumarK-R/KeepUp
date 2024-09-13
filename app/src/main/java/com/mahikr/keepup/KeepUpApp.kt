package com.mahikr.keepup

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.mahikr.keepup.domain.buildNotification
import com.mahikr.keepup.domain.store.usecase.GetAlarmTime
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
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
        const val NAME = "Skill-Up Remainder"
        const val APP_LAUNCH_CODE = 987
    }

    @Inject
    lateinit var getAlarmTime: GetAlarmTime

    init {
        Log.d("KeepUpApp_TAG", " init : ")
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL, NAME, NotificationManager.IMPORTANCE_HIGH)
            val notificationMgr =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationMgr.createNotificationChannel(notificationChannel)
        }
        val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.d("KeepUpApp_TAG", "APP init: coroutineExceptionHandler: ${throwable.localizedMessage}  ${throwable.stackTrace}")
        }
        getAlarmTime().onEach { alarmTime ->
            Log.d("KeepUpApp_TAG", "init: getAlarmTime time in millis: $alarmTime format alarm time ${
                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(alarmTime)}")
            if(alarmTime ==0L) return@onEach
            /*PendingIntent.getBroadcast(this, 204, Intent(this, AppAlarmReceiver::class.java).apply {
                    action = "REBOOT"
                    putExtra("RESTART_TIME", alarmTime.toString())
                }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT).send()*/
            val currentTime = Calendar.getInstance().timeInMillis
            if(currentTime > alarmTime) {
                Log.d("KeepUpApp_TAG", "APP init:KeepUpApp alarm missed @${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(alarmTime)}")
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return@onEach
                }
                NotificationManagerCompat.from(this@KeepUpApp).notify(402,this@KeepUpApp.buildNotification(
                    channelId = CHANNEL,
                    iconId = R.drawable.login_background,
                    contentTitle = "Skill up time",
                    contentText = "Hey mate, It's time for you to grow your-self"
                ).build())

            }
            else {
                Log.d("KeepUpApp_TAG", "onCreate:KeepUpApp reschedule alarm")
                //this.setupPeriodicAlarm(alarmTime)
            }
        }.launchIn(CoroutineScope(Dispatchers.Default+coroutineExceptionHandler))


    }

}