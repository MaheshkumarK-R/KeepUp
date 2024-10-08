package com.mahikr.keepup

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.mahikr.keepup.common.AppConstants.CHANNEL
import com.mahikr.keepup.common.AppConstants.NAME
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KeepUpApp : Application() {

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