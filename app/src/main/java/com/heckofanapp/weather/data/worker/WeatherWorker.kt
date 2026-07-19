package com.heckofanapp.weather.data.worker

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.core.model.weather.WeatherResult
import com.heckofanapp.weather.data.provider.WeatherRepositoryProvider
import com.heckofanapp.weather.data.repository.LocationsRepository
import com.heckofanapp.weather.data.repository.WeatherUnitsRepository
import com.heckofanapp.weather.data.worker.notification.WeatherNotification
import com.heckofanapp.weather.data.worker.widgets.WeatherWidgetUpdater
import com.heckofanapp.weather.data.worker.widgets.widgetWeatherMapper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeatherWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repositoryProvider: WeatherRepositoryProvider,
    private val locationsRepository: LocationsRepository,
    private val appVisibility: AppVisibility,
    private val weatherUnitsRepository: WeatherUnitsRepository,
) : CoroutineWorker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        // Only run if app is backgrounded
        if (appVisibility.isForeground) {
            return Result.success()
        }

        return try {
            // Get the locations and units
            val locations = locationsRepository.getLocationsOnce()
            val default = locations.find { it.isDefault }
            val units = weatherUnitsRepository.getUnitsOnce()

            if (default == null || units == null) {
                return Result.success()
            }

            /**
             * Show a notification whenever the worker runs
             * Don't really need it but why not, i wanna know if its working
             */
            WeatherNotification.showNotification(
                context = applicationContext,
                locationName = default.name,
            )

            // Get the repository
            val repo = repositoryProvider.getRepository(default.source)

            val result = repo.getWeather(
                isForceRefresh = false,
                isManualRefresh = true,
                location = default,
            )

            if (result !is WeatherResult.Success) {
                return Result.success()
            }

            val weather = result.weather

            updateAllWidgets(
                context = applicationContext,
                data = weather,
                units = units,
            )

            return Result.success()
        } catch (e: Exception) {
            WeatherNotification.hideNotification(applicationContext)
            Result.failure()
        } finally {
            WeatherNotification.hideNotification(applicationContext)
        }
    }

    companion object {
        suspend fun updateAllWidgets(
            context: Context,
            data: Weather,
            units: WeatherUnits,
        ) {
            WeatherWidgetUpdater(context).update(
                json = widgetWeatherMapper(
                    applicationContext = context,
                    units = units,
                    weather = data,
                ),
            )
        }
    }
}
