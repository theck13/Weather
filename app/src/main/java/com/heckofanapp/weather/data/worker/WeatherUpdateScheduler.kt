package com.heckofanapp.weather.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.heckofanapp.weather.NOTIFICATION_CHANNEL_ID
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import java.util.concurrent.TimeUnit

object WeatherUpdateScheduler {
    fun disableWeatherUpdates(
        context: Context,
    ) {
        WorkManager.getInstance(
            context,
        )
            .cancelUniqueWork(
                uniqueWorkName = NOTIFICATION_CHANNEL_ID,
            )
    }

    fun scheduleWeatherUpdates(
        context: Context,
        repeatInterval: Int,
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                networkType = NetworkType.CONNECTED,
            )
            .build()

        val request =
            PeriodicWorkRequestBuilder<WeatherWorker>(
                repeatInterval = repeatInterval.toLong(),
                repeatIntervalTimeUnit = TimeUnit.MINUTES,
            )
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
            request = request,
            uniqueWorkName = NOTIFICATION_CHANNEL_ID,
        )
    }

    suspend fun updateAllWidgets(
        context: Context,
        data: Weather,
        units: WeatherUnits,
    ) {
        WeatherWorker.updateAllWidgets(
            context = context,
            data = data,
            units = units,
        )
    }
}
