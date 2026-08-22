package com.heckofanapp.weather.core.network.sources.weather.pirate.json

import com.google.gson.annotations.SerializedName

/**
 * Requests always ask for si units, version 2, and pirate icons.  So, units are known up front:
 * °C, m/s, hPa, km capped at 16, mm/h for intensity, and cm for accumulation.  Humidity, cloud
 * cover, and precipitation probability come back as fractions of one.  Every time is unixtime
 * in seconds.
 */
data class PirateJson(
    val currently: PirateCurrentJson?,
    val daily: PirateDailyJson?,
    val hourly: PirateHourlyJson?,
)

data class PirateCurrentJson(
    val cloudCover: Double?,
    val dewPoint: Double?,
    val humidity: Double?,
    val icon: String?,
    val pressure: Double?,
    val temperature: Double?,
    @SerializedName("apparentTemperature") val temperatureFeels: Double?,
    val time: Long?,
    @SerializedName("uvIndex") val ultraviolet: Double?,
    val visibility: Double?,
    @SerializedName("windBearing") val windDirection: Double?,
    val windSpeed: Double?,
)

data class PirateHourlyJson(
    val data: List<PirateHourlyItemJson>,
)

data class PirateHourlyItemJson(
    val dewPoint: Double?,
    val humidity: Double?,
    val icon: String?,
    @SerializedName("precipProbability") val precipitationProbability: Double?,
    val pressure: Double?,
    @SerializedName("rainIntensity") val rain: Double?, // mm/h, which over one hour is mm.
    @SerializedName("snowAccumulation") val snowfall: Double?,
    val temperature: Double?,
    val time: Long?,
    @SerializedName("uvIndex") val ultraviolet: Double?,
    val visibility: Double?,
    @SerializedName("windBearing") val windDirection: Double?,
    val windSpeed: Double?,
)

data class PirateDailyJson(
    val data: List<PirateDailyItemJson>,
)

/**
 * Sunrise, sunset, and moon phase come back here too but are ignored, since app always
 * computes those locally.  Temperature high and low only cover daylight stretch.  So,
 * maximum and minimum are taken instead as they span whole day.
 */
data class PirateDailyItemJson(
    val dewPoint: Double?,
    val humidity: Double?,
    val icon: String?,
    @SerializedName("precipProbability") val precipitationProbability: Double?,
    val pressure: Double?,
    @SerializedName("liquidAccumulation") val rainSum: Double?,
    @SerializedName("snowAccumulation") val snowfallSum: Double?,
    @SerializedName("temperatureMax") val temperatureMaximum: Double?,
    @SerializedName("temperatureMin") val temperatureMinimum: Double?,
    val time: Long?,
    @SerializedName("uvIndex") val ultraviolet: Double?,
    val visibility: Double?,
    @SerializedName("windBearing") val windDirection: Double?,
    val windSpeed: Double?,
)
