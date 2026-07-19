package com.heckofanapp.weather.data.repository

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.weather.WeatherResult

interface WeatherRepository {
    suspend fun getWeather(
        isForceRefresh: Boolean,
        isManualRefresh: Boolean = false,
        location: Location,
    ): WeatherResult
}
