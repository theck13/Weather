package com.heckofanapp.weather.core.utils.weather.computing.summary

import android.content.Context
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.core.model.weather.TemperatureUnit
import com.heckofanapp.weather.core.prefs.PreferencesHelper
import com.heckofanapp.weather.core.utils.formatters.to12HourTimeString
import com.heckofanapp.weather.core.utils.formatters.to24HourTimeString
import kotlin.math.roundToInt

fun getHeadline(
    context: Context,
    summaryData: SummaryData,
    units: WeatherUnits,
    zoneId: String,
): String {
    val rain = summaryData.rain
    val snow = summaryData.snow
    val peakUv = summaryData.uv
    val is24hr = PreferencesHelper.getString("app_time") == "24"
    val overviewTemplates = listOf(
        context.getString(R.string.summary_overview_template_1, summaryData.condition),
        context.getString(R.string.summary_overview_template_2, summaryData.condition),
        context.getString(R.string.summary_overview_template_3, summaryData.condition),
        context.getString(R.string.summary_overview_template_4, summaryData.condition)
    )
    val parts = mutableListOf<String>()

    val peakRainyAt =
        if (is24hr) to24HourTimeString(
            timeMilli = rain.at,
            zoneId = zoneId
        ) else to12HourTimeString(
            timeMilli = rain.at,
            zoneId = zoneId
        )
    val peakSnowAt =
        if (is24hr) to24HourTimeString(
            timeMilli = snow.at,
            zoneId = zoneId
        ) else to12HourTimeString(
            timeMilli = snow.at,
            zoneId = zoneId
        )
    val peakUvAt =
        if (is24hr) to24HourTimeString(
            timeMilli = peakUv.at,
            zoneId = zoneId
        ) else to12HourTimeString(
            timeMilli = peakUv.at,
            zoneId = zoneId
        )

    parts += overviewTemplates.random()

    val rainSentence = when {
        rain.amount == 0.0 -> null

        rain.probability >= 80 ->
            listOf(
                context.getString(R.string.summary_rain_high_template_1, peakRainyAt),
                context.getString(R.string.summary_rain_high_template_2, peakRainyAt),
                context.getString(R.string.summary_rain_high_template_3, peakRainyAt),
                context.getString(R.string.summary_rain_high_template_4, peakRainyAt),
            ).random()

        rain.probability >= 40 ->
            listOf(
                context.getString(R.string.summary_rain_medium_template_1, peakRainyAt),
                context.getString(R.string.summary_rain_medium_template_2, peakRainyAt),
                context.getString(R.string.summary_rain_medium_template_3, peakRainyAt),
                context.getString(R.string.summary_rain_medium_template_4, peakRainyAt),
            ).random()

        else ->
            listOf(
                context.getString(R.string.summary_rain_low_template_1, peakRainyAt),
                context.getString(R.string.summary_rain_low_template_2, peakRainyAt),
                context.getString(R.string.summary_rain_low_template_3, peakRainyAt),
            ).random()
    }

    rainSentence?.let { parts += it }

    val snowSentence = when {
        snow.amount == 0.0 -> null

        snow.probability >= 80 ->
            listOf(
                context.getString(R.string.summary_snow_high_template_1, peakSnowAt),
                context.getString(R.string.summary_snow_high_template_2, peakSnowAt),
                context.getString(R.string.summary_snow_high_template_3, peakSnowAt),
                context.getString(R.string.summary_snow_high_template_4)
            ).random()

        snow.probability >= 40 ->
            listOf(
                context.getString(R.string.summary_snow_medium_template_1, peakSnowAt),
                context.getString(R.string.summary_snow_medium_template_2, peakSnowAt),
                context.getString(R.string.summary_snow_medium_template_3, peakSnowAt),
                context.getString(R.string.summary_snow_medium_template_4)
            ).random()

        else ->
            listOf(
                context.getString(R.string.summary_snow_low_template_1, peakSnowAt),
                context.getString(R.string.summary_snow_low_template_2, peakSnowAt),
                context.getString(R.string.summary_snow_low_template_3)
            ).random()
    }

    snowSentence?.let { parts += it }

    val uvSentence = when {
        peakUv.uv >= 10 ->
            listOf(
                context.getString(R.string.summary_uv_extreme_template_1, peakUvAt),
                context.getString(R.string.summary_uv_extreme_template_2, peakUvAt),
                context.getString(R.string.summary_uv_extreme_template_3, peakUvAt)
            ).random()

        peakUv.uv >= 7 ->
            listOf(
                context.getString(R.string.summary_uv_high_template_1, peakUvAt),
                context.getString(R.string.summary_uv_high_template_2, peakUvAt),
                context.getString(R.string.summary_uv_high_template_3, peakUvAt)
            ).random()

        peakUv.uv >= 4 ->
            listOf(
                context.getString(R.string.summary_uv_moderate_template_1),
                context.getString(R.string.summary_uv_moderate_template_2),
                context.getString(R.string.summary_uv_moderate_template_3)
            ).random()

        else -> null
    }

    uvSentence?.let { parts += it }

    val temperatureMaximum =
        TemperatureUnit.CELSIUS.convert(
            from = summaryData.temperatures.temperatureMaximum,
            to = units.temperature,
        )?.roundToInt()!!
    val temperatureMinimum =
        TemperatureUnit.CELSIUS.convert(
            from = summaryData.temperatures.temperatureMinimum,
            to = units.temperature,
        )?.roundToInt()!!

    val temperatureSentence = when {
        summaryData.temperatures.temperatureMaximum >= 35 -> listOf(
            context.getString(R.string.summary_temp_hot_template_1, "${temperatureMaximum}°"),
            context.getString(R.string.summary_temp_hot_template_2),
            context.getString(R.string.summary_temp_hot_template_3, "${temperatureMinimum}°")
        ).random()

        summaryData.temperatures.temperatureMaximum >= 25 -> listOf(
            context.getString(R.string.summary_temp_warm_template_1, "${temperatureMaximum}°"),
            context.getString(R.string.summary_temp_warm_template_2),
            context.getString(R.string.summary_temp_warm_template_3, "${temperatureMaximum}°")
        ).random()

        summaryData.temperatures.temperatureMaximum >= 15 -> listOf(
            context.getString(R.string.summary_temp_mild_template_1),
            context.getString(R.string.summary_temp_mild_template_2, "${temperatureMaximum}°"),
            context.getString(R.string.summary_temp_mild_template_3)
        ).random()

        summaryData.temperatures.temperatureMaximum >= 5 -> listOf(
            context.getString(R.string.summary_temp_cool_template_1, "${temperatureMaximum}°"),
            context.getString(R.string.summary_temp_cool_template_2),
            context.getString(R.string.summary_temp_cool_template_3)
        ).random()

        else -> listOf(
            context.getString(R.string.summary_temp_cold_template_1),
            context.getString(R.string.summary_temp_cold_template_2),
            context.getString(R.string.summary_temp_cold_template_3, "${temperatureMinimum}°")
        ).random()
    }

    parts += temperatureSentence

    return parts.joinToString(" ")
}
