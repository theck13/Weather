package com.pranshulgg.weather_master_app.core.utils.weather.forecast

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import java.time.Instant

fun findMatchingDaily(
    dailyList: List<WeatherDaily>,
    targetTimeMilli: Long,
    timezone: String,
): WeatherDaily? {
    val targetDate = Instant.ofEpochMilli(targetTimeMilli)
        .atZone(
            safeZoneId(
                id = timezone,
            )
        )
        .toLocalDate()
    return dailyList.firstOrNull { daily ->
        val dailyDate = Instant.ofEpochMilli(daily.time)
            .atZone(
                safeZoneId(
                    id = timezone,
                )
            )
            .toLocalDate()

        targetDate == dailyDate
    }
}

fun findMatchingHourly(
    currentMilli: Long? = 0,
    data: List<WeatherHourly>,
    source: WeatherSource?,
): List<WeatherHourly> {
    val startIndex = data.indexOfFirst { it.time >= (currentMilli ?: 0) }
    if (startIndex == -1) return emptyList()
    return data.drop(maxOf(0, startIndex)).take(source?.hourlyAggregationLimitHours ?: 0)
}

fun findHourlyIndexForTime(
    startMilli: Long = System.currentTimeMillis(),
    time: List<Long>?,
): Int {
    val startIndex = time?.indexOfFirst { it >= startMilli }.takeIf { it != -1 } ?: 0
    return maxOf(0, (startIndex))
}
