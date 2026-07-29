package com.heckofanapp.weather.data.local.mapper.weather.sources.meteoam

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.WindUnit
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.meteoam.MeteoAmWeatherConditionMap
import com.heckofanapp.weather.core.network.sources.weather.meteoam.json.bundle.MeteoAmWeatherBundle
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.heckofanapp.weather.core.utils.formatters.toSafeDouble
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.calculations.computeApparentTemperature
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import kotlin.math.roundToInt

private data class Daily(
    val temp: Double?,
    val windSpeed: Double?,
    val windDirection: Int?,
    val humidity: Double?,
    val icon: String?,
    val pop: Double?,
    val time: String,
    val pressure: Double?,
)

fun MeteoAmWeatherBundle.toDomain(
    location: Location,
): Weather {
    val current = this.current.datasets.current
    val forecast = this.forecast.datasets.forecast
    val forecastTimes = this.forecast.timeSeries
    val time = this.current.timeSeries.first().first()
    val windDirectionCurrent = current.windDirection.value.takeUnless { it == "VRB" }

    val temperature = forecast.temperature?.values?.toList()
    val windDirection = forecast.windDirection?.values?.toList()
    val windSpeed = forecast.windSpeedKmh?.values?.toList()

    val humidity = forecast.humidity?.values?.toList()
    val icon = forecast.icon.values.toList()
    val precipitationProbability = forecast.precipitationProbability?.values?.toList()
    val pressure = forecast.pressure?.values?.toList()

    val dailyWrapper: List<Daily> =
        icon.mapIndexed { index, string ->
            val direction = windDirection?.getOrNull(index).takeUnless { it == "VRB" }

            Daily(
                humidity = humidity?.getOrNull(index),
                icon = string,
                pop = precipitationProbability?.getOrNull(index),
                pressure = pressure?.getOrNull(index),
                temp = temperature?.getOrNull(index),
                time = forecastTimes.getOrNull(index)!!,
                windDirection = direction.toSafeDouble()?.toInt(),
                windSpeed = windSpeed?.getOrNull(index),
            )
        }

    val daily = computeDaily(
        data = dailyWrapper,
        location = location,
    )

    return Weather(
        current = WeatherCurrently(
            temperature = current.temperature.value,
            humidity = current.humidity.value ?: 0.0,
            windSpeed = current.windSpeedKmh.value,
            windDirection = WindDirection.toWindDirectionFromDegrees(
                value = windDirectionCurrent.toSafeDouble()?.toInt(),
            ),
            pressureMsl = current.pressure.value,
            visibility = null,
            cloudCover = null,
            ultraviolet = null,
            weatherCondition = MeteoAmWeatherConditionMap.getCondition(current.icon.value),
            feelsLike = computeApparentTemperature(
                humidity = current.humidity.value,
                tempC = current.temperature.value,
                windMs = WindUnit.KPH.convert(
                    from = current.windSpeedKmh.value,
                    to = WindUnit.MPS,
                ),
            ),
            time = time.iso8601TimestampToMilliseconds(),
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        daily = daily,
        hourly = forecastTimes.mapIndexed { index, time ->
            val direction = windDirection?.getOrNull(index).takeUnless { it == "VRB" }

            WeatherHourly(
                dewPoint = null,
                humidity = humidity?.getOrNull(index),
                precipitationProbability = precipitationProbability
                    ?.getOrNull(index)
                    ?.roundToInt(),
                pressureMsl = pressure?.getOrNull(index),
                rain = 0.0,
                snowfall = null,
                temperature = temperature?.getOrNull(index),
                time = time.iso8601TimestampToMilliseconds(),
                ultraviolet = null,
                visibility = null,
                weatherCondition = MeteoAmWeatherConditionMap.getCondition(icon.getOrNull(index)),
                windDirection = WindDirection.toWindDirectionFromDegrees(
                    value = direction.toSafeDouble()?.toInt(),
                ),
                windSpeed = windSpeed?.getOrNull(index),
            )
        }.filter { it.weatherCondition != WeatherCondition.NO_CONDITION_FOUND },
        location = location,
    )
}

private fun computeDaily(
    data: List<Daily>,
    location: Location,
): List<WeatherDaily> {
    val zoneId = location.timezone

    val groupedByDay = data.groupBy {
        it.time.iso8601TimestampToMilliseconds().normalizeToDay(zoneId)
    }

    val keyIndices = groupedByDay.keys.withIndex().associate { it.value to it.index }
    val moonTimings = getMoonTimings(
        groupedByDay.map {
            it.key
        },
        location.timezone,
        location.latitude,
        location.longitude,
    )
    val sunTimings = getSunTimings(
        groupedByDay.map {
            it.key
        },
        location.timezone,
        location.latitude,
        location.longitude,
    )

    return groupedByDay
        .map { dailyIt ->
            val humidityMinimum = dailyIt.value.minOf { it.humidity ?: -1.0 }.takeIf { it >= 0.0 }
            val icon = dailyIt.value.map { it.icon }.groupingBy { it }
                .eachCount().entries.maxByOrNull { it.value }
            val index = keyIndices[dailyIt.key] ?: -1
            val pressureMinimum = dailyIt.value.minOf { it.pressure ?: -1.0 }.takeIf { it >= 0.0 }
            val temperatureMaximum = dailyIt.value.maxOf { it.temp ?: -1.0 }.takeIf { it >= 0.0 }
            val temperatureMinimum = dailyIt.value.minOf { it.temp ?: -1.0 }.takeIf { it >= 0.0 }
            val weatherCondition = computeDailyWeatherCondition(
                data = getHourlyConditionsForDay(data, dailyIt.key),
                defaultFallBack = MeteoAmWeatherConditionMap.getCondition(icon?.key),
            )
            val windDirection =
                dailyIt.value.map { it.windDirection?.toDouble() ?: -1.0 }.average()
                    .takeIf { it >= 0.0 }
            val windSpeed =
                dailyIt.value.map { it.windSpeed ?: -1.0 }.average().takeIf { it >= 0.0 }

            WeatherDaily(
                dawn = sunTimings[index].dawn ?: -1L,
                dewPoint = null,
                dusk = sunTimings[index].dusk ?: -1L,
                humidity = humidityMinimum,
                moonIllumination = moonTimings[index].illumination,
                moonPhase = moonTimings[index].phase,
                moonPhaseDaysRemaining = moonTimings[index].daysRemaining,
                moonrise = moonTimings[index].moonrise ?: -1L,
                moonset = moonTimings[index].moonset ?: -1L,
                precipitationProbabilityMax = null,
                pressureMsl = pressureMinimum,
                rainSum = 0.0,
                snowfallSum = null,
                sunrise = sunTimings[index].sunrise ?: -1L,
                sunset = sunTimings[index].sunset ?: -1L,
                temperatureMaximum = temperatureMaximum,
                temperatureMinimum = temperatureMinimum,
                time = dailyIt.key,
                ultravioletMaximum = null,
                visibility = null,
                weatherCondition = weatherCondition,
                windDirection = WindDirection.toWindDirectionFromDegrees(windDirection?.toInt()),
                windSpeed = windSpeed,
            )

        }.take(4)
}

private fun getHourlyConditionsForDay(
    data: List<Daily>,
    time: Long,
): List<WeatherCondition> {
    val startIndex =
        data.indexOfFirst { it.time.iso8601TimestampToMilliseconds() >= time }
            .takeIf { it != -1 } ?: 0

    val conditions = data.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.METEOAM.hourlyAggregationLimitHours)
        .map {
            MeteoAmWeatherConditionMap.getCondition(it.icon)
        }

    return conditions
}
