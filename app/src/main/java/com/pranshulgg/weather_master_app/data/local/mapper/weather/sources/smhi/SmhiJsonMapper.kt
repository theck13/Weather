package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.smhi

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrently
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.WindUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.SmhiWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.json.SmhiForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.json.SmhiForecastTimeSeriesJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.calculations.computeApparentTemperature
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import kotlin.math.roundToInt

fun SmhiForecastJson.toDomain(
    location: Location,
): Weather {
    val currentHour = findHourlyIndexForTime(
        time = this.timeSeries.map { it.time.iso8601TimestampToMilliseconds() },
    )
    val current = this.timeSeries[currentHour].data
    val currentTime = this.timeSeries[currentHour].time.iso8601TimestampToMilliseconds()

    val rainTypes = listOf(1, 2, 3, 11, 12, 4, 7)
    val snowTypes = listOf(5, 6, 7, 8, 9)

    val daily = computeDaily(this, location)

    return Weather(
        location = location,
        current = WeatherCurrently(
            temperature = current.temperature,
            humidity = current.humidity?.toDouble() ?: 0.0,
            windSpeed = WindUnit.MPS.convert(
                from = current.windSpeed,
                to = WindUnit.KPH,
            ),
            windDirection = WindDirection.toWindDirectionFromDegrees(current.windDirection),
            pressureMsl = current.pressureMsl,
            visibility = DistanceUnit.KM.convert(
                from = current.visibility,
                to = DistanceUnit.M,
            )?.roundToInt(),
            cloudCover = null, // NOT USED IN THE APP
            ultraviolet = null,
            weatherCondition = SmhiWeatherConditionMap.getCondition(current.symbolCode),
            feelsLike = computeApparentTemperature(
                tempC = current.temperature,
                humidity = current.humidity?.toDouble(),
                windMs = current.windSpeed,
            ),
            time = currentTime,
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis(),
        ),
        hourly = this.timeSeries.map { item ->
            val data = item.data

            /**
             * API only provides precipitation and type
             * So we map the codes to what is considered rain/snow
             * Source: https://opendata.smhi.se/metfcst/snow1gv1/parameters#precipitation-type
             */
            val rain = if (data.precipitationType in rainTypes) data.precipitationAmountMax else 0.0
            val snowfall =
                if (data.precipitationType in snowTypes) {
                    PrecipitationUnit.MM.convert(
                        from = data.precipitationAmountMax,
                        to = PrecipitationUnit.CM,
                    )
                } else {
                    0.0
                }

            WeatherHourly(
                temperature = data.temperature,
                windSpeed = WindUnit.MPS.convert(
                    from = data.windSpeed,
                    to = WindUnit.KPH,
                ),
                windDirection = WindDirection.toWindDirectionFromDegrees(data.windDirection),
                rain = rain,
                snowfall = snowfall,
                ultraviolet = null,
                weatherCondition = SmhiWeatherConditionMap.getCondition(data.symbolCode),
                time = item.time.iso8601TimestampToMilliseconds(),
                precipitationProbability = data.precipitationProbability,
                pressureMsl = data.pressureMsl,
                humidity = data.humidity?.toDouble(),
                visibility = DistanceUnit.KM.convert(
                    from = data.visibility,
                    to = DistanceUnit.M,
                )?.roundToInt(),
                dewPoint = null,
            )
        },
        daily = daily,
    )
}

private fun computeDaily(
    data: SmhiForecastJson,
    location: Location,
): List<WeatherDaily> {
    val daily = data.timeSeries
    val zoneId = location.timezone

    val rainTypes = listOf(1, 2, 3, 11, 12, 4, 7)
    val snowTypes = listOf(5, 6, 7, 8, 9)

    val groupedByDay = daily.groupBy {
        it.time.iso8601TimestampToMilliseconds()
            .normalizeToDay(zoneId)
    }

    val sunTimings = getSunTimings(
        groupedByDay.map {
            it.key
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        groupedByDay.map {
            it.key
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    return groupedByDay.map { dailyIt ->
        val minTemperature = dailyIt.value.minOf { it.data.temperature }
        val maxTemperature = dailyIt.value.maxOf { it.data.temperature }
        val windSpeed = dailyIt.value.map { it.data.windSpeed }.average()
        val windDirection = dailyIt.value.map { it.data.windDirection }.average().roundToInt()

        val rainSum =
            dailyIt.value.sumOf { if (it.data.precipitationType in rainTypes) it.data.precipitationAmountMax else 0.0 }
        val snowfallSum =
            dailyIt.value.sumOf { if (it.data.precipitationType in snowTypes) it.data.precipitationAmountMax else 0.0 }

        val time = dailyIt.key
        val icon = dailyIt.value.map { it.data.symbolCode }.groupingBy { it }
            .eachCount().entries.maxByOrNull { it.value }

        val condition = computeDailyWeatherCondition(
            getHourlyConditionsForDay(dailyIt.value, time),
            SmhiWeatherConditionMap.getCondition(icon?.key)
        )

        val precipitationProbabilityMax = dailyIt.value.maxOf { it.data.precipitationProbability }

        val index = groupedByDay.keys.indexOf(dailyIt.key)

        val avgHumidity = dailyIt.value.map { it.data.humidity?.toDouble() ?: -1.0 }.average()
        val avgPressure = dailyIt.value.map { it.data.pressureMsl ?: -1.0 }.average()
        val minVisibility = dailyIt.value.minOf { it.data.visibility ?: -1.0 }

        WeatherDaily(
            temperatureMin = minTemperature,
            temperatureMax = maxTemperature,
            windSpeed = WindUnit.MPS.convert(
                from = windSpeed,
                to = WindUnit.KPH,
            ),
            windDirection = WindDirection.toWindDirectionFromDegrees(windDirection),
            rainSum = rainSum,
            snowfallSum = PrecipitationUnit.MM.convert(
                from = snowfallSum,
                to = PrecipitationUnit.CM,
            ),
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
            pressureMsl = avgPressure,
            visibility = DistanceUnit.KM.convert(
                from = minVisibility,
                to = DistanceUnit.M,
            )?.roundToInt(),
            humidity = avgHumidity,
            dewPoint = null,
        )
    }
}

private fun getHourlyConditionsForDay(
    data: List<SmhiForecastTimeSeriesJson>,
    time: Long,
): List<WeatherCondition> {
    val startIndex =
        data.indexOfFirst { it.time.iso8601TimestampToMilliseconds() >= time }
            .takeIf { it != -1 } ?: 0

    val conditions =
        data.drop(maxOf(0, startIndex - 1)).take(WeatherSource.SMHI.hourlyAggregationLimitHours)
            .map {
                SmhiWeatherConditionMap.getCondition(it.data.symbolCode)
            }

    return conditions
}
