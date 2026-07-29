package com.heckofanapp.weather.data.local.mapper.weather.sources.meteofrance

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherCurrently
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherHourly
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.WindUnit
import com.heckofanapp.weather.core.model.weather.wind.WindDirection
import com.heckofanapp.weather.core.network.sources.weather.meteofrance.MeteoFranceConditionMap
import com.heckofanapp.weather.core.network.sources.weather.meteofrance.json.MeteoFranceForecastItemJson
import com.heckofanapp.weather.core.network.sources.weather.meteofrance.json.MeteoFranceForecastJson
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.heckofanapp.weather.core.utils.weather.astronomy.getMoonTimings
import com.heckofanapp.weather.core.utils.weather.astronomy.getSunTimings
import com.heckofanapp.weather.core.utils.weather.calculations.computeApparentTemperature
import com.heckofanapp.weather.core.utils.weather.computing.computeDailyWeatherCondition
import com.heckofanapp.weather.core.utils.weather.forecast.findHourlyIndexForTime

fun MeteoFranceForecastJson.toDomain(location: Location): Weather {
    val forecast = this.properties.forecast
    val daily =
        computeDaily(
            forecast.filter { it.temperature != null },
            location
        ) // We don't use "this.properties.daily" cuz too complicated

    val currentHour = findHourlyIndexForTime(
        time = forecast.map { it.time.secondsToMilliseconds() },
    )

    return Weather(
        current = WeatherCurrently(
            cloudCover = null, // NOT USED IN THE APP
            dewPoint = null,
            feelsLike = computeApparentTemperature(
                humidity = forecast[currentHour].humidity?.toDouble(),
                tempC = forecast[currentHour].temperature,
                windMs = forecast[currentHour].windSpeed?.toDouble(),
            ),
            humidity = forecast[currentHour].humidity?.toDouble() ?: 0.0,
            lastUpdatedInMilli = System.currentTimeMillis(),
            pressureMsl = forecast[currentHour].pressureMsl,
            temperature = forecast[currentHour].temperature,
            time = forecast[currentHour].time.secondsToMilliseconds(),
            ultraviolet = null, // Only daily
            utcOffsetSeconds = null,
            visibility = null,
            weatherCondition = MeteoFranceConditionMap.getCondition(forecast[currentHour].icon),
            windDirection = WindDirection.toWindDirectionFromDegrees(forecast[currentHour].windDirection),
            windSpeed = WindUnit.MPS.convert(
                from = forecast[currentHour].windSpeed?.toDouble(),
                to = WindUnit.KPH,
            ),
        ),
        daily = daily,
        hourly = forecast.filter { it.temperature != null }.map {
            WeatherHourly(
                dewPoint = null,
                humidity = it.humidity?.toDouble(),
                precipitationProbability = null,
                pressureMsl = it.pressureMsl,
                rain = it.rain ?: 0.0,
                snowfall = it.snow,
                temperature = it.temperature,
                time = it.time.secondsToMilliseconds(),
                ultraviolet = null,
                visibility = null,
                weatherCondition = MeteoFranceConditionMap.getCondition(it.icon),
                windDirection = WindDirection.toWindDirectionFromDegrees(it.windDirection),
                windSpeed = WindUnit.MPS.convert(
                    from = it.windSpeed?.toDouble(),
                    to = WindUnit.KPH,
                ),
            )
        },
        location = location,
    )
}

private fun computeDaily(
    data: List<MeteoFranceForecastItemJson>,
    location: Location,
): List<WeatherDaily> {
    val zoneId = location.timezone

    val groupedByDay = data.groupBy {
        it.time.secondsToMilliseconds().normalizeToDay(zoneId)
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
        val temperatureMinimum = dailyIt.value.mapNotNull { it.temperature }.min()
        val temperatureMaximum = dailyIt.value.mapNotNull { it.temperature }.max()
        val windSpeed = dailyIt.value
            .mapNotNull { it.windSpeed }
            .average()

        val windDirection =
            dailyIt.value.mapNotNull { it.windDirection }.maxOrNull()

        val rainSum =
            dailyIt.value.sumOf { it.rain ?: 0.0 }

        val snowSum =
            dailyIt.value.sumOf { it.snow ?: 0.0 }

        val time = dailyIt.key
        val icon = dailyIt.value.map { it.icon }.groupingBy { it }
            .eachCount().entries.maxByOrNull { it.value }

        val condition = computeDailyWeatherCondition(
            getHourlyConditionsForDay(dailyIt.value, time),
            MeteoFranceConditionMap.getCondition(icon?.key)
        )

        val index = groupedByDay.keys.indexOf(dailyIt.key)

        val avgHumidity = dailyIt.value.map { it.humidity?.toDouble() ?: -1.0 }.average()
        val avgPressure = dailyIt.value.map { it.pressureMsl ?: -1.0 }.average()

        WeatherDaily(
            dawn = sunTimings[index].dawn ?: 0L,
            dewPoint = null,
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
            snowfallSum = snowSum,
            sunrise = sunTimings[index].sunrise ?: -0L,
            sunset = sunTimings[index].sunset ?: -0L,
            temperatureMinimum = temperatureMinimum,
            temperatureMaximum = temperatureMaximum,
            time = time,
            ultravioletMaximum = null,
            visibility = null,
            weatherCondition = condition,
            windDirection = WindDirection.toWindDirectionFromDegrees(windDirection),
            windSpeed = WindUnit.MPS.convert(
                from = windSpeed,
                to = WindUnit.KPH,
            ),
        )
    }
}

private fun getHourlyConditionsForDay(
    data: List<MeteoFranceForecastItemJson>,
    time: Long,
): List<WeatherCondition> {
    val startIndex =
        data.indexOfFirst { it.time >= time }
            .takeIf { it != -1 } ?: 0

    val conditions = data.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.METEO.hourlyAggregationLimitHours)
        .map {
            MeteoFranceConditionMap.getCondition(it.icon)
        }

    return conditions
}
