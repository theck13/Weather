package com.heckofanapp.weather.core.network.sources.weather.google.json.bundle

import com.heckofanapp.weather.core.network.sources.weather.google.json.GoogleCurrentWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.google.json.GoogleDailyWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.google.json.GoogleHourlyWeatherItemJson

data class GoogleWeatherBundle(
    val current: GoogleCurrentWeatherJson,
    val daily: GoogleDailyWeatherJson,
    val hourly: List<GoogleHourlyWeatherItemJson>,
)
