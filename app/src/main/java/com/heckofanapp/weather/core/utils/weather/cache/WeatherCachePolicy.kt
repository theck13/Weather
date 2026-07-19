package com.heckofanapp.weather.core.utils.weather.cache

import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.weather.WeatherResultType
import com.heckofanapp.weather.core.utils.weather.cache.CacheConfig.AUTO_REFRESH_MAX_MINUTES
import com.heckofanapp.weather.core.utils.weather.cache.CacheConfig.MANUAL_REFRESH_MINUTES
import com.heckofanapp.weather.data.local.entity.weather.WeatherWithRelations
import java.util.concurrent.TimeUnit

fun isWeatherCacheSafe(
    cache: WeatherWithRelations?,
): Boolean {
    return cache != null && cache.daily.isNotEmpty() && cache.hourly.isNotEmpty() && cache.current != null
}

fun isWeatherDailyDomainSafe(
    weather: Weather?,
): Boolean {
    return weather != null && weather.daily.isNotEmpty()
}

fun isWeatherDomainSafe(
    weather: Weather?,
): Boolean {
    return weather != null && weather.daily.isNotEmpty() && weather.hourly.isNotEmpty()
}

fun isWeatherHourlyDomainSafe(
    weather: Weather?,
): Boolean {
    return weather != null && weather.hourly.isNotEmpty()
}

fun shouldReturnWeatherCache(
    cache: WeatherWithRelations?,
    isManualRefresh: Boolean,
    isForceRefresh: Boolean
): WeatherResultType {
    if (cache == null || cache.current == null || isForceRefresh || isWeatherCacheSafe(cache).not()) {
        return WeatherResultType.ERROR
    }

    val cacheMilli = cache.current.lastUpdatedInMilli
    val ageMillis = System.currentTimeMillis() - cacheMilli
    val ageMinutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)

    val tooEarly = isManualRefresh && ageMinutes < MANUAL_REFRESH_MINUTES
    val maxAge = if (isManualRefresh) MANUAL_REFRESH_MINUTES else AUTO_REFRESH_MAX_MINUTES

    if (tooEarly) return WeatherResultType.REFRESH_TOO_EARLY

    val shouldReturnCache = ageMinutes < maxAge

    return if (shouldReturnCache) WeatherResultType.SUCCESS else WeatherResultType.ERROR
}
