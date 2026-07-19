package com.heckofanapp.weather.core.model.domain.weather

import com.heckofanapp.weather.core.model.astro.MoonPhase
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.wind.WindDirection

data class Weather(
    val location: Location,
    val current: WeatherCurrently,
    val hourly: List<WeatherHourly>,
    val daily: List<WeatherDaily>,
)

data class WeatherCurrently(
    val temperature: Double?, // NOTE: ALWAYS C
    val humidity: Double,
    val windSpeed: Double?, // NOTE: ALWAYS KMH
    val windDirection: WindDirection?, // NOTE: ALWAYS DOMINANT
    val pressureMsl: Double?, // NOTE: ALWAYS HPA
    val visibility: Int?,  // NOTE: ALWAYS METERS
    val cloudCover: Double?,
    val ultraviolet: Double?,
    val weatherCondition: WeatherCondition,
    val feelsLike: Double?,
    val time: Long, // NOTE: ALWAYS MILLISECONDS
    val dewPoint: Double?,
    val utcOffsetSeconds: Long?,
    val lastUpdatedInMilli: Long,
) {
    fun isDewPointValid(): Boolean {
        return dewPoint != null
    }

    fun isPressureValid(): Boolean {
        return pressureMsl != null
    }

    fun isWindDirectionValid(): Boolean {
        return windDirection != null
    }

    fun isWindSpeedValid(): Boolean {
        return windSpeed != null
    }

    fun isUltravioletValid(): Boolean {
        return ultraviolet != null
    }

    fun isVisibilityValid(): Boolean {
        return visibility != null
    }
}

data class WeatherDaily(
    val temperatureMin: Double?, // NOTE: ALWAYS C
    val temperatureMax: Double?, // NOTE: ALWAYS C
    val windSpeed: Double?, // NOTE: ALWAYS KMH (Average)
    val windDirection: WindDirection?, // NOTE: ALWAYS DOMINANT
    val rainSum: Double,  // NOTE: ALWAYS MM
    val snowfallSum: Double?, // NOTE: ALWAYS CM
    val ultravioletMaximum: Double?,
    val weatherCondition: WeatherCondition,
    val time: Long, // NOTE: ALWAYS MILLISECONDS
    val precipitationProbabilityMax: Int?,
    val pressureMsl: Double?, // NOTE: ALWAYS HPA (Average)
    val visibility: Int?,  // NOTE: ALWAYS METERS (Minimum)
    val humidity: Double?, // (Average)
    val dewPoint: Double?, // (Average)
    val sunrise: Long, // NOTE: ALWAYS MILLISECONDS
    val sunset: Long, // NOTE: ALWAYS MILLISECONDS
    val moonrise: Long, // NOTE: ALWAYS MILLISECONDS
    val moonset: Long, // NOTE: ALWAYS MILLISECONDS
    val moonPhase: MoonPhase,
    val dawn: Long,
    val dusk: Long,
) {

    fun isHumidityValid(): Boolean {
        return humidity != null && humidity != -1.0
    }

    fun isPrecipitationProbabilityMaxValid(): Boolean {
        return precipitationProbabilityMax != null
    }

    fun isPressureValid(): Boolean {
        return pressureMsl != null && pressureMsl != -1.0
    }

    fun isUltravioletMaxValid(): Boolean {
        return ultravioletMaximum != null
    }

    fun isVisibilityValid(): Boolean {
        return visibility != null && visibility != -1
    }

    fun isWindDirectionValid(): Boolean {
        return windDirection != null
    }

    fun isWindSpeedValid(): Boolean {
        return windSpeed != null
    }
}

data class WeatherHourly(
    val temperature: Double?, // NOTE: ALWAYS C
    val windSpeed: Double?, // NOTE: ALWAYS KMH
    val windDirection: WindDirection?, // NOTE: ALWAYS DOMINANT
    val rain: Double,  // NOTE: ALWAYS MM
    val snowfall: Double?, // NOTE: ALWAYS CM
    val ultraviolet: Double?,
    val pressureMsl: Double?, // NOTE: ALWAYS HPA
    val visibility: Int?,  // NOTE: ALWAYS METERS
    val humidity: Double?,
    val dewPoint: Double?,
    val weatherCondition: WeatherCondition,
    val time: Long,  // NOTE: ALWAYS MILLISECONDS
    val precipitationProbability: Int?,
) {
    fun isUvIndexValid(): Boolean {
        return ultraviolet != null
    }

    fun isWindDirectionValid(): Boolean {
        return windDirection != null
    }

    fun isWindSpeedValid(): Boolean {
        return windSpeed != null
    }
}
