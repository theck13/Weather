package com.heckofanapp.weather.core.network.sources.weather.bmkg.json.bundle

import com.heckofanapp.weather.core.network.sources.weather.bmkg.json.BmkgCurrentForecastJson
import com.heckofanapp.weather.core.network.sources.weather.bmkg.json.BmkgForecastJson

data class BmkgForecastBundle(
    val current: BmkgCurrentForecastJson,
    val forecast: BmkgForecastJson,
)
