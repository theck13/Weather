package com.pranshulgg.weather_master_app.core.model.weather.air

/**
 * US EPA AirNow AQI categories.
 * [https://www.airnow.gov/aqi/aqi-basics/]
 */
enum class AirQualityLevelUs(
    override val label: String,
    override val severity: Int,
) : AirQualityIndexCategory {
    GOOD(
        label = "0 - 50",
        severity = 0,
    ),
    HAZARDOUS(
        label = "301+",
        severity = 5,
    ),
    MODERATE(
        label = "51 - 100",
        severity = 1,
    ),
    UNHEALTHY(
        label = "151 - 200",
        severity = 3,
    ),
    UNHEALTHY_SENSITIVE(
        label = "101 - 150",
        severity = 2,
    ),
    VERY_UNHEALTHY(
        label = "201 - 300",
        severity = 4,
    );

    companion object {
        /** Lower bound of each category's AQI value, indexed by [AirQualityIndexCategory.severity]. */
        private val thresholds = listOf(
            0,
            51,
            101,
            151,
            201,
            301,
        )

        fun fromAqi(
            aqi: Int,
        ): AirQualityLevelUs {
            val severity = thresholds.indexOfLast { aqi >= it }.coerceAtLeast(0)
            return entries.first { it.severity == severity }
        }
    }
}
