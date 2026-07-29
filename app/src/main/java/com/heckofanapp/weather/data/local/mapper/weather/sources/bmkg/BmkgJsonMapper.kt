package com.heckofanapp.weather.data.local.mapper.weather.sources.bmkg

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.WindUnit
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.bmkg.BmkgWeatherConditionMap
import com.heckofanapp.weather.core.network.sources.weather.bmkg.json.BmkgForecastWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.bmkg.json.bundle.BmkgForecastBundle
import com.heckofanapp.weather.core.network.sources.weather.china.ChinaWeatherConditionMap
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.calculations.computeApparentTemperature
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import kotlin.math.roundToInt

fun BmkgForecastBundle.toDomain(
    location: Location,
): Weather {
    val current = this.current.data.weather
    val forecast = this.forecast.data.flatMap { it.weather }.flatten()

    val daily = computeDaily(forecast, location)

    return Weather(
        current = WeatherCurrently(
            cloudCover = null, // NOT USED IN THE APP
            dewPoint = null,
            feelsLike = computeApparentTemperature(
                humidity = current.humidity,
                tempC = current.temperature,
                windMs = WindUnit.KPH.convert(
                    from = current.windSpeed,
                    to = WindUnit.MPS,
                ),
            ),
            humidity = current.humidity ?: 0.0,
            lastUpdatedInMilli = System.currentTimeMillis(),
            pressureMsl = null,
            temperature = current.temperature,
            time = current.datetime.iso8601TimestampToMilliseconds(),
            ultraviolet = null,
            utcOffsetSeconds = null,
            visibility = current.visibility?.roundToInt(),
            weatherCondition = BmkgWeatherConditionMap.getCondition(current.weather),
            windDirection = WindDirection.toWindDirectionFromDegrees(current.windDirection?.toInt()),
            windSpeed = current.windSpeed,
        ),
        daily = daily,
        hourly = forecast.map {
            WeatherHourly(
                dewPoint = null,
                humidity = it.hu,
                precipitationProbability = null,
                pressureMsl = null,
                rain = it.tp ?: 0.0,
                snowfall = null,
                temperature = it.t,
                time = it.datetime.iso8601TimestampToMilliseconds(),
                ultraviolet = null,
                visibility = it.vs?.roundToInt(),
                weatherCondition = BmkgWeatherConditionMap.getCondition(it.weather),
                windDirection = WindDirection.toWindDirectionFromDegrees(it.windDegree?.toInt()),
                windSpeed = it.ws,
            )
        },
        location = location,
    )
}

private fun computeDaily(
    data: List<BmkgForecastWeatherJson>,
    location: Location,
): List<WeatherDaily> {
    val zoneId = location.timezone

    val groupedByDay = data.groupBy {
        it.datetime.iso8601TimestampToMilliseconds().normalizeToDay(zoneId)
    }

    val sunTimings = getSunTimings(
        groupedByDay.map {
            it.key
        },
        location.timezone,
        location.latitude,
        location.longitude,
    )

    val moonTimings = getMoonTimings(
        groupedByDay.map {
            it.key
        },
        location.timezone,
        location.latitude,
        location.longitude,
    )
    val keyIndices = groupedByDay.keys.withIndex().associate { it.value to it.index }

    return groupedByDay.map { dailyIt ->
        val index = keyIndices[dailyIt.key] ?: -1

        val temperatureMinimum = dailyIt.value.minOf { it.t ?: -1.0 }.takeIf { it >= 0.0 }
        val temperatureMaximum = dailyIt.value.maxOf { it.t ?: -1.0 }.takeIf { it >= 0.0 }

        val windSpeed = dailyIt.value.map { it.ws ?: -1.0 }.average().takeIf { it >= 0.0 }
        val windDirection = dailyIt.value.map { it.windDegree ?: -1.0 }.average().takeIf { it >= 0.0 }
        val rainSum = dailyIt.value.maxOf { it.tp ?: -1.0 }.takeIf { it >= 0.0 }

        val weatherConditionAverage = dailyIt.value.map { it.weather.toDouble() }.average()

        val weatherCondition = computeDailyWeatherCondition(
            getHourlyConditionsForDay(data, dailyIt.key),
            BmkgWeatherConditionMap.getCondition(weatherConditionAverage.toInt())
        )

        val visibilityMin = dailyIt.value.minOf { it.vs ?: -1.0 }.takeIf { it >= 0.0 }
        val humidityMin = dailyIt.value.minOf { it.hu ?: -1.0 }.takeIf { it >= 0.0 }

        WeatherDaily(
            dawn = sunTimings[index].dawn ?: -1L,
            dewPoint = null,
            dusk = sunTimings[index].dusk ?: -1L,
            humidity = humidityMin,
            moonIllumination = moonTimings[index].illumination,
            moonPhase = moonTimings[index].phase,
            moonPhaseDaysRemaining = moonTimings[index].daysRemaining,
            moonrise = moonTimings[index].moonrise ?: -1L,
            moonset = moonTimings[index].moonset ?: -1L,
            precipitationProbabilityMax = null,
            pressureMsl = null,
            rainSum = rainSum ?: 0.0,
            snowfallSum = null,
            sunrise = sunTimings[index].sunrise ?: -1L,
            sunset = sunTimings[index].sunset ?: -1L,
            temperatureMaximum = temperatureMaximum,
            temperatureMinimum = temperatureMinimum,
            time = dailyIt.key,
            ultravioletMaximum = null,
            visibility = visibilityMin?.toInt(),
            weatherCondition = weatherCondition,
            windDirection = WindDirection.toWindDirectionFromDegrees(windDirection?.toInt()),
            windSpeed = windSpeed,
        )
    }
}

private fun getHourlyConditionsForDay(
    data: List<BmkgForecastWeatherJson>,
    time: Long,
): List<WeatherCondition> {
    val startIndex =
        data.indexOfFirst { it.datetime.iso8601TimestampToMilliseconds() >= time }
            .takeIf { it != -1 } ?: 0

    val conditions = data.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.BMKG.hourlyAggregationLimitHours)
        .map {
            ChinaWeatherConditionMap.getCondition(it.weather)
        }

    return conditions
}
