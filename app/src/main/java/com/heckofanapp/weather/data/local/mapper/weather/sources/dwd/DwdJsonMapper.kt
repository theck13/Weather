package com.heckofanapp.weather.data.local.mapper.weather.sources.dwd

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.dwd.DwdWeatherConditionMap
import com.heckofanapp.weather.core.network.sources.weather.dwd.json.DwdWeatherForecastDataJson
import com.heckofanapp.weather.core.network.sources.weather.dwd.json.bundle.DwdWeatherJsonBundle
import com.heckofanapp.weather.core.network.sources.weather.metnorway.MetNorwayWeatherConditionMap
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.calculations.computeApparentTemperature
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import kotlin.math.roundToInt

// ---------------------------- JSON TO DOMAIN ----------------------------

fun DwdWeatherJsonBundle.toDomain(
    location: Location,
): Weather {
    val current = this.current.weather
    val forecast = this.forecastJson.weather
    val daily = computeDaily(forecast, location)

    return Weather(
        location = location,
        current = WeatherCurrently(
            temperature = current.temperature,
            humidity = current.humidity?.toDouble() ?: 0.0,
            windSpeed = current.windSpeed,
            windDirection = WindDirection.toWindDirectionFromDegrees(current.windDirection),
            pressureMsl = current.pressureMsl,
            visibility = current.visibility,
            cloudCover = null, // NOT USED IN THE APP
            ultraviolet = null,
            weatherCondition = DwdWeatherConditionMap.getCondition(current.icon),
            feelsLike = computeApparentTemperature(
                tempC = current.temperature,
                humidity = current.humidity?.toDouble(),
                windMs = current.windSpeed?.kmhToMs()
            ),
            time = current.timestamp.iso8601TimestampToMilliseconds(),
            dewPoint = current.dewPoint,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis(),
        ),
        hourly = forecast.map {
            WeatherHourly(
                temperature = it.temperature,
                windSpeed = it.windSpeed,
                windDirection = WindDirection.toWindDirectionFromDegrees(it.windDirection),
                rain = it.precipitation ?: 0.0,
                snowfall = null,
                ultraviolet = null,
                pressureMsl = it.pressureMsl,
                visibility = it.visibility,
                humidity = it.humidity?.toDouble(),
                dewPoint = it.dewPoint,
                weatherCondition = DwdWeatherConditionMap.getCondition(it.icon),
                time = it.timestamp.iso8601TimestampToMilliseconds(),
                precipitationProbability = it.precipitationProbability,
            )
        },
        daily = daily,
    )
}

private fun computeDaily(
    data: List<DwdWeatherForecastDataJson>,
    location: Location,
): List<WeatherDaily> {
    val zoneId = location.timezone

    val groupedByDay = data.groupBy {
        it.timestamp.iso8601TimestampToMilliseconds().normalizeToDay(zoneId)
    }

    val sunTimings = getSunTimings(
        timeMilli = groupedByDay.map {
            it.key
        },
        zoneId = location.timezone,
        latitude = location.latitude,
        longitude = location.longitude,
    )

    val moonTimings = getMoonTimings(
        timeMilli = groupedByDay.map {
            it.key
        },
        zoneId = location.timezone,
        latitude = location.latitude,
        longitude = location.longitude,
    )

    return groupedByDay.filter {(key, value) -> (value.size == 24) || key == groupedByDay.keys.firstOrNull()}.map{ dailyIt ->
        val minTemperature = dailyIt.value.minOf { it.temperature }
        val maxTemperature = dailyIt.value.maxOf { it.temperature }

        val avgHumidity = dailyIt.value.map { it.humidity?.toDouble() ?: -1.0 }.average()

        val avgPressure = dailyIt.value.map { it.pressureMsl ?: -1.0 }.average()

        val minVisibility = dailyIt.value.minOf { it.visibility?.toDouble() ?: -1.0 }

        val avgDewPoint = dailyIt.value.map { it.dewPoint ?: -1.0 }.average()

        val windSpeed = dailyIt.value
            .mapNotNull { it.windSpeed }
            .average() ?: null

        val windDirection =
            dailyIt.value.mapNotNull { it.windDirection }.maxOrNull()

        val rainSum =
            dailyIt.value.sumOf { it.precipitation ?: 0.0 }

        val time = dailyIt.key
        val icon = dailyIt.value.map { it.icon }.groupingBy { it }
            .eachCount().entries.maxByOrNull { it.value }

        val condition = computeDailyWeatherCondition(
            getHourlyConditionsForDay(dailyIt.value, time),
            MetNorwayWeatherConditionMap.getCondition(icon?.key)
        )

        val precipitationProbabilityMax = dailyIt.value.mapNotNull { it.precipitationProbability }
            .maxOrNull()

        val index = groupedByDay.keys.indexOf(dailyIt.key)

        WeatherDaily(
            temperatureMin = minTemperature,
            temperatureMax = maxTemperature,
            windSpeed = windSpeed,
            windDirection = WindDirection.toWindDirectionFromDegrees(windDirection),
            rainSum = rainSum,
            snowfallSum = null,
            ultravioletMaximum = null,
            weatherCondition = condition,
            time = time,
            precipitationProbabilityMax = precipitationProbabilityMax,
            sunrise = sunTimings[index].sunrise ?: -0L,
            sunset = sunTimings[index].sunset ?: -0L,
            moonrise = moonTimings[index].moonrise ?: -0L,
            moonset = moonTimings[index].moonset ?: -0L,
            moonPhase = moonTimings[index].phase,
            dawn = sunTimings[index].dawn ?: 0L,
            dusk = sunTimings[index].dusk ?: 0L,
            humidity = avgHumidity,
            pressureMsl = avgPressure,
            visibility = minVisibility.roundToInt(),
            dewPoint = avgDewPoint,
        )
    }
}

private fun getHourlyConditionsForDay(
    data: List<DwdWeatherForecastDataJson>,
    time: Long,
): List<WeatherCondition> {
    val startIndex =
        data.indexOfFirst { it.timestamp.iso8601TimestampToMilliseconds() >= time }
            .takeIf { it != -1 } ?: 0

    val conditions = data.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.DWD.hourlyAggregationLimitHours)
        .map {
            DwdWeatherConditionMap.getCondition(it.icon)
        }

    return conditions
}

private fun Double.kmhToMs(): Double {
    return (this / 3.6)
}
