package com.heckofanapp.weather.data.worker.widgets

import android.content.Context
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.core.model.weather.TemperatureUnit
import com.heckofanapp.weather.core.model.weather.toFroggy
import com.heckofanapp.weather.core.model.weather.toIcon
import com.heckofanapp.weather.core.model.weather.toLabel
import com.heckofanapp.weather.core.prefs.PreferencesHelper
import com.heckofanapp.weather.core.utils.formatters.to12HourTimeString
import com.heckofanapp.weather.core.utils.formatters.to24HourTimeString
import com.heckofanapp.weather.core.utils.formatters.toWeekdayString
import com.heckofanapp.weather.core.utils.weather.computing.summary.computeDaySummary
import com.heckofanapp.weather.core.utils.weather.forecast.findHourlyIndexForTime
import com.heckofanapp.weather.core.utils.weather.forecast.findMatchingDaily
import com.heckofanapp.weather.widgets.model.WidgetDailyItem
import com.heckofanapp.weather.widgets.model.WidgetHourlyItem
import com.heckofanapp.weather.widgets.model.WidgetWeather
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt

fun widgetWeatherMapper(
    applicationContext: Context,
    units: WeatherUnits,
    weather: Weather,
): String {
    val currentCondition = weather.current.weatherCondition.toLabel(
        context = applicationContext,
    )
    val currentFrogIcon = weather.current.weatherCondition.toFroggy(
        daily = weather.daily.firstOrNull(),
        targetTimeMilli = weather.current.time,
    )
    val currentIcon = weather.current.weatherCondition.toIcon(
        daily = weather.daily.firstOrNull(),
        targetTimeMilli = weather.current.time,
    )
    val currentTemperature = TemperatureUnit.CELSIUS.convert(
        from = weather.current.temperature,
        to = units.temperature,
    )?.roundToInt()
    val daySummary = computeDaySummary(
        context = applicationContext,
        dailyIndex = 0,
        units = units,
        weather = weather,
    )
    val hourlyStartIndex = findHourlyIndexForTime(
        time = weather.hourly.map { it.time },
    )
    val is24hr = PreferencesHelper.getString("app_time") == "24"
    val timezone = weather.location.timezone

    val daily = weather.daily
        .take(6)
        .map {
            val maxTemperature = TemperatureUnit.CELSIUS.convert(
                from = it.temperatureMax,
                to = units.temperature,
            )?.roundToInt()
            val minTemperature = TemperatureUnit.CELSIUS.convert(
                from = it.temperatureMin,
                to = units.temperature,
            )?.roundToInt()
            val icon = it.weatherCondition.toIcon(
                targetTimeMilli = System.currentTimeMillis(),
            )

            WidgetDailyItem(
                icon = icon,
                temperatureMaximum = "${maxTemperature}°",
                temperatureMinimum = "${minTemperature}°",
                time = toWeekdayString(
                    timeMilli = it.time,
                    zoneId = timezone,
                ),
            )
        }
    val hourly = weather.hourly
        .drop(hourlyStartIndex)
        .take(12)
        .map {
            val daily = findMatchingDaily(
                dailyList = weather.daily,
                targetTimeMilli = it.time,
                timezone = weather.location.timezone,
            )
            val icon = it.weatherCondition.toIcon(
                daily = daily,
                targetTimeMilli = it.time,
            )
            val temperature =
                TemperatureUnit.CELSIUS.convert(
                    from = it.temperature,
                    to = units.temperature,
                )?.roundToInt()
            val formattedTime =
                if (is24hr) to24HourTimeString(
                    timeMilli = it.time,
                    zoneId = timezone,
                ) else to12HourTimeString(
                    timeMilli = it.time,
                    zoneId = timezone,
                )

            WidgetHourlyItem(
                icon = icon,
                temperature = "${temperature}°",
                time = formattedTime,
            )
        }

    val widgetState = WidgetWeather(
        currentCondition = currentCondition,
        currentFrog = currentFrogIcon,
        currentIcon = currentIcon,
        currentTemp = "${currentTemperature}°",
        daily = daily,
        hourly = hourly,
        locationName = weather.location.name,
        summary = daySummary,
    )

    return Json.encodeToString(widgetState)
}
