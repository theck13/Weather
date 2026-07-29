package com.heckofanapp.weather.data.local.mapper.weather.sources.metnorway

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.metnorway.MetNorwayWeatherConditionMap
import com.heckofanapp.weather.core.network.sources.weather.metnorway.json.MetNorwayForecastJson
import com.heckofanapp.weather.core.network.sources.weather.metnorway.json.MetNorwayForecastTimeSeriesJson
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.calculations.computeApparentTemperature
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import com.heckofanapp.weather.core.utils.weather.forecast.findHourlyIndexForTime
import kotlin.math.roundToInt

fun MetNorwayForecastJson.toDomain(
    location: Location,
): Weather {
    val currentHour = findHourlyIndexForTime(
        time = this.properties.data.map { it.time.iso8601TimestampToMilliseconds() },
    )

    val current = this.properties.data[currentHour].data
    val currentTime = this.properties.data[currentHour].time.iso8601TimestampToMilliseconds()
    val daily = computeDaily(this, location)

    val nextHour = this.properties.data.map {
        it.data.nextHour
            ?: it.data.next6Hours
            ?: it.data.next12Hours
    }

    return Weather(
        current = WeatherCurrently(
            cloudCover = null, // NOT USED IN THE APP
            dewPoint = current.instant.details.dewPoint,
            feelsLike = computeApparentTemperature(
                humidity = current.instant.details.relativeHumidity,
                tempC = current.instant.details.temperature,
                windMs = current.instant.details.windSpeed,
            ),
            humidity = current.instant.details.relativeHumidity ?: 0.0,
            lastUpdatedInMilli = System.currentTimeMillis(),
            pressureMsl = current.instant.details.pressureMsl,
            temperature = current.instant.details.temperature,
            time = currentTime,
            ultraviolet = current.instant.details.uvIndex,
            utcOffsetSeconds = null,
            visibility = null,
            weatherCondition = MetNorwayWeatherConditionMap.getCondition(nextHour[currentHour]?.summary?.symbolCode),
            windDirection = WindDirection.toWindDirectionFromDegrees(current.instant.details.windDirection.roundToInt()),
            windSpeed = current.instant.details.windSpeed,
        ),
        daily = daily,
        hourly = this.properties.data.map { item ->
            val data = item.data.instant.details

            val nextHourDetails =
                item.data.nextHour?.details
                    ?: item.data.next6Hours?.details
                    ?: item.data.next12Hours?.details

            val icon =
                item.data.nextHour?.summary?.symbolCode
                    ?: item.data.next6Hours?.summary?.symbolCode
                    ?: item.data.next12Hours?.summary?.symbolCode

            WeatherHourly(
                dewPoint = data.dewPoint,
                humidity = data.relativeHumidity,
                precipitationProbability = null,
                pressureMsl = data.pressureMsl,
                rain = nextHourDetails?.precipitationAmount ?: 0.0,
                snowfall = null,
                temperature = data.temperature,
                time = item.time.iso8601TimestampToMilliseconds(),
                ultraviolet = data.uvIndex,
                visibility = null,
                weatherCondition = MetNorwayWeatherConditionMap.getCondition(icon),
                windDirection = WindDirection.toWindDirectionFromDegrees(data.windDirection.roundToInt()),
                windSpeed = data.windSpeed,
            )
        },
        location = location,
    )
}

private fun computeDaily(
    data: MetNorwayForecastJson,
    location: Location,
): List<WeatherDaily> {
    val daily = data.properties.data
    val zoneId = location.timezone

    val groupedByDay = daily.groupBy {
        it.time.iso8601TimestampToMilliseconds().normalizeToDay(zoneId)

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

    return groupedByDay.map { dailyIt ->
        val nextHourDetails = dailyIt.value.map {
            it.data.nextHour?.details
                ?: it.data.next6Hours?.details
                ?: it.data.next12Hours?.details
        }

        val nextHourSummary = dailyIt.value.map {
            it.data.nextHour?.summary
                ?: it.data.next6Hours?.summary
                ?: it.data.next12Hours?.summary
        }

        val minTemperature = dailyIt.value.minOf { it.data.instant.details.temperature }
        val maxTemperature = dailyIt.value.maxOf { it.data.instant.details.temperature }
        val windSpeed = dailyIt.value.map { it.data.instant.details.windSpeed }.average()
        val windDirection = dailyIt.value.map { it.data.instant.details.windDirection }.average().roundToInt()
        val rainSum = nextHourDetails.sumOf { it?.precipitationAmount ?: 0.0 } ?: 0.0
        val uvIndexMax = dailyIt.value.maxOf { it.data.instant.details.uvIndex }
        val time = dailyIt.key
        val icon = nextHourSummary.map { it?.symbolCode }.groupingBy { it }
            .eachCount().entries.maxByOrNull { it.value }

        val condition = computeDailyWeatherCondition(
            getHourlyConditionsForDay(dailyIt.value, time),
            MetNorwayWeatherConditionMap.getCondition(icon?.key)
        )

        val avgHumidity = dailyIt.value.map { it.data.instant.details.relativeHumidity ?: -1.0 }.average()
        val avgDewPoint = dailyIt.value.map { it.data.instant.details.dewPoint ?: -1.0 }.average()
        val avgPressure = dailyIt.value.map { it.data.instant.details.pressureMsl ?: -1.0 }.average()

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
            precipitationProbabilityMax = null,
            pressureMsl = avgPressure,
            rainSum = rainSum,
            snowfallSum = null,
            sunrise = sunTimings[index].sunrise ?: -0L,
            sunset = sunTimings[index].sunset ?: -0L,
            temperatureMaximum = maxTemperature,
            temperatureMinimum = minTemperature,
            time = time,
            ultravioletMaximum = uvIndexMax,
            visibility = null,
            weatherCondition = condition,
            windDirection = WindDirection.toWindDirectionFromDegrees(windDirection),
            windSpeed = windSpeed,
        )
    }.take(4)
}

private fun getHourlyConditionsForDay(
    data: List<MetNorwayForecastTimeSeriesJson>,
    time: Long,
): List<WeatherCondition> {
    val startIndex =
        data.indexOfFirst { it.time.iso8601TimestampToMilliseconds() >= time }
            .takeIf { it != -1 } ?: 0

    val conditions = data.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.MET.hourlyAggregationLimitHours)
        .map {
            MetNorwayWeatherConditionMap.getCondition(
                it.data.nextHour?.summary?.symbolCode
                    ?: it.data.next6Hours?.summary?.symbolCode
                    ?: it.data.next12Hours?.summary?.symbolCode
            )
        }

    return conditions
}
