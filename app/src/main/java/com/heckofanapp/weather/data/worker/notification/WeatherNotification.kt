package com.heckofanapp.weather.data.worker.notification

import android.Manifest
import android.content.Context
import android.graphics.drawable.Icon
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.heckofanapp.weather.NOTIFICATION_CHANNEL_ID
import com.heckofanapp.weather.NOTIFICATION_CHANNEL_ID_ERROR
import com.heckofanapp.weather.NOTIFICATION_ID
import com.heckofanapp.weather.NOTIFICATION_ID_ERROR
import com.heckofanapp.weather.R

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
        location: String?,
    ) {
        val contentText = location?.let{
            context.resources.getString(
                R.string.notification_updating_location,
                location,
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

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotificationError(
        context: Context,
        message: String?,
    ) {
        val contentText = message?.let{
            context.resources.getString(
                R.string.notification_error_message,
                message,
            )
        } ?: run {
            context.resources.getString(R.string.notification_error)
        }

        val notification = context.let {
            NotificationCompat.Builder(
                it,
                NOTIFICATION_CHANNEL_ID_ERROR,
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
                    NOTIFICATION_ID_ERROR,
                    notification,
                )
        }
    }
}
