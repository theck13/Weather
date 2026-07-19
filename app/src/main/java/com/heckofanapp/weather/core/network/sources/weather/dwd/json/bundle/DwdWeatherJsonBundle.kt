package com.heckofanapp.weather.core.network.sources.weather.dwd.json.bundle

import com.heckofanapp.weather.core.network.sources.weather.dwd.json.DwdCurrentWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.dwd.json.DwdWeatherForecastJson

data class DwdWeatherJsonBundle(
    val current: DwdCurrentWeatherJson,
    val forecastJson: DwdWeatherForecastJson
)