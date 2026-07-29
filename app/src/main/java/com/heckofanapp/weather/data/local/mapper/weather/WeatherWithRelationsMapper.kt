package com.heckofanapp.weather.data.local.mapper.weather

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.utils.formatters.safeZoneId
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.data.local.entity.weather.DailyWeatherEntity
import com.heckofanapp.weather.data.local.entity.weather.WeatherWithRelations
import java.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun WeatherWithRelations.toDomain(): Weather {
    val timezone = location.timezone

    // Moon illumination and phase duration are derived from date.
    // They are recomputed here rather than stored.
    val moonTimings = getMoonTimings(
        latitude = location.lat,
        longitude = location.lon,
        timeMilli = daily.map { it.time },
        zoneId = timezone,
    )

    // DROP PAST DAYS
    val todayIndex = getDailyIndexForToday(
        dailyList = daily,
        targetTimeMillis = current?.time ?: System.currentTimeMillis(),
        timezone = timezone,
    ).coerceAtLeast(0)

    return Weather(
        current = WeatherCurrently(
            cloudCover = current?.cloudCover,
            dewPoint = current?.dewPoint,
            feelsLike = current?.feelsLike,
            humidity = current?.humidity ?: 0.0,
            lastUpdatedInMilli = current?.lastUpdatedInMilli ?: -1L,
            pressureMsl = current?.pressureMsl,
            temperature = current?.temperature,
            time = current?.time ?: System.currentTimeMillis(),
            ultraviolet = current?.uvIndex,
            utcOffsetSeconds = current?.utcOffsetSeconds,
            visibility = current?.visibility,
            weatherCondition = current?.weatherCondition ?: WeatherCondition.NO_CONDITION_FOUND,
            windDirection = current?.windDirection,
            windSpeed = current?.windSpeed,
        ),
        daily = List(daily.size) {
            WeatherDaily(
                dawn = daily[it].dawn,
                dewPoint = daily[it].dewPoint,
                dusk = daily[it].dusk,
                humidity = daily[it].humidity,
                moonIllumination = moonTimings[it].illumination,
                moonPhase = daily[it].moonPhase,
                moonPhaseDaysRemaining = moonTimings[it].daysRemaining,
                moonrise = daily[it].moonrise,
                moonset = daily[it].moonset,
                precipitationProbabilityMax = daily[it].precipitationProbabilityMax,
                pressureMsl = daily[it].pressureMsl,
                rainSum = daily[it].rainSum,
                snowfallSum = daily[it].snowfallSum ?: 0.0,
                sunrise = daily[it].sunrise,
                sunset = daily[it].sunset,
                temperatureMaximum = daily[it].temperatureMax,
                temperatureMinimum = daily[it].temperatureMin,
                time = daily[it].time,
                ultravioletMaximum = daily[it].uvIndexMax,
                visibility = daily[it].visibility,
                weatherCondition = daily[it].weatherCondition,
                windDirection = daily[it].windDirection,
                windSpeed = daily[it].windSpeed,
            )
        }.drop(todayIndex),
        hourly = List(hourly.size) {
            WeatherHourly(
                dewPoint = hourly[it].dewPoint,
                humidity = hourly[it].humidity,
                precipitationProbability = hourly[it].precipitationProbability,
                pressureMsl = hourly[it].pressureMsl,
                rain = hourly[it].rain,
                snowfall = hourly[it].snowfall ?: 0.0,
                temperature = hourly[it].temperature,
                time = hourly[it].time,
                ultraviolet = hourly[it].uvIndex,
                visibility = hourly[it].visibility,
                weatherCondition = hourly[it].weatherCondition,
                windDirection = hourly[it].windDirection,
                windSpeed = hourly[it].windSpeed,
            )
        },
        location = Location(
            country = location.country,
            countryCode = location.countryCode,
            id = location.id,
            isDefault = location.isDefault,
            isFavorite = location.isFavorite,
            isPinned = location.isPinned,
            latitude = location.lat,
            longitude = location.lon,
            name = location.name,
            source = location.source,
            state = location.state ?: "",
            timezone = location.timezone,
        ),
    )
}

private fun getDailyIndexForToday(
    dailyList: List<DailyWeatherEntity>,
    targetTimeMillis: Long,
    timezone: String,
): Int {
    val zoneId = safeZoneId(
        id = timezone,
    )

    val targetDate = Instant.ofEpochMilli(targetTimeMillis)
        .atZone(zoneId)
        .toLocalDate()

    return dailyList.indexOfFirst { daily ->
        Instant.ofEpochMilli(daily.time)
            .atZone(zoneId)
            .toLocalDate() == targetDate
    }.coerceAtLeast(0)
}
