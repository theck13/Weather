package com.heckofanapp.weather.core.network.sources.weather.google.json

import com.google.gson.annotations.SerializedName

/**
 * Shared value objects.  Every measurement comes back wrapped with unit it was measure in.
 * Requests always ask for Metric.  So, units are known up front and only values are parsed:
 * °C, km/h, hPa (millibars), km, and mm.
 */
data class GoogleAirPressureJson(
    val meanSeaLevelMillibars: Double?,
)

data class GoogleDateJson(
    val day: Int?,
    val month: Int?,
    val year: Int?,
)

data class GoogleIntervalJson(
    val startTime: String?,
)

data class GooglePrecipitationJson(
    val probability: GooglePrecipitationProbabilityJson?,
    val qpf: GooglePrecipitationAmountJson?,
    val snowQpf: GooglePrecipitationAmountJson?,
)

data class GooglePrecipitationAmountJson(
    val quantity: Double?,
)

data class GooglePrecipitationProbabilityJson(
    val percent: Int?,
)

data class GoogleTemperatureJson(
    val degrees: Double?,
)

data class GoogleVisibilityJson(
    val distance: Double?,
)

data class GoogleWeatherConditionJson(
    val type: String?,
)

data class GoogleWindJson(
    val direction: GoogleWindDirectionJson?,
    val speed: GoogleWindSpeedJson?,
)

data class GoogleWindDirectionJson(
    val degrees: Int?,
)

data class GoogleWindSpeedJson(
    val value: Double?,
)

data class GoogleCurrentWeatherJson(
    val cloudCover: Int?,
    val currentTime: String?,
    val dewPoint: GoogleTemperatureJson?,
    @SerializedName("relativeHumidity") val humidity: Int?,
    @SerializedName("airPressure") val pressure: GoogleAirPressureJson?,
    val temperature: GoogleTemperatureJson?,
    @SerializedName("feelsLikeTemperature") val temperatureFeels: GoogleTemperatureJson?,
    @SerializedName("uvIndex") val ultraviolet: Double?,
    val visibility: GoogleVisibilityJson?,
    val weatherCondition: GoogleWeatherConditionJson?,
    val wind: GoogleWindJson?,
)

data class GoogleHourlyWeatherJson(
    val forecastHours: List<GoogleHourlyWeatherItemJson>,
    val nextPageToken: String?,
)

data class GoogleHourlyWeatherItemJson(
    val dewPoint: GoogleTemperatureJson?,
    @SerializedName("relativeHumidity") val humidity: Int?,
    val interval: GoogleIntervalJson?,
    val precipitation: GooglePrecipitationJson?,
    @SerializedName("airPressure") val pressure: GoogleAirPressureJson?,
    val temperature: GoogleTemperatureJson?,
    @SerializedName("uvIndex") val ultraviolet: Double?,
    val visibility: GoogleVisibilityJson?,
    val weatherCondition: GoogleWeatherConditionJson?,
    val wind: GoogleWindJson?,
)

data class GoogleDailyWeatherJson(
    val forecastDays: List<GoogleDailyWeatherItemJson>,
)

data class GoogleDailyWeatherItemJson(
    @SerializedName("daytimeForecast") val day: GoogleDailyWeatherPartJson?,
    val displayDate: GoogleDateJson?,
    val interval: GoogleIntervalJson?,
    @SerializedName("nighttimeForecast") val night: GoogleDailyWeatherPartJson?,
    @SerializedName("maxTemperature") val temperatureMaximum: GoogleTemperatureJson?,
    @SerializedName("minTemperature") val temperatureMinimum: GoogleTemperatureJson?,
)

/**
 * Day is split in two halves rather than aggregated: 7am-7pm and 7pm-7am next day, both local time.
 */
data class GoogleDailyWeatherPartJson(
    @SerializedName("relativeHumidity") val humidity: Int?,
    val precipitation: GooglePrecipitationJson?,
    @SerializedName("uvIndex") val ultraviolet: Double?,
    val weatherCondition: GoogleWeatherConditionJson?,
    val wind: GoogleWindJson?,
)
