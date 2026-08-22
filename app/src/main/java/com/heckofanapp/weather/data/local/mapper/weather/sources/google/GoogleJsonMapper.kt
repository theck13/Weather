package com.heckofanapp.weather.data.local.mapper.weather.sources.google

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.weather.DistanceUnit
import com.heckofanapp.weather.core.model.weather.PrecipitationUnit
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.google.GoogleWeatherConditionMap
import com.heckofanapp.weather.core.network.sources.weather.google.json.GoogleDailyWeatherItemJson
import com.heckofanapp.weather.core.network.sources.weather.google.json.GoogleDailyWeatherPartJson
import com.heckofanapp.weather.core.network.sources.weather.google.json.GoogleHourlyWeatherItemJson
import com.heckofanapp.weather.core.network.sources.weather.google.json.GooglePrecipitationJson
import com.heckofanapp.weather.core.network.sources.weather.google.json.bundle.GoogleWeatherBundle
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.heckofanapp.weather.core.utils.formatters.safeZoneId
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import java.time.LocalDate
import java.time.format.DateTimeParseException
import kotlin.math.roundToInt

// Snow comes back as liquid water equivalent.  Standard 10:1 ratio turns it into depth.
private const val SNOW_TO_LIQUID_WATER_RATIO = 10.00

/**
 * Daily gives no whole day pressure, dew point, or visibility; only two 12 hour halves.
 * So, these are folded out of hourly list that was already fetched rather than costing
 * second provider call.  Days past hourly coverage stay null and their blocks hide.
 */
private data class GoogleDailyAggregate(
    val dewPoint: Double?,   // °C (mean)
    val pressure: Double?,   // hPa (mean)
    val visibility: Double?, // meters (minimum)
)

private fun List<GoogleHourlyWeatherItemJson>.toDailyAggregates(
    zoneId: String,
): Map<Long, GoogleDailyAggregate> {
    return this
        .groupBy { hour -> hour.interval?.startTime.toMillisecondsOrNull()?.normalizeToDay(zoneId) }
        .mapNotNull { (day, hours) ->
            if (day == null) return@mapNotNull null

            day to GoogleDailyAggregate(
                dewPoint = hours.mapNotNull { it.dewPoint?.degrees }.averageOrNull(),
                pressure = hours.mapNotNull { it.pressure?.meanSeaLevelMillibars }.averageOrNull(),
                visibility = DistanceUnit.KM.convert(
                    from = hours.mapNotNull { it.visibility?.distance }.minOrNull(),
                    to = DistanceUnit.M,
                ),
            )
        }
        .toMap()
}

private fun List<Double>.averageOrNull(): Double? {
    return if (isEmpty()) null else average()
}

/**
 * Timestamps are RFC 3339 rather than unixtime.  A malformed one only drops its own entry
 * instead of failing whole refresh.
 */
private fun String?.toMillisecondsOrNull(): Long? {
    if (this == null) return null

    return try {
        this.iso8601TimestampToMilliseconds()
    } catch (e: DateTimeParseException) {
        null
    }
}

/**
 * Google day runs 7am to 7am next local day.  So, [GoogleDailyWeatherItemJson.displayDate] is
 * what day is called and interval start is only a fallback.
 */
private fun GoogleDailyWeatherItemJson.toDayStartMilliseconds(
    zoneId: String,
): Long? {
    val date = displayDate

    if (date?.day != null && date.month != null && date.year != null) {
        return LocalDate
            .of(date.year, date.month, date.day)
            .atStartOfDay(
                safeZoneId(
                    id = zoneId,
                )
            )
            .toInstant()
            .toEpochMilli()
    }

    return interval?.startTime.toMillisecondsOrNull()?.normalizeToDay(zoneId)
}

private fun GooglePrecipitationJson?.toRainMillimeters(): Double {
    return this?.qpf?.quantity ?: 0.00
}

private fun GooglePrecipitationJson?.toSnowfallCentimeters(): Double? {
    val liquid = this?.snowQpf?.quantity ?: return null

    return PrecipitationUnit.MM.convert(
        from = liquid * SNOW_TO_LIQUID_WATER_RATIO,
        to = PrecipitationUnit.CM,
    )
}

fun GoogleWeatherBundle.toDomain(
    location: Location,
): Weather {
    val current = this.current
    val hourly = this.hourly
    val aggregates = hourly.toDailyAggregates(location.timezone)
    // A day with no readable date can't be placed on timeline.  So, it is dropped.
    val daily = this.daily.forecastDays.mapNotNull { item ->
        item.toDayStartMilliseconds(location.timezone)?.let { item to it }
    }
    // Sun and moon are always computed locally, so Google's own events are ignored.
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
            cloudCover = current.cloudCover?.toDouble(),
            dewPoint = current.dewPoint?.degrees,
            feelsLike = current.temperatureFeels?.degrees,
            humidity = current.humidity?.toDouble() ?: 0.00,
            lastUpdatedInMilli = System.currentTimeMillis(),
            pressureMsl = current.pressure?.meanSeaLevelMillibars,
            temperature = current.temperature?.degrees,
            time = current.currentTime.toMillisecondsOrNull() ?: System.currentTimeMillis(),
            ultraviolet = current.ultraviolet,
            utcOffsetSeconds = null,
            visibility = DistanceUnit.KM.convert(current.visibility?.distance, DistanceUnit.M)?.roundToInt(),
            weatherCondition = GoogleWeatherConditionMap.getCondition(current.weatherCondition?.type),
            windDirection = WindDirection.toWindDirectionFromDegrees(current.wind?.direction?.degrees),
            windSpeed = current.wind?.speed?.value,
        ),
        daily = daily.mapIndexed { index, (item, time) ->
            val parts = listOfNotNull(item.day, item.night)
            val aggregate = aggregates[time]

            val windSpeed = parts.mapNotNull { it.wind?.speed?.value }.averageOrNull()
            val windDirection = parts.mapNotNull { it.wind?.direction?.degrees?.toDouble() }.averageOrNull()
            val humidity = parts.mapNotNull { it.humidity?.toDouble() }.averageOrNull()
            val rain = parts.sumOf { it.precipitation.toRainMillimeters() }
            val snow = parts.mapNotNull { it.precipitation.toSnowfallCentimeters() }.ifEmpty { null }?.sum()

            // Halves are weighted as day carrying full stretch of waking hours.
            val condition = computeDailyWeatherCondition(
                List(12) { GoogleWeatherConditionMap.getCondition(item.day?.weatherCondition?.type) } +
                    GoogleWeatherConditionMap.getCondition(item.night?.weatherCondition?.type),
                WeatherCondition.NO_CONDITION_FOUND,
            )

            val precipitationProbabilityMax = parts.mapNotNull { it.precipitation?.probability?.percent }.maxOrNull()
            val ultravioletMaximum = parts.mapNotNull(GoogleDailyWeatherPartJson::ultraviolet).maxOrNull()

            WeatherDaily(
                dawn = sunTimings[index].dawn ?: -0L,
                dewPoint = aggregate?.dewPoint,
                dusk = sunTimings[index].dusk ?: -0L,
                humidity = humidity,
                moonIllumination = moonTimings[index].illumination,
                moonPhase = moonTimings[index].phase,
                moonPhaseDaysRemaining = moonTimings[index].daysRemaining,
                moonrise = moonTimings[index].moonrise ?: -0L,
                moonset = moonTimings[index].moonset ?: -0L,
                precipitationProbabilityMax = precipitationProbabilityMax,
                pressureMsl = aggregate?.pressure,
                rainSum = rain,
                snowfallSum = snow,
                sunrise = sunTimings[index].sunrise ?: -0L,
                sunset = sunTimings[index].sunset ?: -0L,
                temperatureMaximum = item.temperatureMaximum?.degrees,
                temperatureMinimum = item.temperatureMinimum?.degrees,
                time = time,
                ultravioletMaximum = ultravioletMaximum,
                visibility = aggregate?.visibility?.roundToInt(),
                weatherCondition = condition,
                windDirection = WindDirection.toWindDirectionFromDegrees(windDirection?.roundToInt()),
                windSpeed = windSpeed,
            )
        },
        // An hour with no readable time can't be matched to a day.  So, it is dropped.
        hourly = hourly.mapNotNull { hour ->
            val time = hour.interval?.startTime.toMillisecondsOrNull() ?: return@mapNotNull null

            WeatherHourly(
                dewPoint = hour.dewPoint?.degrees,
                humidity = hour.humidity?.toDouble(),
                precipitationProbability = hour.precipitation?.probability?.percent,
                pressureMsl = hour.pressure?.meanSeaLevelMillibars,
                rain = hour.precipitation.toRainMillimeters(),
                snowfall = hour.precipitation.toSnowfallCentimeters(),
                temperature = hour.temperature?.degrees,
                time = time,
                ultraviolet = hour.ultraviolet,
                visibility = DistanceUnit.KM.convert(hour.visibility?.distance, DistanceUnit.M)?.roundToInt(),
                weatherCondition = GoogleWeatherConditionMap.getCondition(hour.weatherCondition?.type),
                windDirection = WindDirection.toWindDirectionFromDegrees(hour.wind?.direction?.degrees),
                windSpeed = hour.wind?.speed?.value,
            )
        },
        location = location,
    )
}
