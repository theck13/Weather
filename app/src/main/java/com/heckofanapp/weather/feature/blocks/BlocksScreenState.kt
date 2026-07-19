package com.heckofanapp.weather.feature.blocks

import com.heckofanapp.weather.core.model.domain.airquality.AirQuality
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits

data class BlockScreenUiState(
    val air: AirQuality? = null,
    val weather: Weather? = null,
    val units: WeatherUnits = WeatherUnits.getDefault()
)
