package com.heckofanapp.weather.core.network.sources.weather.pirate

import com.heckofanapp.weather.core.model.weather.WeatherCondition

/**
 * Source: https://docs.pirateweather.net/en/latest/API/#icon
 *
 * Requests ask for Pirate icon set, which splits Dark Sky's original eleven into finer
 * conditions.  Day and night halves collapse together, since app picks artwork by sun
 * times rather than by icon name.
 */
object PirateConditionMap {
    fun getCondition(
        icon: String?,
    ): WeatherCondition {
        return when (icon) {
            // TODO: Add missing in-app conditions.
            "clear-day",
            "clear-night" -> WeatherCondition.CLEAR_SKY
            "mostly-clear-day",
            "mostly-clear-night" -> WeatherCondition.MOSTLY_CLEAR
            "partly-cloudy-day",
            "partly-cloudy-night" -> WeatherCondition.PARTLY_CLOUDY
            "cloudy",
            "mostly-cloudy-day",
            "mostly-cloudy-night" -> WeatherCondition.OVERCAST
            "breezy", // SHOULD BE WIND
            "dangerous-wind", // SHOULD BE WIND
            "wind" -> WeatherCondition.OVERCAST // SHOULD BE WIND
            "fog",
            "haze",
            "mist",
            "smoke" -> WeatherCondition.FOG_HAZE
            "drizzle",
            "light-rain",
            "possible-rain-day",
            "possible-rain-night" -> WeatherCondition.LIGHT_RAIN
            "possible-precipitation-day",
            "possible-precipitation-night",
            "precipitation",
            "rain" -> WeatherCondition.RAIN
            "heavy-rain" -> WeatherCondition.HEAVY_RAIN
            "flurries",
            "light-snow",
            "possible-snow-day",
            "possible-snow-night" -> WeatherCondition.LIGHT_SNOW
            "hail", // SHOULD BE HAIL
            "light-sleet", // SHOULD BE SLEET
            "mixed", // SHOULD BE SLEET
            "possible-sleet-day", // SHOULD BE SLEET
            "possible-sleet-night", // SHOULD BE SLEET
            "sleet", // SHOULD BE SLEET
            "very-light-sleet", // SHOULD BE SLEET
            "snow" -> WeatherCondition.SNOW
            "heavy-sleet", // SHOULD BE SLEET
            "heavy-snow" -> WeatherCondition.HEAVY_SNOW
            "possible-thunderstorm-day",
            "possible-thunderstorm-night",
            "thunderstorm" -> WeatherCondition.THUNDERSTORM
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}
