package com.pranshulgg.weather_master_app.core.model.domain.airquality

import com.pranshulgg.weather_master_app.core.model.weather.air.AirQualityIndexCategory
import com.pranshulgg.weather_master_app.core.model.weather.air.AirQualityIndexStandard
import com.pranshulgg.weather_master_app.core.model.weather.air.AirQualityLevel
import com.pranshulgg.weather_master_app.core.model.weather.air.AirQualityLevelUs
import kotlin.math.roundToInt

/**
 * Keeping air quality separate from main weather domain.
 * We don't wanna delay weather from displaying because API hasn't returned anything or is slow.
 * We only display air quality if it's available.
 */
data class AirQuality(
    val current: AirQualityCurrently,
    val hourly: List<AirQualityHourly> = emptyList(),
) {
    companion object {
        /**
         * Thresholds extracted from Breezy Weather
         * [https://github.com/breezy-weather/breezy-weather/blob/06c4f1bbd6f27f8ae04ebba550cf8981e362e777/app/src/main/kotlin/org/breezyweather/domain/weather/index/PollutantIndex.kt]
         */
        private val aqiThreshold = listOf(0, 20, 50, 100, 150, 250)
        private val coThresholds = listOf(0, 2, 4, 35, 100, 230)
        private val no2Thresholds = listOf(0, 10, 25, 200, 400, 1000)
        private val o3Thresholds = listOf(0, 50, 100, 160, 240, 480)
        private val pm10Thresholds = listOf(0, 15, 45, 80, 160, 400)
        private val pm25Thresholds = listOf(0, 5, 15, 30, 60, 150)
        private val so2Thresholds = listOf(0, 20, 40, 270, 500, 960)

        private fun getAqi(value: Double, thresholds: List<Int>): Int {
            val index = thresholds.indexOfLast { value >= it }

            if (index == -1) return 0
            if (index >= thresholds.lastIndex) return aqiThreshold.last()

            /**
             * Get threshold range
             * For example: thresholds [0, 5, 15, 30, 60, 150]
             * If value is 21, the index should return 2 because 21 is more than 15 but less than 30
             * So get the threshold range 15 - 30
             */
            val threshold1 = thresholds[index]
            val threshold2 = thresholds[index + 1]

            /**
             * Do same for AQI
             * For example: aqi thresholds [0, 20, 50, 100, 150, 250]
             * If threshold range 15 - 30, should get 50 - 100
             */
            val aqi1 = aqiThreshold[index]
            val aqi2 = aqiThreshold[index + 1]

            val pos = (value - threshold1) / (threshold2 - threshold1)

            return (aqi1 + pos * (aqi2 - aqi1)).roundToInt()
        }

        /**
         * Computes an AQI value from raw pollutant concentrations, reusable for both current
         * snapshot and each hourly step.  Null pollutants are skipped so a partial reading
         * still yields an AQI from whatever is available.
         */
        private fun computeAqi(
            carbonMonoxide: Double?,
            nitrogenDioxide: Double?,
            ozone: Double?,
            pm10: Double?,
            pm25: Double?,
            sulphurDioxide: Double?,
        ): Int {
            val values = listOfNotNull(
                pm25?.let { getAqi(it, pm25Thresholds) },
                nitrogenDioxide?.let { getAqi(it, no2Thresholds) },
                ozone?.let { getAqi(it, o3Thresholds) },
                pm10?.let { getAqi(it, pm10Thresholds) },
                // Open-Meteo provides "carbon_monoxide" in MicrogramsPerCubicMeter, so convert to Milligrams.
                carbonMonoxide?.let { getAqi(it / 1000, coThresholds) },
                sulphurDioxide?.let { getAqi(it, so2Thresholds) },
            )

            return values.maxOrNull() ?: 0
        }
    }

    /**
     * AQI value for this location's [standard].  US locations use Open-Meteo's `us_aqi`
     * (the EPA AirNow AQI); everywhere else European-style index computed from concentrations.
     */
    fun getAqi(
        standard: AirQualityIndexStandard,
    ): Int =
        when (standard) {
            AirQualityIndexStandard.EUROPEAN -> computeAqi(
                carbonMonoxide = current.carbonMonoxide,
                nitrogenDioxide = current.nitrogenDioxide,
                ozone = current.ozone,
                pm10 = current.pm10,
                pm25 = current.pm25,
                sulphurDioxide = current.sulphurDioxide,
            )
            AirQualityIndexStandard.US -> current.usAqi ?: 0
        }

    fun getAqi(
        hourly: AirQualityHourly,
        standard: AirQualityIndexStandard,
    ): Int =
        when (standard) {
            AirQualityIndexStandard.EUROPEAN -> computeAqi(
                carbonMonoxide = hourly.carbonMonoxide,
                nitrogenDioxide = hourly.nitrogenDioxide,
                ozone = hourly.ozone,
                pm10 = hourly.pm10,
                pm25 = hourly.pm25,
                sulphurDioxide = hourly.sulphurDioxide,
            )
            AirQualityIndexStandard.US -> hourly.usAqi ?: 0
        }

    fun getAqiBarValue(
        aqi: Int,
        standard: AirQualityIndexStandard,
    ): Float {
        val max = when (standard) {
            AirQualityIndexStandard.US -> 500.00f
            AirQualityIndexStandard.EUROPEAN -> 250.00f
        }
        return (aqi / max).coerceIn(0.00f, 1.00f)
    }

    fun getAqiCategory(
        aqi: Int,
        standard: AirQualityIndexStandard,
    ): AirQualityIndexCategory =
        when (standard) {
            AirQualityIndexStandard.EUROPEAN -> AirQualityLevel.fromAqi(aqi)
            AirQualityIndexStandard.US -> AirQualityLevelUs.fromAqi(aqi)
        }

    fun getAqiScale(
        standard: AirQualityIndexStandard,
    ): List<AirQualityIndexCategory> =
        when (standard) {
            AirQualityIndexStandard.EUROPEAN -> AirQualityLevel.entries
            AirQualityIndexStandard.US -> AirQualityLevelUs.entries
        }.sortedBy { it.severity }
}

data class AirQualityCurrently(
    val carbonMonoxide: Double?,
    val lastUpdatedInMilli: Long,
    val nitrogenDioxide: Double?,
    val ozone: Double?,
    val pm10: Double?,
    val pm25: Double?,
    val sulphurDioxide: Double?,
    val usAqi: Int?,
)

data class AirQualityHourly(
    val carbonMonoxide: Double?,
    val nitrogenDioxide: Double?,
    val ozone: Double?,
    val pm10: Double?,
    val pm25: Double?,
    val sulphurDioxide: Double?,
    val time: Long,
    val usAqi: Int?,
)
