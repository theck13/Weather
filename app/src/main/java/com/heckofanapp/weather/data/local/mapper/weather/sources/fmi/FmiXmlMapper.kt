package com.heckofanapp.weather.data.local.mapper.weather.sources.fmi

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.WindUnit
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.fmi.FmiConditionMap
import com.heckofanapp.weather.core.network.sources.weather.fmi.model.FmiWeather
import com.heckofanapp.weather.core.network.sources.weather.fmi.model.FmiWeatherMember
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.heckofanapp.weather.core.utils.formatters.toSafeDouble
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.calculations.computeApparentTemperature
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import com.heckofanapp.weather.core.utils.weather.forecast.findHourlyIndexForTime
import kotlin.math.roundToInt

fun FmiWeather.toDomain(
    location: Location,
): Weather {
    val forecast = this.data.groupBy { it.time!!.iso8601TimestampToMilliseconds() }
    val current = if (!this.observation.isNullOrEmpty()) {
        this.observation.groupBy { it.parameterName }
    } else {
        null
    }
    val currentHour = findHourlyIndexForTime(
        time = forecast.keys.map { it }
    )

    val hourDataForParam: (Long, String) -> Double? = { hour, param ->
        forecast[hour]?.firstOrNull { data -> data.parameterName == param }?.parameterValue?.toSafeDouble()
            ?.takeIf { it.isFinite() }
    }

    val currentDataForParam: (String) -> Double? = {
        val current = if (current != null) {
            current[it]?.get(0)?.parameterValue?.toSafeDouble()
        } else null
        val correctKey = when (it) {
            "t2m" -> "Temperature"
            "ws_10min" -> "WindSpeedMS"
            "wd_10min" -> "WindDirection"
            "rh" -> "Humidity"
            "td" -> "DewPoint"
            "p_sea" -> "Pressure"
            "vis" -> "Visibility"
            "wawa" -> "WeatherSymbol3"
            else -> ""
        }
        val currentFromHourly = forecast[forecast.keys.sorted()
            .getOrNull(currentHour)]?.firstOrNull { data -> data.parameterName == correctKey }?.parameterValue?.toSafeDouble()

        current ?: currentFromHourly
    }

    /**
     * Doing this separately
     * If we use currentDataForParam("wawa") it will never be null
     * Because it falls back to hourly
     *
     * Which would prevent us from knowing if the current icon is null or not
     * and make "use hourly icon as fallback" just not work
     */
    val currentIcon =
        if (current != null) {
            current["wawa"]?.get(0)?.parameterValue?.toSafeDouble()
        } else {
            null
        }

    val daily = computeDaily(this.data, location)

    return Weather(
        current = WeatherCurrently(
            cloudCover = null,
            dewPoint = currentDataForParam("td"),
            feelsLike = computeApparentTemperature(
                humidity = currentDataForParam("rh"),
                tempC = currentDataForParam("t2m"),
                windMs = currentDataForParam("ws_10min"),
            ),
            humidity = currentDataForParam("rh") ?: 0.0,
            lastUpdatedInMilli = System.currentTimeMillis(),
            pressureMsl = currentDataForParam("p_sea"),
            temperature = currentDataForParam("t2m"),
            time = if (current != null) current["t2m"]?.get(0)!!.time!!.iso8601TimestampToMilliseconds() else forecast.keys.sorted()[currentHour],
            ultraviolet = null,
            utcOffsetSeconds = null,
            visibility = currentDataForParam("vis")?.toInt(),
            weatherCondition =
                if (currentIcon == 0.0 || currentIcon == null || currentIcon.isNaN()) {
                    FmiConditionMap.getCondition(
                        hourDataForParam(
                            forecast.keys.sorted()[currentHour],
                            "WeatherSymbol3"
                        )?.toInt()
                    )
                } else {
                    FmiConditionMap.getCurrentCondition(currentDataForParam("wawa")?.toInt())
                },
            windDirection = WindDirection.toWindDirectionFromDegrees(currentDataForParam("wd_10min")?.toInt()),
            windSpeed = WindUnit.MPS.convert(
                from = currentDataForParam("ws_10min"),
                to = WindUnit.KPH,
            ),
        ),
        daily = daily,
        hourly = forecast.keys.map { time ->
            WeatherHourly(
                temperature = hourDataForParam(time, "Temperature"),
                windSpeed = WindUnit.MPS.convert(
                    from = hourDataForParam(time, "WindSpeedMS"),
                    to = WindUnit.KPH,
                ),
                windDirection = WindDirection.toWindDirectionFromDegrees(
                    hourDataForParam(
                        time,
                        "WindDirection"
                    )?.toInt()
                ),
                rain = hourDataForParam(time, "Precipitation1h") ?: 0.0,
                snowfall = null,
                ultraviolet = null,
                pressureMsl = hourDataForParam(time, "Pressure"),
                visibility = hourDataForParam(time, "Visibility")?.roundToInt(),
                humidity = hourDataForParam(time, "Humidity"),
                dewPoint = hourDataForParam(time, "DewPoint"),
                weatherCondition = FmiConditionMap.getCondition(
                    code = hourDataForParam(
                        time,
                        "WeatherSymbol3"
                    )?.toInt(),
                ),
                time = time,
                precipitationProbability = hourDataForParam(time, "PoP")?.roundToInt()
            )
        },
        location = location,
    )
}

private fun computeDaily(
    data: List<FmiWeatherMember>,
    location: Location,
): List<WeatherDaily> {
    val zoneId = location.timezone

    val groupedByDay = data.groupBy {
        it.time!!.iso8601TimestampToMilliseconds().normalizeToDay(zoneId)
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

    val dataForParam: (Long, String) -> List<FmiWeatherMember>? = { hour, param ->
        groupedByDay[hour]?.filter { it.parameterName == param } ?: emptyList()
    }
    return groupedByDay.map { dailyIt ->
        val minTemperature =
            dataForParam(
                dailyIt.key,
                "Temperature"
            )?.mapNotNull { it.parameterValue?.toSafeDouble() }
                ?.minOf { it }.takeIf { it != null && it.isNaN().not() }
        val maxTemperature =
            dataForParam(
                dailyIt.key,
                "Temperature"
            )?.mapNotNull { it.parameterValue?.toSafeDouble() }
                ?.maxOf { it }.takeIf { it != null && it.isNaN().not() }

        val windDirection =
            dataForParam(dailyIt.key, "WindDirection")?.mapNotNull { it.parameterValue }
                ?.maxOrNull().takeIf { it != null }

        val rainSum = dataForParam(dailyIt.key, "Precipitation1h")?.sumOf {
            it.parameterValue?.toSafeDouble() ?: 0.0
        } ?: 0.0

        val windSpeed = dataForParam(dailyIt.key, "WindSpeedMS")
            ?.mapNotNull { it.parameterValue?.toSafeDouble() }
            ?.average().takeIf { it != null && it.isNaN().not() }

        val icon = dataForParam(dailyIt.key, "WeatherSymbol3")?.map { it.parameterValue }
            ?.groupingBy { it }
            ?.eachCount()?.entries?.maxByOrNull { it.value }.takeIf { it != null }

        val condition = computeDailyWeatherCondition(
            getHourlyConditionsForDay(dataForParam(dailyIt.key, "WeatherSymbol3")!!, dailyIt.key),
            FmiConditionMap.getCondition(icon?.key?.toSafeDouble()?.toInt())
        )

        val precipitationProbabilityMax =
            dataForParam(dailyIt.key, "PoP")?.mapNotNull {
                it.parameterValue?.toSafeDouble()?.toInt()
            }
                ?.maxOrNull()

        val avgHumidity =
            dataForParam(dailyIt.key, "Humidity")?.map { it.parameterValue.toSafeDouble() ?: -1.0 }
                ?.average().takeIf { it != null && it.isNaN().not() }

        val avgPressure =
            dataForParam(dailyIt.key, "Pressure")?.map { it.parameterValue.toSafeDouble() ?: -1.0 }
                ?.average().takeIf { it != null && it.isNaN().not() }

        val minVisibility = dataForParam(dailyIt.key, "Visibility")?.minOfOrNull {
            it.parameterValue.toSafeDouble() ?: -1.0
        }

        val avgDewPoint =
            dataForParam(dailyIt.key, "DewPoint")?.map { it.parameterValue.toSafeDouble() ?: -1.0 }
                ?.average().takeIf { it != null && it.isNaN().not() }

        val index = groupedByDay.keys.indexOf(dailyIt.key)

        WeatherDaily(
            dawn = sunTimings[index].dawn ?: 0L,
            dewPoint = avgDewPoint,
            dusk = sunTimings[index].dusk ?: 0L,
            humidity = avgHumidity,
            moonIllumination = moonTimings[index].illumination,
            moonPhase = moonTimings[index].phase,
            moonPhaseDaysRemaining = moonTimings[index].daysRemaining,
            moonrise = moonTimings[index].moonrise ?: -0L,
            moonset = moonTimings[index].moonset ?: -0L,
            precipitationProbabilityMax = precipitationProbabilityMax,
            pressureMsl = avgPressure,
            rainSum = rainSum,
            snowfallSum = null,
            sunrise = sunTimings[index].sunrise ?: -0L,
            sunset = sunTimings[index].sunset ?: -0L,
            temperatureMaximum = maxTemperature,
            temperatureMinimum = minTemperature,
            time = dailyIt.key,
            ultravioletMaximum = null,
            visibility = minVisibility?.roundToInt(),
            weatherCondition = condition,
            windDirection = WindDirection.toWindDirectionFromDegrees(
                value = windDirection?.toSafeDouble()?.toInt(),
            ),
            windSpeed = windSpeed,
        )
    }
}

private fun getHourlyConditionsForDay(
    data: List<FmiWeatherMember>,
    time: Long,
): List<WeatherCondition> {
    val startIndex =
        data.indexOfFirst { it.time!!.iso8601TimestampToMilliseconds() >= time }
            .takeIf { it != -1 } ?: 0

    val conditions = data.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.FMI.hourlyAggregationLimitHours)
        .map {
            FmiConditionMap.getCondition(it.parameterValue?.toSafeDouble()?.toInt())
        }

    return conditions
}
