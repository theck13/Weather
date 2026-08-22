package com.heckofanapp.weather.core.network.sources.weather.google

import com.heckofanapp.weather.core.model.weather.WeatherCondition

/**
 * Source: https://developers.google.com/maps/documentation/weather/weather-condition-icons
 *
 * Only base conditions are returned here.  computeDailyWeatherCondition groups these into
 * combined daily conditions.  So, it can't take them as input.
 */
object GoogleWeatherConditionMap {
    fun getCondition(
        type: String?,
    ): WeatherCondition {
        return when (type) {
            // TODO: Add missing in-app conditions.
            "CLEAR" -> WeatherCondition.CLEAR_SKY
            "MOSTLY_CLEAR" -> WeatherCondition.MOSTLY_CLEAR
            "PARTLY_CLOUDY" -> WeatherCondition.PARTLY_CLOUDY
            "CLOUDY",
            "MOSTLY_CLOUDY" -> WeatherCondition.OVERCAST
            "WINDY" -> WeatherCondition.OVERCAST // SHOULD BE WIND
            "CHANCE_OF_SHOWERS",
            "LIGHT_RAIN",
            "LIGHT_RAIN_SHOWERS",
            "SCATTERED_SHOWERS" -> WeatherCondition.LIGHT_RAIN
            "LIGHT_TO_MODERATE_RAIN",
            "RAIN",
            "RAIN_SHOWERS",
            "WIND_AND_RAIN" -> WeatherCondition.RAIN
            "HEAVY_RAIN",
            "HEAVY_RAIN_SHOWERS",
            "MODERATE_TO_HEAVY_RAIN",
            "RAIN_PERIODICALLY_HEAVY" -> WeatherCondition.HEAVY_RAIN
            "CHANCE_OF_SNOW_SHOWERS",
            "LIGHT_SNOW",
            "LIGHT_SNOW_SHOWERS",
            "SCATTERED_SNOW_SHOWERS" -> WeatherCondition.LIGHT_SNOW
            "HAIL", // SHOULD BE HAIL
            "HAIL_SHOWERS", // SHOULD BE HAIL
            "LIGHT_TO_MODERATE_SNOW",
            "RAIN_AND_SNOW", // SHOULD BE SLEET
            "SNOW",
            "SNOW_SHOWERS" -> WeatherCondition.SNOW
            "BLOWING_SNOW", // SHOULD BE BLIZZARD
            "HEAVY_SNOW",
            "HEAVY_SNOW_SHOWERS",
            "HEAVY_SNOW_STORM",
            "MODERATE_TO_HEAVY_SNOW",
            "SNOWSTORM",
            "SNOW_PERIODICALLY_HEAVY" -> WeatherCondition.HEAVY_SNOW
            "HEAVY_THUNDERSTORM",
            "LIGHT_THUNDERSTORM_RAIN",
            "SCATTERED_THUNDERSTORMS",
            "THUNDERSHOWER",
            "THUNDERSTORM" -> WeatherCondition.THUNDERSTORM
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}
