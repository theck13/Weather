package com.heckofanapp.weather.core.network.sources.weather.openweather

import com.heckofanapp.weather.core.model.weather.WeatherCondition

/**
 * Source: https://openweathermap.org/weather-conditions
 *
 * Conditions arrive as numeric id grouped by family: two hundreds thunderstorm, three hundreds
 * drizzle, five hundreds rain, six hundreds snow, seven hundreds atmosphere, and eight hundreds
 * clear through overcast.  Day and night never split, since app picks artwork by sun times
 * rather than by icon name.
 */
object OpenWeatherConditionMap {
    fun getCondition(
        id: Int?,
    ): WeatherCondition {
        return when (id) {
            // TODO: Add missing in-app conditions.
            800 -> WeatherCondition.CLEAR_SKY
            801 -> WeatherCondition.MOSTLY_CLEAR // Few clouds, covering a quarter of sky at most.
            802 -> WeatherCondition.PARTLY_CLOUDY // Scattered clouds, covering up to half of sky.
            803, // Broken clouds, covering more than half of sky.
            804 -> WeatherCondition.OVERCAST
            771 -> WeatherCondition.OVERCAST // SHOULD BE WIND
            701, // Mist.
            711, // Smoke.
            721, // Haze.
            731, // Sand and dust whirls.
            741, // Fog.
            751, // Sand.
            761, // Dust.
            762 -> WeatherCondition.FOG_HAZE // Volcanic ash.
            in 300..321, // Every drizzle intensity stays lighter than light rain.
            500,
            520 -> WeatherCondition.LIGHT_RAIN
            501,
            521,
            531 -> WeatherCondition.RAIN
            502,
            503,
            504,
            522 -> WeatherCondition.HEAVY_RAIN
            600,
            620 -> WeatherCondition.LIGHT_SNOW
            511, // SHOULD BE SLEET
            601,
            611, // SHOULD BE SLEET
            612, // SHOULD BE SLEET
            613, // SHOULD BE SLEET
            615, // SHOULD BE SLEET
            616, // SHOULD BE SLEET
            621 -> WeatherCondition.SNOW
            602,
            622 -> WeatherCondition.HEAVY_SNOW
            in 200..232 -> WeatherCondition.THUNDERSTORM
            781 -> WeatherCondition.THUNDERSTORM // SHOULD BE TORNADO
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}
