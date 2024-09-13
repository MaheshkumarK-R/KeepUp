package com.mahikr.keepup.domain

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.mahikr.keepup.KeepUpApp.Companion.APP_LAUNCH_CODE
import com.mahikr.keepup.MainActivity

fun Context.buildNotification(channelId: String, iconId:Int, contentTitle:String,contentText:String, isOnGoing:Boolean=false): NotificationCompat.Builder =
    NotificationCompat.Builder(this, channelId).apply {
        setSmallIcon(iconId)
        setContentTitle(contentTitle)
        setContentText(contentText)
        setOngoing(isOnGoing)
        setContentIntent(
            PendingIntent.getActivity(
            this@buildNotification, APP_LAUNCH_CODE,
            Intent(this@buildNotification, MainActivity::class.java).apply {
                action = Intent.ACTION_MAIN
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        ))
    }

