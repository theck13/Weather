package com.heckofanapp.weather.feature.shared.components.blocks

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.core.model.weather.PrecipitationUnit
import com.heckofanapp.weather.core.model.weather.toName
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.theme.ShadowElevation
import com.heckofanapp.weather.core.ui.theme.onSurfaceDim
import com.heckofanapp.weather.core.utils.formatters.formatLocalizedNumber
import com.heckofanapp.weather.core.utils.locale.getCurrentAppLocale
import com.heckofanapp.weather.feature.shared.components.Header

@Composable
fun RainBlock(
    context: Context,
    isPrecipitation: Boolean = false,
    onClickBlock: () -> Unit,
    rainForTheDay: Double,
    units: WeatherUnits,
) {
    val color = MaterialTheme.colorScheme.tertiaryContainer
    // Fill represents same physical rainfall in any unit: convert to inches, then fill continuously
    // up to 4 in / 10 cm.  So, water level is independent of user's selected precipitation unit.
    val rainInInches = units.precipitation.convert(
        from = rainForTheDay,
        to = PrecipitationUnit.IN,
    ) ?: 0.0
    val fillTotal = (rainInInches / 4.00).coerceIn(0.00, 1.00).toFloat()  // Full at 4 in / 10 cm.

    Surface(
        color = MaterialTheme.colorScheme.surface,
        onClick = onClickBlock,
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
            if (rainInInches > 0) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = 0.80f
                            compositingStrategy = CompositingStrategy.Offscreen
                        },
                ) {
                    // Reserve the wave's own height so its crest — not the waterline
                    // — reaches the top at fillLevel = 1.00f (4 in).  The image is
                    // 360x36 (a 10:1 ratio), so filling the square tile's width
                    // is at 1/10 of the tile height.  At fillLevel = 0.00f the
                    // wave rests on the bottom with no fill beneath it.
                    val waveHeight = maxHeight / 10.00f
                    val fillHeight = (maxHeight - waveHeight) * fillTotal

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
                        contentDescription = "Rain Wave",
                        contentScale = ContentScale.FillWidth,
                        painter = painterResource(R.drawable.il_wave_5),
                    )
                }
            }

            Column(
                Modifier
                    .align(
                        alignment = Alignment.BottomEnd,
                    )
                    .padding(
                        bottom = 16.dp,
                    ),
            ) {
                Header(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceDim,
                    icon = R.drawable.ic_water_drop_24,
                    padding = PaddingValues(
                        end = 12.dp,
                        start = 12.dp,
                        top = 16.dp,
                    ),
                    text = stringResource(if (isPrecipitation) R.string.weather_precipitation else R.string.weather_rain_block),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(
                            weight = 1.00f,
                        )
                        .wrapContentHeight(
                            align = Alignment.CenterVertically,
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp,
                        style = MaterialTheme.typography.displayMedium,
                        text = formatLocalizedNumber(
                            decimalPlaces = 2,
                            locale = getCurrentAppLocale(),
                            number = rainForTheDay,
                        ),
                        textAlign = TextAlign.Center,
                    )

                    Gap(
                        horizontal = 2.dp,
                    )

                    Text(
                        modifier = Modifier.alignByBaseline(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleLarge,
                        text = units.precipitation.toName(
                            context = context,
                            inShort = true,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                        ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    text = stringResource(if (isPrecipitation) R.string.weather_total_amount else R.string.weather_total_rain_day),
                )
            }
        }
    }
}
