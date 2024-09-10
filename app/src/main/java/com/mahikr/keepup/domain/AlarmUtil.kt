package com.mahikr.keepup.domain

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

const val REMAINDER = "remainder"
private const val TAG = "AlarmUtil_TAG"

fun Context.setUpAlarm(timeInMillis:Long) {

    val intent = Intent(this, AppAlarmReceiver::class.java).apply {
        putExtra(REMAINDER, "ONE_TIME_ALARM")
    }

    val formatTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
    Log.d(TAG, "setupPeriodicAlarm: ${intent.hasExtra(REMAINDER)} to $timeInMillis  ${formatTime.format(timeInMillis)}")

    val pendingIntent = PendingIntent.getBroadcast(
        this,
        301,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    try {
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }catch (exception:SecurityException){
        exception.printStackTrace()
    }

}


fun Context.cancelAlarm() {

    val intent = Intent(this, AppAlarmReceiver::class.java).apply {
        putExtra(REMAINDER, "CANCEL_ALARM")
    }
    Log.d(TAG, "cancelAlarm: ${intent.hasExtra(REMAINDER)}")
    val pendingIntent = PendingIntent.getBroadcast(
        this,
        301,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    try {
        alarmMgr.cancel(pendingIntent)
    }catch (exception:SecurityException){
        exception.printStackTrace()
    }

}


fun Context.setupPeriodicAlarm(timeInMillis:Long) {

    val intent = Intent(this, AppAlarmReceiver::class.java).apply {
        putExtra(REMAINDER, "SCHEDULE_ALARM")
    }
    val formatTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
    Log.d(TAG, "setupPeriodicAlarm: ${intent.hasExtra(REMAINDER)} to $timeInMillis  ${formatTime.format(timeInMillis)}")
    val pendingIntent = PendingIntent.getBroadcast(
        this,
        301,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    try {
        val interval = 2L * 60L * 1000L
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis,interval,pendingIntent)
    }catch (exception:SecurityException){
        exception.printStackTrace()
    }

}