package com.heckofanapp.weather.data.local.mapper.weather.sources.china

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.weather.DistanceUnit
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.china.ChinaWeatherConditionMap
import com.heckofanapp.weather.core.network.sources.weather.china.json.ChinaForecastJson
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.heckofanapp.weather.core.utils.formatters.toSafeDouble
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import kotlin.math.roundToInt

fun ChinaForecastJson.toDomain(
    location: Location,
): Weather {
    val current = this.current
    val daily = this.forecastDaily
    val hourly = this.forecastHourly
    val msDay = 24L * 60 * 60 * 1000

    val time = daily.pubTime.iso8601TimestampToMilliseconds()

    val moonTimings = getMoonTimings(
        List(daily.temperature.value.size) {
            (time + (it * msDay)).normalizeToDay(location.timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude,
    )
    val sunTimings = getSunTimings(
        List(daily.temperature.value.size) {
            (time + (it * msDay)).normalizeToDay(location.timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude,
    )

    return Weather(
        current = WeatherCurrently(
            cloudCover = null, // NOT USED IN THE APP
            dewPoint = null,
            feelsLike = current.feelsLike.value.toSafeDouble(),
            humidity = current.humidity.value.toSafeDouble() ?: 0.0,
            lastUpdatedInMilli = System.currentTimeMillis(),
            pressureMsl = current.pressure.value.toSafeDouble(),
            temperature = current.temperature.value.toSafeDouble(),
            time = current.pubTime.iso8601TimestampToMilliseconds(),
            ultraviolet = current.uvIndex.toSafeDouble(),
            utcOffsetSeconds = null,
            visibility = DistanceUnit.KM.convert(
                from = current.visibility.value.toSafeDouble(),
                to = DistanceUnit.M,
            )?.roundToInt(),
            weatherCondition = ChinaWeatherConditionMap.getCondition(current.weather.toSafeDouble()?.toInt()),
            windDirection = WindDirection.toWindDirectionFromDegrees(current.wind.direction.value.toSafeDouble()?.roundToInt()),
            windSpeed = current.wind.speed.value.toSafeDouble(),
        ),
        daily = List(daily.temperature.value.size) {
            val dailyTime = time + (it * msDay)

            val avgWindSpeed = listOf(
                daily.wind.speed.value[it].from.toSafeDouble() ?: -1.0,
                daily.wind.speed.value[it].to.toSafeDouble() ?: -1.0,
            ).average()
            val windDirection = listOf(
                daily.wind.direction.value[it].from.toSafeDouble() ?: -1.0,
                daily.wind.direction.value[it].to.toSafeDouble() ?: -1.0,
            ).average()

            WeatherDaily(
                dawn = sunTimings[it].dawn ?: -1L,
                dewPoint = null,
                dusk = sunTimings[it].dusk ?: -1L,
                humidity = null,
                moonIllumination = moonTimings[it].illumination,
                moonPhase = moonTimings[it].phase,
                moonPhaseDaysRemaining = moonTimings[it].daysRemaining,
                moonrise = moonTimings[it].moonrise ?: -1L,
                moonset = moonTimings[it].moonset ?: -1L,
                precipitationProbabilityMax = daily.precipitationProbability.value.getOrNull(it).toSafeDouble()?.roundToInt(),
                pressureMsl = null,
                rainSum = 0.0,
                snowfallSum = null,
                sunrise = sunTimings[it].sunrise ?: -1L,
                sunset = sunTimings[it].sunset ?: -1L,
                temperatureMaximum = daily.temperature.value[it].max.toSafeDouble(),
                temperatureMinimum = daily.temperature.value[it].min.toSafeDouble(),
                time = dailyTime.normalizeToDay(location.timezone),
                ultravioletMaximum = null,
                visibility = null,
                weatherCondition = ChinaWeatherConditionMap.getCondition(daily.weather.value[it].from?.toSafeDouble()?.toInt()),
                windDirection = WindDirection.toWindDirectionFromDegrees(windDirection.roundToInt()),
                windSpeed = if (avgWindSpeed >= 0.0) avgWindSpeed else null,
            )
        },
        hourly = List(hourly.wind.value.size) {
            WeatherHourly(
                dewPoint = null,
                humidity = null,
                precipitationProbability = null,
                pressureMsl = null,
                rain = 0.0,
                snowfall = null,
                temperature = hourly.temperature.value[it].toDouble(),
                time = hourly.wind.value[it].datetime.iso8601TimestampToMilliseconds(),
                ultraviolet = null,
                visibility = null,
                weatherCondition = ChinaWeatherConditionMap.getCondition(hourly.weather.value[it]),
                windDirection = WindDirection.toWindDirectionFromDegrees(hourly.wind.value[it].direction.toSafeDouble()?.roundToInt()),
                windSpeed = hourly.wind.value[it].speed.toSafeDouble(),
            )
        },
        location = location,
    )
}
