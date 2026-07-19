package com.heckofanapp.weather.feature.shared.components.blocks

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.prefs.AppPrefsState
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.Symbol
import com.heckofanapp.weather.core.ui.theme.ShadowElevation
import com.heckofanapp.weather.core.utils.formatters.to12HourTimeString
import com.heckofanapp.weather.core.utils.formatters.to24HourTimeString
import com.heckofanapp.weather.core.utils.weather.cache.isWeatherDailyDomainSafe

@Composable
internal fun CelestialBlock(
    index: Int,
    onClick: (() -> Unit)?,
    state: AppPrefsState,
    type: CelestialType,
    weather: Weather,
) {
    if (isWeatherDailyDomainSafe(weather).not()) return

    val daily = weather.daily[index]
    val drawable = when (type) {
        CelestialType.Moon -> daily.moonPhase.icon
        CelestialType.Sun -> R.drawable.ic_rise_set_sun
    }
    val image = painterResource(drawable)
    val is24Hr = state.timeFormat == "24"
    val formatter: (Long) -> String = {
        if (is24Hr) to24HourTimeString(
            timeMilli = it,
            zoneId = weather.location.timezone,
        ) else to12HourTimeString(
            pattern = "hh:mm a",
            timeMilli = it,
            zoneId = weather.location.timezone,
        )
    }
    val timeNow = System.currentTimeMillis()
    val timeRise =
        when (type) {
            CelestialType.Moon -> daily.moonrise
            CelestialType.Sun -> daily.sunrise
        }
    val timeSet =
        when (type) {
            CelestialType.Moon -> daily.moonset
            CelestialType.Sun -> daily.sunset
        }

    val progress = ((timeNow - timeRise).toFloat() / (timeSet - timeRise)).coerceIn(
        minimumValue = 0.00f,
        maximumValue = 1.00f,
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        enabled = onClick != null,
        onClick = onClick ?: {},
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2,
    ) {
        Box(
            Modifier
                .aspectRatio(
                    ratio = 1.00f,
                )
                .fillMaxSize(),
        ) {
            Image(
                modifier = Modifier
                    .align(
                        alignment = Alignment.BottomCenter,
                    )
                    .matchParentSize(),
                alignment = Alignment.BottomCenter,
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                ),
                contentDescription = "",
                painter = painterResource(R.drawable.il_rise_set_arc),
            )

            Canvas(
                modifier = Modifier
                    .alpha(
                        alpha = if (index == 0) 1.00f else 0.00f,
                    )
                    .fillMaxSize(),
            ) {
                val height = size.height
                val width = size.width

                val path = Path().apply {
                    moveTo(
                        x = 0.00f,
                        y = height / 1.45f,
                    )
                    cubicTo(
                        width * 0.38f, height / 2.00f,
                        width * 0.38f, height / 15.00f,
                        width * 0.98f, height / 1.40f,
                    )
                }

                drawPath(
                    color = Color.Transparent,
                    path = path,
                )

                val pathMeasure = PathMeasure().apply {
                    setPath(
                        forceClosed = false,
                        path = path,
                    )
                }
                val position = pathMeasure.getPosition(pathMeasure.length * progress)
                val iconSize = 64.00f

                translate(
                    left = position.x - iconSize / 2.00f,
                    top = position.y - iconSize / 2.00f,
                ) {
                    with(image) {
                        draw(
                            size = Size(
                                height = iconSize,
                                width = iconSize,
                            ),
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.align(
                    alignment = Alignment.TopStart,
                ),
            ) {
                CelestialHeader(
                    type = type,
                )
            }

            Surface(
                modifier = Modifier
                    .align(
                        alignment = Alignment.BottomCenter,
                    )
                    .fillMaxHeight(
                        fraction = 0.40f,
                    )
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainer.copy(
                    alpha = 0.80f,
                ),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier.align(
                            alignment = Alignment.Center,
                        ),
                    ) {
                        CelestialTime(
                            icon = R.drawable.ic_arrow_upward_24,
                            text = formatter(timeRise),
                        )

                        CelestialTime(
                            icon = R.drawable.ic_arrow_downward_24,
                            text = formatter(timeSet),
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun CelestialHeader(
    type: CelestialType,
) {
    val alpha = 0.90f
    val color =  MaterialTheme.colorScheme.onSurface.copy(
        alpha = alpha,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                end = 12.dp,
                start = 12.dp,
                top = 16.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(
            alignment = Alignment.CenterHorizontally,
            space = 5.dp,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Symbol(
            color = color,
            icon = type.icon,
        )

        Text(
            color = color,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = stringResource(type.title),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
internal fun CelestialTime(
    icon: Int,
    text: String,
) {
    Row {
        Symbol(
            color = MaterialTheme.colorScheme.onSurface,
            icon = icon,
            size = 18.dp,
        )

        Gap(
            horizontal = 5.dp,
        )

        Text(
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge,
            text = text,
        )
    }
}

internal enum class CelestialType(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
) {
    Moon(
        icon = R.drawable.ic_bedtime_24,
        title = R.string.weather_moon,
    ),
    Sun(
        icon = R.drawable.ic_sunny_24,
        title = R.string.weather_sun,
    ),
}
