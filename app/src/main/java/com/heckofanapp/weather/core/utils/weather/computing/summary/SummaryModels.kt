package com.heckofanapp.weather.core.utils.weather.computing.summary

data class SummaryPeakRain(
    val amount: Double,
    val at: Long,
    val probability: Int,
)

data class SummaryPeakSnow(
    val amount: Double,
    val at: Long,
    val probability: Int,
)

data class SummaryPeakUv(
    val at: Long,
    val uv: Double,
)

data class SummaryTemperatures(
    val average: Double,
    val temperatureMaximum: Double,
    val temperatureMinimum: Double,
)

data class SummaryData(
    val condition: String,
    val rain: SummaryPeakRain,
    val snow: SummaryPeakSnow,
    val temperatures: SummaryTemperatures,
    val uv: SummaryPeakUv,
)
