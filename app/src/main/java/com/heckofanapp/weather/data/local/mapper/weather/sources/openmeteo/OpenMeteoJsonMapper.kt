package com.heckofanapp.weather.data.local.mapper.weather.sources.openmeteo

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.openmeteo.OpenMeteoWeatherConditionMap
import com.heckofanapp.weather.core.network.sources.weather.openmeteo.json.OpenMeteoHourlyForecastJson
import com.heckofanapp.weather.core.network.sources.weather.openmeteo.json.OpenMeteoWeatherJson
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import com.heckofanapp.weather.core.utils.weather.forecast.findHourlyIndexForTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun OpenMeteoWeatherJson.toDomain(
    location: Location,
): Weather {
    val sunTimings = getSunTimings(
        daily.time.map {
            it.secondsToMilliseconds()
        }, // Open-Meteo returns in seconds
        location.timezone,
        location.latitude,
        location.longitude,
    )

    val moonTimings = getMoonTimings(
        daily.time.map {
            it.secondsToMilliseconds()
        }, // Open-Meteo returns in seconds
        location.timezone,
        location.latitude,
        location.longitude,
    )

    val currentHourIndex = findHourlyIndexForTime(
        time = hourly.time.map { it.secondsToMilliseconds() },
    )

    return Weather(
        current = WeatherCurrently(
            cloudCover = current.cloudCover,
            dewPoint = hourly.dewPoint[currentHourIndex],
            feelsLike = current.feelsLike,
            humidity = current.relativeHumidity,
            lastUpdatedInMilli = System.currentTimeMillis(),
            pressureMsl = current.pressureMsl,
            temperature = current.temperature,
            time = current.time.secondsToMilliseconds(), // Open-Meteo returns in seconds
            ultraviolet = current.uvIndex,
            utcOffsetSeconds = utcOffsetSeconds,
            visibility = hourly.visibility[currentHourIndex],
            weatherCondition = OpenMeteoWeatherConditionMap.getCondition(current.weatherCode),
            windDirection = WindDirection.toWindDirectionFromDegrees(current.windDirection),
            windSpeed = current.windSpeed,
        ),
        daily = List(daily.time.size) {
            val meanCondition = getHourlyConditionsForDay(
                hourly,
                daily.time[it]
            ).groupingBy { condition -> condition }
                .eachCount().entries.maxByOrNull { map -> map.key }?.key

            val condition = computeDailyWeatherCondition(
                getHourlyConditionsForDay(hourly, daily.time[it]),
                meanCondition ?: OpenMeteoWeatherConditionMap.getCondition(daily.weatherCode[it])
            )

            WeatherDaily(
                dawn = sunTimings[it].dawn ?: 0L,
                dewPoint = daily.dewPoint[it],
                dusk = sunTimings[it].dusk ?: 0L,
                humidity = daily.humidity[it]?.toDouble(),
                moonPhase = moonTimings[it].phase,
                moonrise = moonTimings[it].moonrise ?: -0L,
                moonset = moonTimings[it].moonset ?: -0L,
                precipitationProbabilityMax = daily.precipitationProbabilityMax[it],
                pressureMsl = daily.pressureMsl[it],
                rainSum = daily.rainSum[it],
                snowfallSum = daily.snowfallSum[it],
                sunrise = sunTimings[it].sunrise ?: -0L,
                sunset = sunTimings[it].sunset ?: -0L,
                temperatureMaximum = daily.temperatureMax[it],
                temperatureMinimum = daily.temperatureMin[it],
                time = daily.time[it].secondsToMilliseconds(), // Open-Meteo returns in seconds
                ultravioletMaximum = daily.uvIndexMax[it],
                visibility = daily.visibility[it],
                weatherCondition = condition,
                windDirection = WindDirection.toWindDirectionFromDegrees(daily.windDirectionDominant[it]),
                windSpeed = daily.windSpeedMean[it],
            )
        },
        hourly = List(hourly.time.size) {
            WeatherHourly(
                dewPoint = hourly.dewPoint[it],
                humidity = hourly.relativeHumidity[it],
                precipitationProbability = hourly.precipitationProbability[it],
                pressureMsl = hourly.pressureMsl[it],
                rain = hourly.rain[it],
                snowfall = hourly.snowfall[it],
                temperature = hourly.temperature[it],
                time = hourly.time[it].secondsToMilliseconds(), // Open-Meteo returns in seconds
                ultraviolet = hourly.uvIndex[it],
                visibility = hourly.visibility[it],
                weatherCondition = OpenMeteoWeatherConditionMap.getCondition(hourly.weatherCode[it]),
                windDirection = WindDirection.toWindDirectionFromDegrees(hourly.windDirection[it]),
                windSpeed = hourly.windSpeed[it],
            )
        },
        location = location,
    )
}

private fun getHourlyConditionsForDay(
    data: OpenMeteoHourlyForecastJson,
    time: Long,
): List<WeatherCondition> {
    val startIndex =
        data.time.indexOfFirst { it.secondsToMilliseconds() >= time.secondsToMilliseconds() }
            .takeIf { it != -1 } ?: 0

    val conditions = data.weatherCode.drop(maxOf(0, startIndex)).take(24)
        .map { OpenMeteoWeatherConditionMap.getCondition(it) }

    return conditions
}
