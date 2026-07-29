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

fun DwdWeatherJsonBundle.toDomain(
    location: Location,
): Weather {
    val current = this.current.weather
    val forecast = this.forecastJson.weather
    val daily = computeDaily(forecast, location)

    return Weather(
        current = WeatherCurrently(
            cloudCover = null, // NOT USED IN THE APP
            dewPoint = current.dewPoint,
            feelsLike = computeApparentTemperature(
                humidity = current.humidity?.toDouble(),
                tempC = current.temperature,
                windMs = current.windSpeed?.kmhToMs(),
            ),
            humidity = current.humidity?.toDouble() ?: 0.0,
            lastUpdatedInMilli = System.currentTimeMillis(),
            pressureMsl = current.pressureMsl,
            temperature = current.temperature,
            time = current.timestamp.iso8601TimestampToMilliseconds(),
            ultraviolet = null,
            utcOffsetSeconds = null,
            visibility = current.visibility,
            weatherCondition = DwdWeatherConditionMap.getCondition(current.icon),
            windDirection = WindDirection.toWindDirectionFromDegrees(current.windDirection),
            windSpeed = current.windSpeed,
        ),
        daily = daily,
        hourly = forecast.map {
            WeatherHourly(
                dewPoint = it.dewPoint,
                humidity = it.humidity?.toDouble(),
                precipitationProbability = it.precipitationProbability,
                pressureMsl = it.pressureMsl,
                rain = it.precipitation ?: 0.0,
                snowfall = null,
                temperature = it.temperature,
                time = it.timestamp.iso8601TimestampToMilliseconds(),
                ultraviolet = null,
                visibility = it.visibility,
                weatherCondition = DwdWeatherConditionMap.getCondition(it.icon),
                windDirection = WindDirection.toWindDirectionFromDegrees(it.windDirection),
                windSpeed = it.windSpeed,
            )
        },
        location = location,
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
        val dewPoint = dailyIt.value.map { it.dewPoint ?: -1.0 }.average()
        val humidity = dailyIt.value.map { it.humidity?.toDouble() ?: -1.0 }.average()
        val icon = dailyIt.value.map { it.icon }.groupingBy { it }.eachCount().entries.maxByOrNull { it.value }
        val pressure = dailyIt.value.map { it.pressureMsl ?: -1.0 }.average()
        val rainSum = dailyIt.value.sumOf { it.precipitation ?: 0.0 }
        val temperatureMaximum = dailyIt.value.maxOf { it.temperature }
        val temperatureMinimum = dailyIt.value.minOf { it.temperature }
        val time = dailyIt.key
        val visibility = dailyIt.value.minOf { it.visibility?.toDouble() ?: -1.0 }
        val windDirection = dailyIt.value.mapNotNull { it.windDirection }.maxOrNull()
        val windSpeed = dailyIt.value
            .mapNotNull { it.windSpeed }
            .average()
        val condition = computeDailyWeatherCondition(
            getHourlyConditionsForDay(
                data = dailyIt.value,
                time = time,
            ),
            MetNorwayWeatherConditionMap.getCondition(icon?.key)
        )

        val precipitationProbabilityMax = dailyIt.value.mapNotNull { it.precipitationProbability }.maxOrNull()
        val index = groupedByDay.keys.indexOf(dailyIt.key)

        WeatherDaily(
            dawn = sunTimings[index].dawn ?: 0L,
            dewPoint = dewPoint,
            dusk = sunTimings[index].dusk ?: 0L,
            humidity = humidity,
            moonIllumination = moonTimings[index].illumination,
            moonPhase = moonTimings[index].phase,
            moonPhaseDaysRemaining = moonTimings[index].daysRemaining,
            moonrise = moonTimings[index].moonrise ?: -0L,
            moonset = moonTimings[index].moonset ?: -0L,
            precipitationProbabilityMax = precipitationProbabilityMax,
            pressureMsl = pressure,
            rainSum = rainSum,
            snowfallSum = null,
            sunrise = sunTimings[index].sunrise ?: -0L,
            sunset = sunTimings[index].sunset ?: -0L,
            temperatureMaximum = temperatureMaximum,
            temperatureMinimum = temperatureMinimum,
            time = time,
            ultravioletMaximum = null,
            visibility = visibility.roundToInt(),
            weatherCondition = condition,
            windDirection = WindDirection.toWindDirectionFromDegrees(windDirection),
            windSpeed = windSpeed,
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
