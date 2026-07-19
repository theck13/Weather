package com.pranshulgg.weather_master_app.feature.main.ui.weatheranimations

import androidx.compose.runtime.Composable
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

@Composable
fun WeatherAnimations(
    isFroggyLayout: Boolean,
    weather: Weather,
) {
    val condition = weather.current.weatherCondition
    val day = weather.daily.firstOrNull()
    val isDay = day?.let { weather.current.time in day.sunrise..day.sunset } ?: true

    when (condition) {
        WeatherCondition.CLEAR_SKY ->
            if (isDay) {
                SunCanvas(
                    isFroggyLayout = isFroggyLayout,
                )
            } else {
                StarsCanvas()
            }

        WeatherCondition.MOSTLY_CLEAR, WeatherCondition.PARTLY_CLOUDY ->
            if (isDay) {
                SunCanvas(
                    isFroggyLayout = isFroggyLayout,
                    showClouds = true,
                )
            } else {
                StarsCanvas(
                    showClouds = true,
                )
            }

        WeatherCondition.LIGHT_RAIN -> RainCanvas(
            isFroggyLayout = isFroggyLayout,
            rainDropCount = 30,
        )

        WeatherCondition.RAIN -> RainCanvas(
            isFroggyLayout = isFroggyLayout,
            rainDropCount = 50,
        )

        WeatherCondition.HEAVY_RAIN -> RainCanvas(
            isFroggyLayout = isFroggyLayout,
            rainDropCount = 80,
        )

        WeatherCondition.OVERCAST -> OvercastCanvas()

        WeatherCondition.SNOW -> SnowCanvas(
            isFroggyLayout = isFroggyLayout,
        )

        WeatherCondition.LIGHT_SNOW -> SnowCanvas(
            isFroggyLayout = isFroggyLayout,
            snowFlakeCount = 15,
        )

        WeatherCondition.HEAVY_SNOW -> SnowCanvas(
            isFroggyLayout = isFroggyLayout,
            snowFlakeCount = 50,
        )

        WeatherCondition.FOG_HAZE -> FogHazeCanvas(
            isFroggyLayout = isFroggyLayout,
        )

        WeatherCondition.THUNDERSTORM -> RainCanvas(
            isFroggyLayout = isFroggyLayout,
            isStorming = true,
            rainDropCount = 50,
        )

        else -> StarsCanvas()
    }
}
