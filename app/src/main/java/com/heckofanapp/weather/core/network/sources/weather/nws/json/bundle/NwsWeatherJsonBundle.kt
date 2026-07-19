package com.heckofanapp.weather.core.network.sources.weather.nws.json.bundle

import com.heckofanapp.weather.core.network.sources.weather.nws.json.NwsCurrentForecastJson
import com.heckofanapp.weather.core.network.sources.weather.nws.json.NwsForecastJson
import com.heckofanapp.weather.core.network.sources.weather.nws.json.NwsGridPointDataJson
import com.heckofanapp.weather.core.network.sources.weather.nws.json.NwsHourlyForecastJson

data class NwsWeatherJsonBundle(
    val forecast: NwsForecastJson,
    val current: NwsCurrentForecastJson,
    val hourly: NwsHourlyForecastJson,
    val gridPointsData: NwsGridPointDataJson
)