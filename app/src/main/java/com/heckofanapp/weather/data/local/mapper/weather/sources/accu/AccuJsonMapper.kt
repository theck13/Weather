package com.heckofanapp.weather.data.local.mapper.weather.sources.accu

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.weather.DistanceUnit
import com.heckofanapp.weather.core.model.weather.PrecipitationUnit
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.accu.AccuWeatherConditionMap
import com.heckofanapp.weather.core.network.sources.weather.accu.json.bundle.AccuWeatherBundle
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import kotlin.math.roundToInt

fun AccuWeatherBundle.toDomain(
    location: Location,
): Weather {
    val current = this.current
    val daily = this.daily.daily
    val hourly = this.hourly
    val moonTimings = getMoonTimings(
        daily.map {
            it.time.secondsToMilliseconds().normalizeToDay(location.timezone)
        }, // Open-Meteo returns in seconds.
        location.timezone,
        location.latitude,
        location.longitude
    )
    val sunTimings = getSunTimings(
        daily.map {
            it.time.secondsToMilliseconds().normalizeToDay(location.timezone)
        }, // Open-Meteo returns in seconds.
        location.timezone,
        location.latitude,
        location.longitude
    )

    return Weather(
        location = location,
        current = WeatherCurrently(
            cloudCover = null, // NOT USED IN THE APP
            dewPoint = current.dewPoint.metric.value,
            feelsLike = current.temperatureFeels.metric.value,
            humidity = current.humidity?.toDouble() ?: 0.0,
            lastUpdatedInMilli = System.currentTimeMillis(),
            pressureMsl = current.pressure.metric.value,
            temperature = current.temperature.metric.value,
            time = current.time.secondsToMilliseconds(),
            ultraviolet = current.ultraviolet,
            utcOffsetSeconds = null,
            visibility = DistanceUnit.KM.convert(current.visibility.metric.value, DistanceUnit.M)?.roundToInt(),
            weatherCondition = AccuWeatherConditionMap.getCondition(current.icon),
            windDirection = WindDirection.toWindDirectionFromDegrees(current.wind.direction.degrees),
            windSpeed = current.wind.speed.metric.value,
        ),
        hourly = hourly.map { hour ->
            WeatherHourly(
                dewPoint = hour.dewPoint.value,
                humidity = hour.humidity?.toDouble(),
                precipitationProbability = hour.precipitation,
                pressureMsl = null,
                rain = hour.rain.value ?: 0.0,
                snowfall = PrecipitationUnit.CM.convert(hour.snowCm.value, PrecipitationUnit.MM),
                temperature = hour.temperature.value,
                time = hour.time.secondsToMilliseconds(),
                ultraviolet = hour.ultraviolet,
                visibility = DistanceUnit.KM.convert(hour.visibility.value, DistanceUnit.M)?.roundToInt(),
                weatherCondition = AccuWeatherConditionMap.getCondition(hour.icon),
                windDirection = WindDirection.toWindDirectionFromDegrees(hour.wind.direction.degrees),
                windSpeed = hour.wind.speed.value,
            )
        },
        daily = daily.mapIndexed { index, item ->

            val windSpeed = listOf(
                item.day.wind.speed.value ?: 0.0,
                item.night.wind.speed.value ?: 0.0
            ).average()

            val windDirection = listOf(
                item.day.wind.direction.degrees ?: 0.0,
                item.night.wind.direction.degrees ?: 0.0
            ).average()

            val rain = listOf(item.day.rain.value ?: 0.0, item.night.rain.value ?: 0.0).sum()
            val snow = listOf(item.day.snowCm.value ?: 0.0, item.night.snowCm.value ?: 0.0).sum()

            val condition =
                computeDailyWeatherCondition(
                    List(12) { AccuWeatherConditionMap.getCondition(item.day.icon) } + AccuWeatherConditionMap.getCondition(
                        item.night.icon
                    ),
                    WeatherCondition.NO_CONDITION_FOUND,
                )

            val precipitationProbabilityMax = listOfNotNull(
                item.day.precipitation,
                item.night.precipitation,
            ).maxOrNull()

            val humidity = listOf(
                item.day.humidity.value?.toDouble() ?: 0.0,
                item.night.humidity.value?.toDouble() ?: 0.0,
            ).average()

            WeatherDaily(
                dawn = sunTimings[index].dawn ?: -0L,
                dewPoint = null,
                dusk = sunTimings[index].dusk ?: -0L,
                humidity = humidity,
                moonPhase = moonTimings[index].phase,
                moonrise = moonTimings[index].moonrise ?: -0L,
                moonset = moonTimings[index].moonset ?: -0L,
                precipitationProbabilityMax = precipitationProbabilityMax,
                pressureMsl = null,
                rainSum = rain,
                snowfallSum = PrecipitationUnit.CM.convert(snow, PrecipitationUnit.MM),
                sunrise = sunTimings[index].sunrise ?: -0L,
                sunset = sunTimings[index].sunset ?: -0L,
                temperatureMax = item.temperature.maximum.value,
                temperatureMin = item.temperature.minimum.value,
                time = item.time.secondsToMilliseconds().normalizeToDay(location.timezone),
                ultravioletMaximum = item.day.ultraviolet.value,
                visibility = null,
                weatherCondition = condition,
                windDirection = WindDirection.toWindDirectionFromDegrees(windDirection.roundToInt()),
                windSpeed = windSpeed,
            )
        },
    )
}
