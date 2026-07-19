package com.pranshulgg.weather_master_app.feature.shared.components.blocks

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.WindUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.core.ui.theme.onSurfaceDim
import com.pranshulgg.weather_master_app.feature.shared.components.Header
import kotlin.math.roundToInt

@Composable
fun WindBlock(
    weather: Weather,
    context: Context,
    isDaily: Boolean,
    dailyIndex: Int,
    units: WeatherUnits,
    onClickBlock: () -> Unit,
) {
    val windDirection =
        if (isDaily) {
            weather.daily[dailyIndex].windDirection
        } else {
            weather.current.windDirection
        }
    val windSpeed =
        if (isDaily) {
            weather.daily[dailyIndex].windSpeed
        } else {
            weather.current.windSpeed
        }
    val windSpeedFormatted = WindUnit.KPH.convert(
        from = windSpeed,
        to = units.speed,
    )?.roundToInt()

    val windDirectionAlpha = getAlphaFromWindSpeed(windSpeed?.roundToInt() ?: 0)

    Surface(
        color = MaterialTheme.colorScheme.surface,
        onClick = onClickBlock,
        shape = RoundedCornerShape(
            size = ShapeRadius.Full,
        ),
        shadowElevation = ShadowElevation.level2,
    ) {
        Box(
            Modifier
                .aspectRatio(
                    ratio = 1.00f,
                )
                .fillMaxSize(),
        ) {
            if (windDirection != null) {
                Image(
                    modifier = Modifier
                        .alpha(
                            alpha = windDirectionAlpha,
                        )
                        .matchParentSize()
                        .rotate(
                            degrees = WindDirection.toDegrees(windDirection)?.toFloat() ?: 0f,
                        ),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                    ),
                    painter = painterResource(R.drawable.il_wind_arrow),
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Gap(
                    vertical = 28.dp,
                )

                Header(
                    color = MaterialTheme.colorScheme.onSurfaceDim,
                    icon = R.drawable.ic_air_24,
                    padding = PaddingValues(
                        end = 16.dp,
                        start = 16.dp,
                    ),
                    text = stringResource(R.string.weather_wind),
                )

                Row {
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.displayMedium,
                        text = windSpeedFormatted.toString(),
                    )

                    Gap(
                        horizontal = 2.dp,
                    )

                    Text(
                        modifier = Modifier.alignByBaseline(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = units.speed.toName(
                            context = context,
                            inShort = true,
                        ),
                    )
                }

                if (windDirection != null) {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = stringResource(R.string.weather_wind_direction_from, windDirection),
                    )
                }

                Gap(
                    vertical = 28.dp,
                )
            }
        }
    }
}

private fun getAlphaFromWindSpeed(
    valueKph: Int,
): Float {
    return when {
        valueKph < 10 -> 0.10f
        valueKph < 30 -> 0.30f
        valueKph < 50 -> 0.50f
        valueKph < 70 -> 0.70f
        else -> 0.80f
    }
}
