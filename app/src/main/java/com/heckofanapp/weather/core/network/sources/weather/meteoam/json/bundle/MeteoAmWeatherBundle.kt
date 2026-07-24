package com.heckofanapp.weather.core.network.sources.weather.meteoam.json.bundle

import com.heckofanapp.weather.core.network.sources.weather.meteoam.json.MeteoAmCurrentWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.meteoam.json.MeteoAmForecastWeatherJson

data class MeteoAmWeatherBundle(
    val current: MeteoAmCurrentWeatherJson,
    val forecast: MeteoAmForecastWeatherJson,
)
