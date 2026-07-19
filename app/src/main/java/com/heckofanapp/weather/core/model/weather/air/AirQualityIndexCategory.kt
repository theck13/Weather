package com.heckofanapp.weather.core.model.weather.air

import android.content.Context
import androidx.annotation.StringRes
import com.heckofanapp.weather.R

/**
 * A single air-quality category (e.g. "Good", "Unhealthy").
 * Both European  [AirQualityLevel] and US [AirQualityLevelUs] implement this so UI can render a name,
 * a range label, and a color (via [severity]) without caring which standard is active.
 */
interface AirQualityIndexCategory {
    /** 0..5 position on the shared green -> maroon color ramp. */
    val severity: Int
    /** Human-facing AQI value range for this category, e.g. "0 - 50". */
    val label: String

    /** Health-guidance description, keyed by [severity] so both standards share same text. */
    @get:StringRes
    val description: Int
        get() = when (severity) {
            0 -> R.string.weather_aqi_description_1
            1 -> R.string.weather_aqi_description_2
            2 -> R.string.weather_aqi_description_3
            3 -> R.string.weather_aqi_description_4
            4 -> R.string.weather_aqi_description_5
            else -> R.string.weather_aqi_description_6
        }

    /** Display name, keyed by [severity] so both standards share same labels. */
    @get:StringRes
    val display: Int
        get() = when (severity) {
            0 -> R.string.weather_aqi_level_1
            1 -> R.string.weather_aqi_level_2
            2 -> R.string.weather_aqi_level_3
            3 -> R.string.weather_aqi_level_4
            4 -> R.string.weather_aqi_level_5
            else -> R.string.weather_aqi_level_6
        }

    fun toName(
        context: Context,
    ): String = context.getString(display)
}
