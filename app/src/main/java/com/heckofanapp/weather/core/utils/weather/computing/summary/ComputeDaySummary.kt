package com.heckofanapp.weather.core.utils.weather.computing.summary

import android.content.Context
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.core.model.weather.toLabel
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import com.heckofanapp.weather.core.utils.weather.forecast.findMatchingHourly

fun computeDaySummary(
    context: Context,
    dailyIndex: Int = 0,
    units: WeatherUnits,
    weather: Weather,
): String {
    val hourly = findMatchingHourly(
        currentMilli = weather.daily[dailyIndex].time,
        data = weather.hourly,
        source = weather.location.source,
    )

    if (hourly.isEmpty()) {
        return context.getString(R.string.weather_no_data)
    }

    val rain = findRainStarting(hourly)
    val snow = findSnowStarting(hourly)
    val peakUv = hourly.maxBy { it.ultraviolet ?: 0.0 }
    val maxTemp = hourly.maxOf { it.temperature ?: 0.0 }
    val minTemp = hourly.minOf { it.temperature ?: 0.0 }
    val avgTemp = hourly.map { it.temperature ?: 0.0 }.average()
    val avgCondition = hourly.map { it.weatherCondition }.groupingBy { it }.eachCount().entries.maxByOrNull { it.key }?.key
    val condition = computeDailyWeatherCondition(hourly.map { it.weatherCondition }, avgCondition!!).toLabel(context)

    return getHeadline(
        context = context,
        summaryData = SummaryData(
            condition = condition,
            rain = rain,
            snow = snow,
            temperatures = SummaryTemperatures(
                average = avgTemp,
                temperatureMaximum = maxTemp,
                temperatureMinimum = minTemp,
            ),
            uv = SummaryPeakUv(
                at = peakUv.time,
                uv = peakUv.ultraviolet ?: 0.0,
            ),
        ),
        units = units,
        zoneId = weather.location.timezone,
    )
}

private fun findRainStarting(
    hourly: List<WeatherHourly>,
): SummaryPeakRain {
    val rainStartIndex = hourly.indexOfFirst { it.rain > 0.0 }.plus(1).coerceIn(0, hourly.size - 1)
    val data = hourly[rainStartIndex]
    return SummaryPeakRain(
        amount = data.rain,
        at = data.time,
        probability = data.precipitationProbability ?: 0,
    )
}

private fun findSnowStarting(
    hourly: List<WeatherHourly>,
): SummaryPeakSnow {
    val snowStartIndex = hourly.indexOfFirst { (it.snowfall ?: 0.0) > 0.0 }.plus(1).coerceIn(0, hourly.size - 1)
    val data = hourly[snowStartIndex]
    return SummaryPeakSnow(
        amount = data.snowfall ?: 0.0,
        at = data.time,
        probability = data.precipitationProbability ?: 0,
    )
}
