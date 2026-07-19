package com.pranshulgg.weather_master_app.data.worker.notification

import android.Manifest
import android.content.Context
import android.graphics.drawable.Icon
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pranshulgg.weather_master_app.NOTIFICATION_CHANNEL_ID
import com.pranshulgg.weather_master_app.NOTIFICATION_ID
import com.pranshulgg.weather_master_app.R

object WeatherNotification {
    fun hideNotification(
        context: Context,
    ) {
        NotificationManagerCompat
            .from(context)
            .cancel(
                NOTIFICATION_ID,
            )
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(
        context: Context,
        locationName: String?,
    ) {
        val contentText = locationName?.let{
            context.resources.getString(
                R.string.notification_updating_location,
                locationName,
            )
        } ?: run {
            context.resources.getString(R.string.notification_updating)
        }

        val notification = context.let {
            NotificationCompat.Builder(
                it,
                NOTIFICATION_CHANNEL_ID,
            )
        }
            .setContentText(contentText)
            .setContentTitle(context.resources.getString(R.string.app_name))
            .setLargeIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(0, 0, true)
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        context.let {
            NotificationManagerCompat.from(it)
                .notify(
                    NOTIFICATION_ID,
                    notification,
                )
        }
    }
}
