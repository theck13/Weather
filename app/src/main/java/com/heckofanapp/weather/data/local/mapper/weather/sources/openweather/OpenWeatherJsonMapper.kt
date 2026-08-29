package com.heckofanapp.weather.data.local.mapper.weather.sources.openweather

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.model.weather.PrecipitationUnit
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.WindUnit
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.openmeteo.json.OpenMeteoWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.openweather.OpenWeatherConditionMap
import com.heckofanapp.weather.core.network.sources.weather.openweather.json.OpenWeatherConditionJson
import com.heckofanapp.weather.core.network.sources.weather.openweather.json.OpenWeatherHourlyItemJson
import com.heckofanapp.weather.core.network.sources.weather.openweather.json.OpenWeatherJson
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * One Call's daily block carries no visibility at all.  So, it is backfilled from supplemental
 * Open Meteo call.  Map is keyed to match how mapper looks up daily values (start-of-day millis).
 */
data class OpenWeatherSupplemental(
    val dailyVisibility: Map<Long, Double>, // dayStartMillis -> meters (minimum)
)

fun OpenMeteoWeatherJson.toOpenWeatherSupplemental(
    zoneId: String,
): OpenWeatherSupplemental {
    val dailyVisibility = daily.time.indices
        .mapNotNull { index ->
            daily.visibility.getOrNull(index)?.let {
                daily.time[index].secondsToMilliseconds().normalizeToDay(zoneId) to it.toDouble()
            }
        }
        .toMap()

    return OpenWeatherSupplemental(
        dailyVisibility = dailyVisibility,
    )
}

/**
 * Look up value keyed by start-of-day millis, tolerating slight day-boundary skew between
 * OpenWeather and Open Meteo day grids by falling back to nearest day.
 */
private fun Map<Long, Double>?.nearestByDay(
    time: Long,
): Double? {
    if (this.isNullOrEmpty()) return null
    this[time]?.let { return it }

    return entries.minByOrNull { abs(it.key - time) }?.value
}

// Condition list carries one entry per request, and first entry is the primary condition.
private fun List<OpenWeatherConditionJson>?.toWeatherCondition(): WeatherCondition {
    return OpenWeatherConditionMap.getCondition(this?.firstOrNull()?.id)
}

// Precipitation probability comes back as fraction of one.
private fun Double?.toPercentOrNull(): Double? {
    return this?.times(100)
}

private fun Double?.toKilometersPerHourOrNull(): Double? {
    return WindUnit.MPS.convert(
        from = this,
        to = WindUnit.KPH,
    )
}

// Snow arrives in millimeters while app keeps snowfall in centimeters.
private fun Double?.toCentimetersOrNull(): Double? {
    return PrecipitationUnit.MM.convert(
        from = this,
        to = PrecipitationUnit.CM,
    )
}

fun OpenWeatherJson.toDomain(
    location: Location,
    supplemental: OpenWeatherSupplemental?,
): Weather {
    val current = this.current
    val hourly = this.hourly.orEmpty()
    // A day with no readable time can't be placed on timeline.  So, it is dropped.
    val daily = this.daily.orEmpty().mapNotNull { item ->
        item.time?.secondsToMilliseconds()?.normalizeToDay(location.timezone)?.let { item to it }
    }
    // Sun and moon are always computed locally, so OpenWeather's own events are ignored.
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
            cloudCover = current?.cloudCover,
            dewPoint = current?.dewPoint,
            feelsLike = current?.temperatureFeels,
            humidity = current?.humidity ?: 0.00,
            lastUpdatedInMilli = System.currentTimeMillis(),
            pressureMsl = current?.pressure,
            temperature = current?.temperature,
            time = current?.time?.secondsToMilliseconds() ?: System.currentTimeMillis(),
            ultraviolet = current?.ultraviolet,
            utcOffsetSeconds = null,
            visibility = current?.visibility,
            weatherCondition = current?.weather.toWeatherCondition(),
            windDirection = WindDirection.toWindDirectionFromDegrees(current?.windDirection?.roundToInt()),
            windSpeed = current?.windSpeed.toKilometersPerHourOrNull(),
        ),
        daily = daily.mapIndexed { index, (item, time) ->
            // Daily condition only covers whole day.  So, hours it spans decide condition and
            // daily entry is fallback for days past hourly coverage.
            val condition = computeDailyWeatherCondition(
                getHourlyConditionsForDay(hourly, time),
                item.weather.toWeatherCondition(),
            )

            WeatherDaily(
                dawn = sunTimings[index].dawn ?: -0L,
                dewPoint = item.dewPoint,
                dusk = sunTimings[index].dusk ?: -0L,
                humidity = item.humidity,
                moonIllumination = moonTimings[index].illumination,
                moonPhase = moonTimings[index].phase,
                moonPhaseDaysRemaining = moonTimings[index].daysRemaining,
                moonrise = moonTimings[index].moonrise ?: -0L,
                moonset = moonTimings[index].moonset ?: -0L,
                precipitationProbabilityMax = item.precipitationProbability.toPercentOrNull()?.roundToInt(),
                pressureMsl = item.pressure,
                rainSum = item.rainSum ?: 0.00,
                snowfallSum = item.snowfallSum.toCentimetersOrNull(),
                sunrise = sunTimings[index].sunrise ?: -0L,
                sunset = sunTimings[index].sunset ?: -0L,
                temperatureMaximum = item.temperature?.maximum,
                temperatureMinimum = item.temperature?.minimum,
                time = time,
                ultravioletMaximum = item.ultraviolet,
                visibility = supplemental?.dailyVisibility.nearestByDay(time)?.roundToInt(),
                weatherCondition = condition,
                windDirection = WindDirection.toWindDirectionFromDegrees(item.windDirection?.roundToInt()),
                windSpeed = item.windSpeed.toKilometersPerHourOrNull(),
            )
        },
        // An hour with no readable time can't be matched to a day.  So, it is dropped.
        hourly = hourly.mapNotNull { hour ->
            val time = hour.time?.secondsToMilliseconds() ?: return@mapNotNull null

            WeatherHourly(
                dewPoint = hour.dewPoint,
                humidity = hour.humidity,
                precipitationProbability = hour.precipitationProbability.toPercentOrNull()?.roundToInt(),
                pressureMsl = hour.pressure,
                rain = hour.rain?.lastHour ?: 0.00,
                snowfall = hour.snowfall?.lastHour.toCentimetersOrNull(),
                temperature = hour.temperature,
                time = time,
                ultraviolet = hour.ultraviolet,
                visibility = hour.visibility,
                weatherCondition = hour.weather.toWeatherCondition(),
                windDirection = WindDirection.toWindDirectionFromDegrees(hour.windDirection?.roundToInt()),
                windSpeed = hour.windSpeed.toKilometersPerHourOrNull(),
            )
        },
        location = location,
    )
}

/**
 * Hourly reaches two days while daily reaches eight.  So, later days match no hour at all and
 * come back empty rather than borrowing first day's hours.
 */
private fun getHourlyConditionsForDay(
    data: List<OpenWeatherHourlyItemJson>,
    time: Long,
): List<WeatherCondition> {
    val startIndex = data
        .indexOfFirst { (it.time?.secondsToMilliseconds() ?: 0L) >= time }
        .takeIf { it != -1 } ?: return emptyList()

    return data.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.OPENWEATHER.hourlyAggregationLimitHours)
        .map { it.weather.toWeatherCondition() }
}
