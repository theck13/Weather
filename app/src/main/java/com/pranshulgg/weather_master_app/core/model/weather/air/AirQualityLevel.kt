package com.pranshulgg.weather_master_app.core.model.weather.air

import android.content.Context
import com.pranshulgg.weather_master_app.R

enum class AirQualityLevel(
    override val label: String,
    override val severity: Int,
) : AirQualityIndexCategory {
    FAIR(
        label = "20 - 50",
        severity = 1,
    ),
    GOOD(
        label = "0 - 20",
        severity = 0,
    ),
    HAZARDOUS(
        label = "250+",
        severity = 5,
    ),
    MODERATE(
        label = "50 - 100",
        severity = 2,
    ),
    POOR(
        label = "100 - 150",
        severity = 3,
    ),
    VERY_POOR(
        label = "150 - 250",
        severity = 4,
    );

    companion object {
        /** Lower bound of each AQI band, indexed by [AirQualityIndexCategory.severity]. */
        private val thresholds = listOf(
            0,
            20,
            50,
            100,
            150,
            250,
        )

        fun fromAqi(
            aqi: Int,
        ): AirQualityLevel {
            val severity = thresholds.indexOfLast { aqi >= it }.coerceAtLeast(0)
            return entries.first { it.severity == severity }
        }
    }
}

fun AirQualityLevel.toDescription(
    context: Context,
): String {
    return context.getString(
        when (this) {
            AirQualityLevel.FAIR -> R.string.weather_aqi_description_2
            AirQualityLevel.GOOD -> R.string.weather_aqi_description_1
            AirQualityLevel.HAZARDOUS -> R.string.weather_aqi_description_6
            AirQualityLevel.MODERATE -> R.string.weather_aqi_description_3
            AirQualityLevel.POOR -> R.string.weather_aqi_description_4
            AirQualityLevel.VERY_POOR -> R.string.weather_aqi_description_5
        }
    )
}
