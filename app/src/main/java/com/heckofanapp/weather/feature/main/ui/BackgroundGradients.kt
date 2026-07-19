package com.heckofanapp.weather.feature.main.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.ui.theme.ClearSkyDayDarkEnd
import com.heckofanapp.weather.core.ui.theme.ClearSkyDayDarkStart
import com.heckofanapp.weather.core.ui.theme.ClearSkyDayLightEnd
import com.heckofanapp.weather.core.ui.theme.ClearSkyDayLightStart
import com.heckofanapp.weather.core.ui.theme.ClearSkyNightDarkEnd
import com.heckofanapp.weather.core.ui.theme.ClearSkyNightDarkStart
import com.heckofanapp.weather.core.ui.theme.ClearSkyNightLightEnd
import com.heckofanapp.weather.core.ui.theme.ClearSkyNightLightStart
import com.heckofanapp.weather.core.ui.theme.FogHazeDarkEnd
import com.heckofanapp.weather.core.ui.theme.FogHazeDarkStart
import com.heckofanapp.weather.core.ui.theme.FogHazeLightEnd
import com.heckofanapp.weather.core.ui.theme.FogHazeLightStart
import com.heckofanapp.weather.core.ui.theme.MostlyClearPartlyCloudyDarkEnd
import com.heckofanapp.weather.core.ui.theme.MostlyClearPartlyCloudyDarkStart
import com.heckofanapp.weather.core.ui.theme.MostlyClearPartlyCloudyLightEnd
import com.heckofanapp.weather.core.ui.theme.MostlyClearPartlyCloudyLightStart
import com.heckofanapp.weather.core.ui.theme.OvercastDarkEnd
import com.heckofanapp.weather.core.ui.theme.OvercastDarkStart
import com.heckofanapp.weather.core.ui.theme.OvercastLightEnd
import com.heckofanapp.weather.core.ui.theme.OvercastLightStart
import com.heckofanapp.weather.core.ui.theme.RainDarkEnd
import com.heckofanapp.weather.core.ui.theme.RainDarkStart
import com.heckofanapp.weather.core.ui.theme.RainLightEnd
import com.heckofanapp.weather.core.ui.theme.RainLightStart
import com.heckofanapp.weather.core.ui.theme.SnowDarkEnd
import com.heckofanapp.weather.core.ui.theme.SnowDarkStart
import com.heckofanapp.weather.core.ui.theme.SnowLightEnd
import com.heckofanapp.weather.core.ui.theme.SnowLightStart
import com.heckofanapp.weather.core.ui.theme.ThunderstormDarkEnd
import com.heckofanapp.weather.core.ui.theme.ThunderstormDarkStart
import com.heckofanapp.weather.core.ui.theme.ThunderstormLightEnd
import com.heckofanapp.weather.core.ui.theme.ThunderstormLightStart
import com.heckofanapp.weather.core.ui.theme.isThemeDark
import com.heckofanapp.weather.core.utils.weather.cache.isWeatherDomainSafe

data class Background(
    val gradient: List<Color>,
    val scrollColor: Color,
)

@Composable
fun BackgroundGradient(
    isScrolled: Boolean = false,
    weather: Weather?,
) {
    val isDark = isThemeDark()

    Box(
        modifier = Modifier
            .background(
                color = backgroundGradients(
                    isDark = isDark,
                    weather = weather,
                ).scrollColor
            )
            .fillMaxSize(),
    )

    AnimatedVisibility(
        enter = fadeIn(),
        exit = fadeOut(),
        visible = isScrolled.not(),
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        backgroundGradients(
                            isDark = isDark,
                            weather = weather,
                        ).gradient,
                        startY = 0.00f,
                        endY = 1000.00f,
                    ),
                )
                .fillMaxSize(),
        )
    }
}

private fun backgroundGradients(
    isDark: Boolean = true,
    weather: Weather?,
): Background {
    if (isWeatherDomainSafe(weather).not()) return ColorsDark.CLEAR_SKY_DAY

    val condition = weather?.current?.weatherCondition
    val day = weather?.daily?.firstOrNull()
    val isDay = day?.let { weather.current.time in day.sunrise..day.sunset } ?: true

    val gradient = gradients(
        condition = condition ?: WeatherCondition.CLEAR_SKY,
        isDay = isDay,
        isDark = isDark,
    )
    return gradient
}

private fun gradients(
    condition: WeatherCondition,
    isDay: Boolean = true,
    isDark: Boolean = true,
): Background {
    val colors = if (isDark) ColorsDark else ColorsLight

    return when (condition) {
        WeatherCondition.CLEAR_SKY -> if (isDay) colors.CLEAR_SKY_DAY else colors.CLEAR_SKY_NIGHT
        WeatherCondition.FOG_HAZE -> colors.FOG_HAZE
        WeatherCondition.MOSTLY_CLEAR, WeatherCondition.PARTLY_CLOUDY -> colors.MOSTLY_CLEAR_PARTLY_CLOUDY
        WeatherCondition.OVERCAST -> colors.OVERCAST
        WeatherCondition.RAIN, WeatherCondition.HEAVY_RAIN, WeatherCondition.LIGHT_RAIN -> colors.RAIN
        WeatherCondition.SNOW, WeatherCondition.HEAVY_SNOW, WeatherCondition.LIGHT_SNOW -> colors.SNOW
        WeatherCondition.THUNDERSTORM -> colors.THUNDERSTORM
        else -> colors.CLEAR_SKY_DAY
    }
}

interface ColorPalette {
    val CLEAR_SKY_DAY: Background
    val CLEAR_SKY_NIGHT: Background
    val FOG_HAZE: Background
    val MOSTLY_CLEAR_PARTLY_CLOUDY: Background
    val OVERCAST: Background
    val RAIN: Background
    val SNOW: Background
    val THUNDERSTORM: Background
}

object ColorsDark : ColorPalette {
    override val CLEAR_SKY_DAY = Background(
        gradient = listOf(
            ClearSkyDayDarkStart,
            ClearSkyDayDarkEnd,
        ),
        scrollColor = ClearSkyDayDarkEnd,
    )

    override val CLEAR_SKY_NIGHT = Background(
        gradient = listOf(
            ClearSkyNightDarkStart,
            ClearSkyNightDarkEnd,
        ),
        scrollColor = ClearSkyNightDarkEnd,
    )

    override val FOG_HAZE = Background(
        gradient = listOf(
            FogHazeDarkStart,
            FogHazeDarkEnd,
        ),
        scrollColor = FogHazeDarkEnd,
    )

    override val MOSTLY_CLEAR_PARTLY_CLOUDY = Background(
        gradient = listOf(
            MostlyClearPartlyCloudyDarkStart,
            MostlyClearPartlyCloudyDarkEnd,
        ),
        scrollColor = MostlyClearPartlyCloudyDarkEnd,
    )

    override val OVERCAST = Background(
        gradient = listOf(
            OvercastDarkStart,
            OvercastDarkEnd,
        ),
        scrollColor = OvercastDarkEnd,
    )

    override val RAIN = Background(
        gradient = listOf(
            RainDarkStart,
            RainDarkEnd,
        ),
        scrollColor = RainDarkEnd,
    )

    override val SNOW = Background(
        gradient = listOf(
            SnowDarkStart,
            SnowDarkEnd,
        ),
        scrollColor = SnowDarkEnd,
    )

    override val THUNDERSTORM = Background(
        gradient = listOf(
            ThunderstormDarkStart,
            ThunderstormDarkEnd,
        ),
        scrollColor = ThunderstormDarkEnd,
    )
}

private object ColorsLight : ColorPalette {
    override val CLEAR_SKY_DAY = Background(
        gradient = listOf(
            ClearSkyDayLightStart,
            ClearSkyDayLightEnd,
        ),
        scrollColor = ClearSkyDayLightEnd,
    )

    override val CLEAR_SKY_NIGHT = Background(
        gradient = listOf(
            ClearSkyNightLightStart,
            ClearSkyNightLightEnd,
        ),
        scrollColor = ClearSkyNightLightEnd,
    )

    override val FOG_HAZE = Background(
        gradient = listOf(
            FogHazeLightStart,
            FogHazeLightEnd,
        ),
        scrollColor = FogHazeLightEnd,
    )

    override val MOSTLY_CLEAR_PARTLY_CLOUDY = Background(
        gradient = listOf(
            MostlyClearPartlyCloudyLightStart,
            MostlyClearPartlyCloudyLightEnd,
        ),
        scrollColor = MostlyClearPartlyCloudyLightEnd,
    )

    override val OVERCAST = Background(
        gradient = listOf(
            OvercastLightStart,
            OvercastLightEnd,
        ),
        scrollColor = OvercastLightEnd,
    )

    override val RAIN = Background(
        gradient = listOf(
            RainLightStart,
            RainLightEnd,
        ),
        scrollColor = RainLightEnd,
    )

    override val SNOW = Background(
        gradient = listOf(
            SnowLightStart,
            SnowLightEnd,
        ),
        scrollColor = SnowLightEnd,
    )

    override val THUNDERSTORM = Background(
        gradient = listOf(
            ThunderstormLightStart,
            ThunderstormLightEnd,
        ),
        scrollColor = ThunderstormLightEnd,
    )
}
