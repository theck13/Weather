package com.heckofanapp.weather.feature.settings.background

import android.content.Context
import androidx.lifecycle.ViewModel
import com.heckofanapp.weather.data.worker.WeatherUpdateScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class BackgroundUpdatesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {
    fun disableWeatherUpdates() {
        WeatherUpdateScheduler.disableWeatherUpdates(
            context = context,
        )
    }

    fun scheduleWeatherUpdates(
        minutes: Int,
    ) {
        WeatherUpdateScheduler.scheduleWeatherUpdates(
            context = context,
            repeatInterval = minutes,
        )
    }
}
