package com.wadhwaniai.taskify.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.wadhwaniai.taskify.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class NotificationHelper @Inject constructor(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "TASKIFY_ID"
        const val CHANNEL_NAME = "TASKIFY"
    }

    private fun getNotificationId() = 0

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        setNotificationChannel()
    }

    private fun setNotificationChannel() {
        NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW).let { channel ->
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showSilentNotification(title: String, description: String, pendingIntent: PendingIntent) {
        val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.taskify_logo_round)
                .setSound(null)
                .build()
        notificationManager.notify(getNotificationId(), notification)
    }

    fun showCompletedNotification(
        title: String,
        description: String,
        pendingIntent: PendingIntent
    ) {
        val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.taskify_logo_round)
                .build()
        notificationManager.notify(getNotificationId(), notification)
    }
}
