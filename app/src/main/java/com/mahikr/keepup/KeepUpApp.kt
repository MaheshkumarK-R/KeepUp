package com.mahikr.keepup

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.mahikr.keepup.domain.store.usecase.GetAlarmTime
import dagger.hilt.android.HiltAndroidApp
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
    }

}