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
import com.heckofanapp.weather.core.network.sources.weather.openmeteo.json.OpenMeteoWeatherJson
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * AccuWeather's daily and hourly endpoints provide no pressure, no daily dew point, and
 * no daily visibility.  So, those are backfilled from a supplemental Open Meteo call.
 * Maps are keyed to match how AccuWeather mapper looks up hourly (millis) and daily
 * (start-of-day millis) values.
 */
data class AccuSupplemental(
    val dailyDewPoint: Map<Long, Double>,   // dayStartMillis  -> °C (mean)
    val dailyPressure: Map<Long, Double>,   // dayStartMillis  -> hPa (mean)
    val dailyVisibility: Map<Long, Double>, // dayStartMillis  -> meters (minimum)
    val hourlyPressure: Map<Long, Double>,  // hourStartMillis -> hPa
)

fun OpenMeteoWeatherJson.toAccuSupplemental(
    zoneId: String,
): AccuSupplemental {
    // Open Meteo uses unixtime in SECONDS; ACCU matching uses milliseconds.
    val hourlyPressure = hourly.time.indices
        .mapNotNull { i -> hourly.pressureMsl.getOrNull(i)?.let { (hourly.time[i] * 1000) to it } }
        .toMap()
    val dailyDewPoint = daily.time.indices
        .mapNotNull { i ->
            daily.dewPoint.getOrNull(i)?.let { (daily.time[i] * 1000).normalizeToDay(zoneId) to it }
        }
        .toMap()
    val dailyPressure = daily.time.indices
        .mapNotNull { i ->
            daily.pressureMsl.getOrNull(i)?.let { (daily.time[i] * 1000).normalizeToDay(zoneId) to it }
        }
        .toMap()
    val dailyVisibility = daily.time.indices
        .mapNotNull { i ->
            daily.visibility.getOrNull(i)?.let { (daily.time[i] * 1000).normalizeToDay(zoneId) to it.toDouble() }
        }
        .toMap()

    return AccuSupplemental(
        dailyDewPoint = dailyDewPoint,
        dailyPressure = dailyPressure,
        dailyVisibility = dailyVisibility,
        hourlyPressure = hourlyPressure,
    )
}

/**
 * Look up a value keyed by start-of-day millis, tolerating slight day-boundary
 * skew between AccuWeather and Open Meteo day grids by falling back to nearest day.
 */
private fun Map<Long, Double>?.nearestByDay(
    time: Long,
): Double? {
    if (this.isNullOrEmpty()) return null
    this[time]?.let { return it }

    return entries.minByOrNull { abs(it.key - time) }?.value
}

fun AccuWeatherBundle.toDomain(
    location: Location,
    supplemental: AccuSupplemental?,
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

            val time = item.time.secondsToMilliseconds().normalizeToDay(location.timezone)

            WeatherDaily(
                dawn = sunTimings[index].dawn ?: -0L,
                dewPoint = supplemental?.dailyDewPoint.nearestByDay(time),
                dusk = sunTimings[index].dusk ?: -0L,
                humidity = humidity,
                moonIllumination = moonTimings[index].illumination,
                moonPhase = moonTimings[index].phase,
                moonPhaseDaysRemaining = moonTimings[index].daysRemaining,
                moonrise = moonTimings[index].moonrise ?: -0L,
                moonset = moonTimings[index].moonset ?: -0L,
                precipitationProbabilityMax = precipitationProbabilityMax,
                pressureMsl = supplemental?.dailyPressure.nearestByDay(time),
                rainSum = rain,
                snowfallSum = PrecipitationUnit.CM.convert(snow, PrecipitationUnit.MM),
                sunrise = sunTimings[index].sunrise ?: -0L,
                sunset = sunTimings[index].sunset ?: -0L,
                temperatureMaximum = item.temperature.maximum.value,
                temperatureMinimum = item.temperature.minimum.value,
                time = time,
                ultravioletMaximum = item.day.ultraviolet.value,
                visibility = supplemental?.dailyVisibility.nearestByDay(time)?.roundToInt(),
                weatherCondition = condition,
                windDirection = WindDirection.toWindDirectionFromDegrees(windDirection.roundToInt()),
                windSpeed = windSpeed,
            )
        },
        hourly = hourly.map { hour ->
            val time = hour.time.secondsToMilliseconds()

            WeatherHourly(
                dewPoint = hour.dewPoint.value,
                humidity = hour.humidity?.toDouble(),
                precipitationProbability = hour.precipitation,
                pressureMsl = supplemental?.hourlyPressure?.get(time),
                rain = hour.rain.value ?: 0.0,
                snowfall = PrecipitationUnit.CM.convert(hour.snowCm.value, PrecipitationUnit.MM),
                temperature = hour.temperature.value,
                time = time,
                ultraviolet = hour.ultraviolet,
                visibility = DistanceUnit.KM.convert(hour.visibility.value, DistanceUnit.M)?.roundToInt(),
                weatherCondition = AccuWeatherConditionMap.getCondition(hour.icon),
                windDirection = WindDirection.toWindDirectionFromDegrees(hour.wind.direction.degrees),
                windSpeed = hour.wind.speed.value,
            )
        },
        location = location,
    )
}
