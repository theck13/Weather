package com.heckofanapp.weather.data.local.mapper.weather.sources.pirate

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.model.weather.DistanceUnit
import com.heckofanapp.weather.core.model.weather.PrecipitationUnit
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.WindUnit
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.pirate.PirateConditionMap
import com.heckofanapp.weather.core.network.sources.weather.pirate.json.PirateHourlyItemJson
import com.heckofanapp.weather.core.network.sources.weather.pirate.json.PirateJson
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import kotlin.math.roundToInt

// Times are unixtime in seconds rather than milliseconds.
private fun Long?.toMillisecondsOrNull(): Long? {
    return this?.times(1000)
}

// Humidity, cloud cover, and probability come back as fractions of one.
private fun Double?.toPercentOrNull(): Double? {
    return this?.times(100)
}

private fun Double?.toKilometersPerHourOrNull(): Double? {
    return WindUnit.MPS.convert(
        from = this,
        to = WindUnit.KPH,
    )
}

private fun Double?.toMetersOrNull(): Int? {
    return DistanceUnit.KM.convert(
        from = this,
        to = DistanceUnit.M,
    )?.roundToInt()
}

fun PirateJson.toDomain(
    location: Location,
): Weather {
    val current = this.currently
    val hourly = this.hourly?.data.orEmpty()
    // A day with no readable time can't be placed on timeline.  So, it is dropped.
    val daily = this.daily?.data.orEmpty().mapNotNull { item ->
        item.time.toMillisecondsOrNull()?.normalizeToDay(location.timezone)?.let { item to it }
    }
    // Sun and moon are always computed locally, so Pirate's own events are ignored.
    val moonTimings = getMoonTimings(
        timeMilli = daily.map { it.second },
        zoneId = location.timezone,
        latitude = location.latitude,
        longitude = location.longitude,
    )
    val sunTimings = getSunTimings(
        timeMilli = daily.map { it.second },
        zoneId = location.timezone,
        latitude = location.latitude,
        longitude = location.longitude,
    )

    return Weather(
        current = WeatherCurrently(
            cloudCover = current?.cloudCover.toPercentOrNull(),
            dewPoint = current?.dewPoint,
            feelsLike = current?.temperatureFeels,
            humidity = current?.humidity.toPercentOrNull() ?: 0.00,
            lastUpdatedInMilli = System.currentTimeMillis(),
            pressureMsl = current?.pressure,
            temperature = current?.temperature,
            time = current?.time.toMillisecondsOrNull() ?: System.currentTimeMillis(),
            ultraviolet = current?.ultraviolet,
            utcOffsetSeconds = null,
            visibility = current?.visibility.toMetersOrNull(),
            weatherCondition = PirateConditionMap.getCondition(current?.icon),
            windDirection = WindDirection.toWindDirectionFromDegrees(current?.windDirection?.roundToInt()),
            windSpeed = current?.windSpeed.toKilometersPerHourOrNull(),
        ),
        daily = daily.mapIndexed { index, (item, time) ->
            // Daily icon only covers whole day.  So, hours it spans decide condition and icon
            // is fallback for days past hourly coverage.
            val condition = computeDailyWeatherCondition(
                getHourlyConditionsForDay(hourly, time),
                PirateConditionMap.getCondition(item.icon),
            )

            WeatherDaily(
                dawn = sunTimings[index].dawn ?: -0L,
                dewPoint = item.dewPoint,
                dusk = sunTimings[index].dusk ?: -0L,
                humidity = item.humidity.toPercentOrNull(),
                moonIllumination = moonTimings[index].illumination,
                moonPhase = moonTimings[index].phase,
                moonPhaseDaysRemaining = moonTimings[index].daysRemaining,
                moonrise = moonTimings[index].moonrise ?: -0L,
                moonset = moonTimings[index].moonset ?: -0L,
                precipitationProbabilityMax = item.precipitationProbability.toPercentOrNull()?.roundToInt(),
                pressureMsl = item.pressure,
                rainSum = PrecipitationUnit.CM.convert(item.rainSum, PrecipitationUnit.MM) ?: 0.00,
                snowfallSum = item.snowfallSum,
                sunrise = sunTimings[index].sunrise ?: -0L,
                sunset = sunTimings[index].sunset ?: -0L,
                temperatureMaximum = item.temperatureMaximum,
                temperatureMinimum = item.temperatureMinimum,
                time = time,
                ultravioletMaximum = item.ultraviolet,
                visibility = item.visibility.toMetersOrNull(),
                weatherCondition = condition,
                windDirection = WindDirection.toWindDirectionFromDegrees(item.windDirection?.roundToInt()),
                windSpeed = item.windSpeed.toKilometersPerHourOrNull(),
            )
        },
        // An hour with no readable time can't be matched to a day.  So, it is dropped.
        hourly = hourly.mapNotNull { hour ->
            val time = hour.time.toMillisecondsOrNull() ?: return@mapNotNull null

            WeatherHourly(
                dewPoint = hour.dewPoint,
                humidity = hour.humidity.toPercentOrNull(),
                precipitationProbability = hour.precipitationProbability.toPercentOrNull()?.roundToInt(),
                pressureMsl = hour.pressure,
                rain = hour.rain ?: 0.00,
                snowfall = hour.snowfall,
                temperature = hour.temperature,
                time = time,
                ultraviolet = hour.ultraviolet,
                visibility = hour.visibility.toMetersOrNull(),
                weatherCondition = PirateConditionMap.getCondition(hour.icon),
                windDirection = WindDirection.toWindDirectionFromDegrees(hour.windDirection?.roundToInt()),
                windSpeed = hour.windSpeed.toKilometersPerHourOrNull(),
            )
        },
        location = location,
    )
}

/**
 * Hourly reaches one day short of daily.  So, last day matches no hour at all and comes back
 * empty rather than borrowing first day's hours.
 */
private fun getHourlyConditionsForDay(
    data: List<PirateHourlyItemJson>,
    time: Long,
): List<WeatherCondition> {
    val startIndex = data
        .indexOfFirst { (it.time.toMillisecondsOrNull() ?: 0L) >= time }
        .takeIf { it != -1 } ?: return emptyList()

    return data.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.PIRATE.hourlyAggregationLimitHours)
        .map { PirateConditionMap.getCondition(it.icon) }
}
