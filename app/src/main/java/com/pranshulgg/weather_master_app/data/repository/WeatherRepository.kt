package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult

interface WeatherRepository {
    suspend fun getWeather(
        isForceRefresh: Boolean,
        isManualRefresh: Boolean = false,
        location: Location,
    ): WeatherResult
}
