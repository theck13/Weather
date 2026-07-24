package com.heckofanapp.weather.core.network.sources.weather.accu.json

import com.google.gson.annotations.SerializedName

data class AccuCurrentWeatherJson(
    @SerializedName("DewPoint") val dewPoint: AccuCurrentWeatherDewPointJson,
    @SerializedName("RelativeHumidity") val humidity: Int?,
    @SerializedName("WeatherIcon") val icon: Int,
    @SerializedName("Pressure") val pressure: AccuCurrentWeatherPressureJson,
    @SerializedName("Temperature") val temperature: AccuCurrentWeatherTemperatureJson,
    @SerializedName("RealFeelTemperature") val temperatureFeels: AccuCurrentWeatherFeelsLikeJson,
    @SerializedName("EpochTime") val time: Long,
    @SerializedName("UVIndexFloat") val ultraviolet: Double?,
    @SerializedName("Visibility") val visibility: AccuCurrentWeatherVisibilityJson,
    @SerializedName("Wind") val wind: AccuCurrentWeatherWindJson,
)

data class AccuCurrentWeatherDewPointJson(
    @SerializedName("Metric") val metric: AccuCurrentWeatherDewPointValueJson,
)

data class AccuCurrentWeatherDewPointValueJson(
    @SerializedName("Value") val value: Double?,
)

data class AccuCurrentWeatherFeelsLikeJson(
    @SerializedName("Metric") val metric: AccuCurrentWeatherFeelsLikeValueJson,
)

data class AccuCurrentWeatherFeelsLikeValueJson(
    @SerializedName("Value") val value: Double?,
)

data class AccuCurrentWeatherPressureJson(
    @SerializedName("Metric") val metric: AccuCurrentWeatherPressureValueJson,
)

data class AccuCurrentWeatherPressureValueJson(
    @SerializedName("Value") val value: Double?,
)

data class AccuCurrentWeatherTemperatureJson(
    @SerializedName("Metric") val metric: AccuCurrentWeatherTemperatureValueJson,
)

data class AccuCurrentWeatherTemperatureValueJson(
    @SerializedName("Value") val value: Double,
)

data class AccuCurrentWeatherVisibilityJson(
    @SerializedName("Metric") val metric: AccuCurrentWeatherVisibilityValueJson,
)

data class AccuCurrentWeatherVisibilityValueJson(
    @SerializedName("Value") val value: Double?,
)

data class AccuCurrentWeatherWindJson(
    @SerializedName("Direction") val direction: AccuCurrentWeatherWindDirection,
    @SerializedName("Speed") val speed: AccuCurrentWeatherWindSpeedJson,
)

data class AccuCurrentWeatherWindDirection(
    @SerializedName("Degrees") val degrees: Int?,
)

data class AccuCurrentWeatherWindSpeedJson(
    @SerializedName("Metric") val metric: AccuCurrentWeatherWindSpeedValueJson,
)

data class AccuCurrentWeatherWindSpeedValueJson(
    @SerializedName("Value") val value: Double?,
)

// HOURLY

data class AccuHourlyWeatherJson(
    @SerializedName("DewPoint") val dewPoint: AccuHourlyWeatherDewPointValueJson,
    @SerializedName("RelativeHumidity") val humidity: Int?,
    @SerializedName("WeatherIcon") val icon: Int,
    @SerializedName("PrecipitationProbability") val precipitation: Int?,
    @SerializedName("Rain") val rain: AccuHourlyWeatherRainValueJson,
    @SerializedName("Snow") val snowCm: AccuHourlyWeatherSnowValueJson,
    @SerializedName("Temperature") val temperature: AccuHourlyWeatherTemperatureValueJson,
    @SerializedName("RealFeelTemperature") val temperatureFeels: AccuHourlyWeatherFeelsLikeTemperatureValueJson,
    @SerializedName("EpochDateTime") val time: Long,
    @SerializedName("UVIndexFloat") val ultraviolet: Double?,
    @SerializedName("Visibility") val visibility: AccuHourlyWeatherVisibilityValueJson,
    @SerializedName("Wind") val wind: AccuHourlyWeatherWindJson,
)

data class AccuHourlyWeatherDewPointValueJson(
    @SerializedName("Value") val value: Double?,
)

data class AccuHourlyWeatherFeelsLikeTemperatureValueJson(
    @SerializedName("Value") val value: Double?,
)

data class AccuHourlyWeatherRainValueJson(
    @SerializedName("Value") val value: Double?,
)

data class AccuHourlyWeatherSnowValueJson(
    @SerializedName("Value") val value: Double?,
)

data class AccuHourlyWeatherTemperatureValueJson(
    @SerializedName("Value") val value: Double,
)

data class AccuHourlyWeatherVisibilityValueJson(
    @SerializedName("Value") val value: Double?,
)

data class AccuHourlyWeatherWindJson(
    @SerializedName("Direction") val direction: AccuHourlyWeatherWindDirectionValueJson,
    @SerializedName("Speed") val speed: AccuHourlyWeatherWindSpeedValueJson,
)

data class AccuHourlyWeatherWindDirectionValueJson(
    @SerializedName("Degrees") val degrees: Int?,
)

data class AccuHourlyWeatherWindSpeedValueJson(
    @SerializedName("Value") val value: Double?,
)

// DAILY

data class AccuDailyWeatherJson(
    @SerializedName("DailyForecasts") val daily: List<AccuDailyWeatherItemJson>,
)

data class AccuDailyWeatherDataJson(
    @SerializedName("RelativeHumidity") val humidity: AccuDailyWeatherHumidityValueJson,
    @SerializedName("Icon") val icon: Int,
    @SerializedName("PrecipitationProbability") val precipitation: Int?,
    @SerializedName("Rain") val rain: AccuDailyWeatherRainValueJson,
    @SerializedName("Snow") val snowCm: AccuDailyWeatherSnowValueJson,
    @SerializedName("UVIndexFloat") val ultraviolet: AccuDailyWeatherUltravioletValueJson,
    @SerializedName("Wind") val wind: AccuDailyWeatherWindJson,
)

data class AccuDailyWeatherHumidityValueJson(
    @SerializedName("Average") val value: Int?,
)

data class AccuDailyWeatherItemJson(
    @SerializedName("Day") val day: AccuDailyWeatherDataJson,
    @SerializedName("Night") val night: AccuDailyWeatherDataJson,
    @SerializedName("Temperature") val temperature: AccuDailyWeatherTemperatureJson,
    @SerializedName("EpochDate") val time: Long,
)

data class AccuDailyWeatherRainValueJson(
    @SerializedName("Value") val value: Double?,
)

data class AccuDailyWeatherSnowValueJson(
    @SerializedName("Value") val value: Double?,
)

data class AccuDailyWeatherTemperatureJson(
    @SerializedName("Maximum") val maximum: AccuDailyWeatherTemperatureMaximumValueJson,
    @SerializedName("Minimum") val minimum: AccuDailyWeatherTemperatureMinimumValueJson,
)

data class AccuDailyWeatherTemperatureMaximumValueJson(
    @SerializedName("Value") val value: Double,
)

data class AccuDailyWeatherTemperatureMinimumValueJson(
    @SerializedName("Value") val value: Double,
)

data class AccuDailyWeatherUltravioletValueJson(
    @SerializedName("Maximum") val value: Double?,
)

data class AccuDailyWeatherWindJson(
    @SerializedName("Direction") val direction: AccuDailyWeatherWindDirectionValueJson,
    @SerializedName("Speed") val speed: AccuDailyWeatherWindSpeedValueJson,
)

data class AccuDailyWeatherWindDirectionValueJson(
    @SerializedName("Degrees") val degrees: Double?,
)

data class AccuDailyWeatherWindSpeedValueJson(
    @SerializedName("Value") val value: Double?,
)
