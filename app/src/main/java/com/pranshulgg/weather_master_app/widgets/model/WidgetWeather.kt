package com.pranshulgg.weather_master_app.widgets.model

import kotlinx.serialization.Serializable

@Serializable
data class WidgetWeather(
    val currentCondition: String,
    val currentFrog: Int,
    val currentIcon: Int,
    val currentTemp: String,
    val daily: List<WidgetDailyItem>,
    val hourly: List<WidgetHourlyItem>,
    val locationName: String,
    val summary: String,
)

@Serializable
data class WidgetHourlyItem(
    val icon: Int,
    val temperature: String,
    val time: String,
)

@Serializable
data class WidgetDailyItem(
    val icon: Int,
    val temperatureMaximum: String,
    val temperatureMinimum: String,
    val time: String,
)
