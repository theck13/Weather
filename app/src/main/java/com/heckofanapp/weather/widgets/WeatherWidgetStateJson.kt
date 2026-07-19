package com.heckofanapp.weather.widgets

import kotlinx.serialization.Serializable

@Serializable
data class WeatherWidgetStateJson(
    val json: String? = null,
    val config: WidgetConfig = WidgetConfig()
)