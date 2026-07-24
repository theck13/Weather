package com.heckofanapp.weather.core.network.sources.weather.accu.json.bundle

import com.heckofanapp.weather.core.network.sources.weather.accu.json.AccuCurrentWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.accu.json.AccuDailyWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.accu.json.AccuHourlyWeatherJson

data class AccuWeatherBundle(
    val current: AccuCurrentWeatherJson,
    val daily: AccuDailyWeatherJson,
    val hourly: List<AccuHourlyWeatherJson>,
)
