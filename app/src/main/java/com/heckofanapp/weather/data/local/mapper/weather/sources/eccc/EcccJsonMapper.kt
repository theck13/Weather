package com.heckofanapp.weather.data.local.mapper.weather.sources.eccc

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.model.weather.DistanceUnit
import com.heckofanapp.weather.core.model.weather.PressureUnit
import com.heckofanapp.weather.core.model.weather.WindUnit
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.eccc.EcccConditionMap
import com.heckofanapp.weather.core.network.sources.weather.eccc.json.EcccHourlyWeatherItemJson
import com.heckofanapp.weather.core.network.sources.weather.eccc.json.EcccWeatherJson
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.heckofanapp.weather.core.utils.formatters.safeZoneId
import com.heckofanapp.weather.core.utils.formatters.toSafeDouble
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.calculations.computeApparentTemperature
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

fun EcccWeatherJson.toDomain(
    location: Location,
): Weather {
    val current = this.observation
    val hourly = this.hourlyFcst.hourly

    val dailyNight = this.dailyFcst.daily.filter { it.periodLabel == "Night" || it.periodLabel == "Tonight" }
    val daily = this.dailyFcst.daily.filter { it.periodLabel != "Night" && it.periodLabel != "Tonight" }
            .take(dailyNight.size)

    val sunTimings = getSunTimings(
        daily.map {
            dateToMillis(it.date, location.timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude,
    )

    val moonTimings = getMoonTimings(
        daily.map {
            dateToMillis(it.date, location.timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude,
    )

    return Weather(
        current = WeatherCurrently(
            cloudCover = null, // NOT USED IN THE APP
            dewPoint = current.dewpoint.metric?.toSafeDouble(),
            feelsLike = current.feelsLike.metric?.toSafeDouble() ?: computeApparentTemperature(
                humidity = current.humidity?.toSafeDouble(),
                tempC = current.temperature.metric?.toSafeDouble(),
                windMs = WindUnit.KPH.convert(
                    from = current.windSpeed.metric?.toSafeDouble(),
                    to = WindUnit.MPS,
                ),
            ),
            humidity = current.humidity?.toSafeDouble() ?: 0.0,
            lastUpdatedInMilli = System.currentTimeMillis(),
            pressureMsl = PressureUnit.INHG.convert(
                from = current.pressure.imperial?.toSafeDouble(),
                to = PressureUnit.HPA,
            ),
            temperature = current.temperature.metric?.toSafeDouble(),
            time = current.timeStamp.iso8601TimestampToMilliseconds(),
            ultraviolet = null,
            utcOffsetSeconds = null,
            weatherCondition = EcccConditionMap.getCondition(current.iconCode),
            windDirection = WindDirection.toWindDirectionFromString(current.windDirection),
            windSpeed = current.windSpeed.metric?.toSafeDouble(),
            visibility = DistanceUnit.KM.convert(
                from = current.visibility.metric?.toSafeDouble(),
                to = DistanceUnit.M,
            )?.roundToInt(),
        ),
        daily = daily.mapIndexed { index, it ->
            val time = dateToMillis(it.date, location.timezone)

            WeatherDaily(
                dawn = sunTimings[index].dawn ?: 0L,
                dewPoint = null,
                dusk = sunTimings[index].dusk ?: 0L,
                humidity = null,
                moonIllumination = moonTimings[index].illumination,
                moonPhase = moonTimings[index].phase,
                moonPhaseDaysRemaining = moonTimings[index].daysRemaining,
                moonrise = moonTimings[index].moonrise ?: -0L,
                moonset = moonTimings[index].moonset ?: -0L,
                precipitationProbabilityMax = it.precipProbability?.toSafeDouble()?.roundToInt() ?: getMaxPrecipitationProbability(hourly, time),
                pressureMsl = null,
                rainSum = 0.0,
                snowfallSum = null,
                sunrise = sunTimings[index].sunrise ?: -0L,
                sunset = sunTimings[index].sunset ?: -0L,
                temperatureMaximum = it.temperature.metric?.toSafeDouble(),
                temperatureMinimum = dailyNight[index].temperature.metric?.toSafeDouble(),
                time = time,
                ultravioletMaximum = null,
                visibility = null,
                weatherCondition = EcccConditionMap.getCondition(it.iconCode),
                windDirection = null,
                windSpeed = null,
            )
        },
        hourly = hourly.map {
            WeatherHourly(
                dewPoint = null,
                humidity = null,
                precipitationProbability = it.precipProbability?.toIntOrNull(),
                pressureMsl = null,
                rain = 0.0, // NULL
                snowfall = null,
                temperature = it.temperature.metric?.toSafeDouble(),
                time = it.epochTime.secondsToMilliseconds(),
                ultraviolet = null,
                visibility = null,
                weatherCondition = EcccConditionMap.getCondition(it.iconCode),
                windDirection = WindDirection.toWindDirectionFromString(it.windDir),
                windSpeed = it.windSpeed.metric?.toSafeDouble(),
            )
        },
        location = location,
    )
}

private fun dateToMillis(
    dateStr: String,
    zoneId: String,
): Long {
    val formatted = dateStr.replace(Regex(" (\\d) ")) { " 0${it.groupValues[1]} " }

    val dateWithYear = "$formatted ${Year.now().value}"
    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)
    val localDate = LocalDate.parse(dateWithYear, formatter)

    return localDate.atStartOfDay(
        safeZoneId(
            id = zoneId,
        )
    ).toInstant().toEpochMilli()
}

private fun getMaxPrecipitationProbability(
    hourly: List<EcccHourlyWeatherItemJson>,
    time: Long,
): Int? {
    val startIndex = hourly.indexOfFirst { it.epochTime.secondsToMilliseconds() >= time }.takeIf { it != -1 } ?: 0
    val data = hourly.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.FMI.hourlyAggregationLimitHours)

    if (data.map { it.precipProbability }.all { it == null }) {
        return null
    }

    val maxProbability = data.mapNotNull { it.precipProbability }.maxOf { it.toDouble() }

    return maxProbability.roundToInt()
}
