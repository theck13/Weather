package com.heckofanapp.weather.core.network.sources.weather.meteoam.json

import com.google.gson.annotations.SerializedName

data class MeteoAmCurrentWeatherJson(
    val datasets: MeteoAmCurrentWeatherDatasetsJson,
    @SerializedName("timeseries") val timeSeries: List<List<String>>,
)

data class MeteoAmCurrentWeatherDatasetsJson(
    @SerializedName("0") val current: MeteoAmCurrentWeatherDatasetValueJson,
)

data class MeteoAmCurrentWeatherDatasetValueJson(
    @SerializedName("0") val temperature: MeteoAmCurrentWeatherDatasetValueTemperatureJson,
    @SerializedName("1") val humidity: MeteoAmCurrentWeatherDatasetValueHumidityJson,
    @SerializedName("2") val pressure: MeteoAmCurrentWeatherDatasetValuePressureJson,
    @SerializedName("3") val windDirection: MeteoAmCurrentWeatherDatasetValueWindDirectionJson,
    @SerializedName("6") val windSpeedKmh: MeteoAmCurrentWeatherDatasetValueWindSpeedJson,
    @SerializedName("8") val icon: MeteoAmCurrentWeatherDatasetValueIconJson,
)

data class MeteoAmCurrentWeatherDatasetValueTemperatureJson(
    @SerializedName("0") val value: Double?,
)

data class MeteoAmCurrentWeatherDatasetValueHumidityJson(
    @SerializedName("0") val value: Double?,
)

data class MeteoAmCurrentWeatherDatasetValuePressureJson(
    @SerializedName("0") val value: Double?,
)

data class MeteoAmCurrentWeatherDatasetValueWindDirectionJson(
    @SerializedName("0") val value: String?, // Could be either "VRB" or a number
)

data class MeteoAmCurrentWeatherDatasetValueWindSpeedJson(
    @SerializedName("0") val value: Double?,
)

data class MeteoAmCurrentWeatherDatasetValueIconJson(
    @SerializedName("0") val value: String?,
)

// FORECAST

data class MeteoAmForecastWeatherJson(
    val datasets: MeteoAmForecastWeatherDatasetsJson,
    @SerializedName("timeseries") val timeSeries: List<String>,
)

data class MeteoAmForecastWeatherDatasetsJson(
    @SerializedName("0") val forecast: MeteoAmForecastWeatherDataJson,
)

data class MeteoAmForecastWeatherDataJson(
    @SerializedName("0") val temperature: Map<String, Double?>?,
    @SerializedName("1") val humidity: Map<String, Double?>?,
    @SerializedName("2") val pressure: Map<String, Double?>?,
    @SerializedName("3") val precipitationProbability: Map<String, Double?>?, // why is it ttp?
    @SerializedName("4") val windDirection: Map<String, String?>?,
    @SerializedName("7") val windSpeedKmh: Map<String, Double?>?,
    @SerializedName("9") val icon: Map<String, String>,
)
