package com.pranshulgg.weather_master_app.feature.shared.components.blocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.ui.theme.onSurfaceDim
import com.pranshulgg.weather_master_app.feature.shared.components.Header
import kotlin.math.roundToInt

@Composable
fun HumidityBlock(
    weather: Weather,
    units: WeatherUnits,
    onClickBlock: () -> Unit,
    isDaily: Boolean,
    dailyIndex: Int,
) {
    val color = MaterialTheme.colorScheme.tertiaryContainer
    val dewPoint = TemperatureUnit.CELSIUS.convert(
        from = if (isDaily) weather.daily[dailyIndex].dewPoint else weather.current.dewPoint,
        to = units.temperature,
    )?.roundToInt() ?: "-"
    val humidity =
        if (isDaily) {
            weather.daily[dailyIndex].humidity?.roundToInt()
        } else {
            weather.current.humidity.roundToInt()
        }
    // Humidity is already a percentage, so no unit conversion is needed.
    // Fill rises continuously with humidity, completely full at 100%.
    val fillLevel = ((humidity ?: 0) / 100.00f).coerceIn(0.00f, 1.00f)

    Surface(
        color = MaterialTheme.colorScheme.surface,
        onClick = onClickBlock,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2,
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(
                    ratio = 1.00f,
                )
                .fillMaxSize(),
        ) {
            if ((humidity ?: 0) > 0) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = 0.80f
                            compositingStrategy = CompositingStrategy.Offscreen
                        },
                ) {
                    // Reserve the wave's own height so its crest — not the waterline
                    // — reaches the top at fillLevel = 1.00f (100%).  The image is
                    // 360x36 (a 10:1 ratio), so filling the square tile's width
                    // is at 1/10 of the tile height.  At fillLevel = 0.00f the
                    // wave rests on the bottom with no fill beneath it.
                    val waveHeight = maxHeight / 10.00f
                    val fillHeight = (maxHeight - waveHeight) * fillLevel

                    Box(
                        modifier = Modifier
                            .align(
                                alignment = Alignment.BottomCenter,
                            )
                            .background(
                                color = color,
                            )
                            .fillMaxWidth()
                            .height(
                                height = fillHeight + 1.dp,
                            ),
                    )

                    Image(
                        modifier = Modifier
                            .align(
                                alignment = Alignment.BottomCenter,
                            )
                            .fillMaxWidth()
                            .offset(
                                y = -fillHeight,
                            ),
                        alignment = Alignment.BottomCenter,
                        colorFilter = ColorFilter.tint(color),
                        contentDescription = "Humidity",
                        contentScale = ContentScale.FillWidth,
                        painter = painterResource(R.drawable.il_wave_5),
                    )
                }
            }

            Box(
                modifier = Modifier.align(
                    alignment = Alignment.TopCenter,
                ),
            ) {
                Header(
                    color = MaterialTheme.colorScheme.onSurfaceDim,
                    icon = R.drawable.ic_humidity_percentage_24,
                    padding = PaddingValues(
                        end = 12.dp,
                        start = 12.dp,
                        top = 16.dp,
                    ),
                    text = stringResource(R.string.weather_humidity),
                )
            }

            Text(
                modifier = Modifier
                    .align(
                        alignment = Alignment.Center,
                    )
                    .padding(
                        horizontal = 12.dp,
                    ),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.displayMedium,
                text = "${humidity}%",
            )

            Box(
                modifier = Modifier
                    .align(
                        alignment = Alignment.BottomCenter,
                    )
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                DewPointRow(
                    dewPoint = dewPoint.toString(),
                )
            }
        }
    }
}

@Composable
private fun DewPointRow(
    dewPoint: String?,
) {
    val size = 36.dp

    Row(
        Modifier.padding(
            bottom = 12.dp,
            end = 12.dp,
            start = 12.dp,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = CircleShape,
        ) {
            Box(
                modifier = Modifier.size(
                    size = size,
                ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    text = "${dewPoint ?: "-"}°",
                )
            }
        }

        Gap(
            horizontal = 8.dp,
        )

        Text(
            modifier = Modifier
                .height(
                    height = size,
                )
                .wrapContentHeight(
                    align = Alignment.CenterVertically,
                ),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(R.string.weather_dew_point),
        )
    }
}
