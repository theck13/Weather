package com.pranshulgg.weather_master_app.core.model.weather

import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather

sealed class WeatherResult {
    data class Error(
        val cacheWeather: Weather? = null,
        val exception: Exception,
    ) : WeatherResult()
    data class RefreshNotAvailable(
        val cacheWeather: Weather? = null,
    ) : WeatherResult()
    data class Success(
        val weather: Weather,
    ) : WeatherResult()
}

enum class WeatherResultType {
    REFRESH_TOO_EARLY,
    SUCCESS,
    ERROR
}
