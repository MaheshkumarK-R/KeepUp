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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class AppAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val DONE = "done"
        const val REJECT = "reject"
        const val REMAINDER = "remainder"
        const val NOTIFICATION_CODE = 402
        private const val DONE_CODE = 302

        private fun Context.buildNotification(): Notification {
            val donePendingIntent = PendingIntent.getBroadcast(
                this,
                DONE_CODE,
                Intent(this, AppAlarmReceiver::class.java).apply {
                    action = DONE
                    putExtra(REMAINDER, DONE)
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val contentPendingIntent = PendingIntent.getActivity(
                this,
                987,
                Intent(this, MainActivity::class.java).apply {
                    action = Intent.ACTION_MAIN
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
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
    }

    private val TAG = "AppAlarmReceiver_TAG"
    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context, intent: Intent) {

        try {
            Log.d(TAG, "onReceive: ${intent.action}")
            if (intent.hasExtra(REMAINDER)) {

                Log.d(TAG, "onReceive: ${intent.action} >> ${intent.hasExtra(REMAINDER)}")
                when (intent.action ?: "NA") {
                    DONE -> {
                        runBlocking {
                            Log.d(TAG, "onReceive:DONE  ${mediaPlayer?.isPlaying}")
                            mediaPlayer?.stop().also {
                                mediaPlayer = null
                            }
                            NotificationManagerCompat.from(context).cancel(NOTIFICATION_CODE)
                        }
                    }

                    else -> {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            NotificationManagerCompat.from(context)
                                .notify(NOTIFICATION_CODE, context.buildNotification())
                            if(mediaPlayer != null)
                                mediaPlayer = MediaPlayer.create(context, R.raw.alarm_music)
                            mediaPlayer?.let { player ->
                                player.setOnCompletionListener {
                                    it.release()
                                }
                                player.start()
                            }
                        }
                    }
                }

            }
        }catch (exception:Exception){
            Log.d(TAG, "onReceive: ${exception.localizedMessage}")
            exception.printStackTrace()
        }
    }
}