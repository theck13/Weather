package com.heckofanapp.weather.core.network.sources.weather.openweather.json

import com.google.gson.annotations.SerializedName

/**
 * Requests always ask for metric units.  So, units are known up front: °C, m/s, hPa, meters
 * capped at ten thousand, and mm for both rain and snow.  Humidity and cloud cover arrive as
 * whole percentages while precipitation probability arrives as fraction of one.  Every time is
 * unixtime in seconds.
 */
data class OpenWeatherJson(
    val current: OpenWeatherCurrentJson?,
    val daily: List<OpenWeatherDailyItemJson>?,
    val hourly: List<OpenWeatherHourlyItemJson>?,
)

/**
 * Condition list carries one entry per request, and first entry is the primary condition.
 * Only numeric id is read, since text and icon name both restate what id already says.
 */
data class OpenWeatherConditionJson(
    val id: Int?,
)

// Hourly rain and snow arrive as objects keyed by accumulation window rather than as plain numbers.
data class OpenWeatherPrecipitationJson(
    @SerializedName("1h") val lastHour: Double?,
)

data class OpenWeatherCurrentJson(
    @SerializedName("clouds") val cloudCover: Double?,
    @SerializedName("dew_point") val dewPoint: Double?,
    val humidity: Double?,
    val pressure: Double?,
    @SerializedName("temp") val temperature: Double?,
    @SerializedName("feels_like") val temperatureFeels: Double?,
    @SerializedName("dt") val time: Long?,
    @SerializedName("uvi") val ultraviolet: Double?,
    val visibility: Int?,
    val weather: List<OpenWeatherConditionJson>?,
    @SerializedName("wind_deg") val windDirection: Double?,
    @SerializedName("wind_speed") val windSpeed: Double?,
)

data class OpenWeatherHourlyItemJson(
    @SerializedName("dew_point") val dewPoint: Double?,
    val humidity: Double?,
    @SerializedName("pop") val precipitationProbability: Double?,
    val pressure: Double?,
    val rain: OpenWeatherPrecipitationJson?,
    @SerializedName("snow") val snowfall: OpenWeatherPrecipitationJson?,
    @SerializedName("temp") val temperature: Double?,
    @SerializedName("dt") val time: Long?,
    @SerializedName("uvi") val ultraviolet: Double?,
    val visibility: Int?,
    val weather: List<OpenWeatherConditionJson>?,
    @SerializedName("wind_deg") val windDirection: Double?,
    @SerializedName("wind_speed") val windSpeed: Double?,
)

// Day, night, evening, and morning temperatures come back here too but are ignored, since app
// shows whole day span instead.
data class OpenWeatherDailyTemperatureJson(
    @SerializedName("max") val maximum: Double?,
    @SerializedName("min") val minimum: Double?,
)

/**
 * Sunrise, sunset, moonrise, moonset, and moon phase come back here too but are ignored, since
 * app always computes those locally.  Visibility is absent from daily altogether, so it is
 * backfilled from supplemental Open Meteo call.
 */
data class OpenWeatherDailyItemJson(
    @SerializedName("dew_point") val dewPoint: Double?,
    val humidity: Double?,
    @SerializedName("pop") val precipitationProbability: Double?,
    val pressure: Double?,
    @SerializedName("rain") val rainSum: Double?,
    @SerializedName("snow") val snowfallSum: Double?,
    @SerializedName("temp") val temperature: OpenWeatherDailyTemperatureJson?,
    @SerializedName("dt") val time: Long?,
    @SerializedName("uvi") val ultraviolet: Double?,
    val weather: List<OpenWeatherConditionJson>?,
    @SerializedName("wind_deg") val windDirection: Double?,
    @SerializedName("wind_speed") val windSpeed: Double?,
)
