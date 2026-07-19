package com.heckofanapp.weather.feature.shared.components.blocks

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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.core.model.weather.DistanceUnit
import com.heckofanapp.weather.core.model.weather.toName
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.theme.ShadowElevation
import com.heckofanapp.weather.core.ui.theme.ShapeRadius
import com.heckofanapp.weather.core.ui.theme.onSurfaceDim
import com.heckofanapp.weather.core.utils.formatters.formatLocalizedNumber
import com.heckofanapp.weather.core.utils.locale.getCurrentAppLocale
import com.heckofanapp.weather.feature.shared.components.Header
import kotlin.math.roundToInt

@Composable
fun VisibilityBlock(
    weather: Weather,
    units: WeatherUnits,
    context: Context,
    isDaily: Boolean,
    dailyIndex: Int,
    onClickBlock: () -> Unit
) {
    val formatter: (Double) -> Double? = {
        DistanceUnit.M.convert(
            from = it,
            to = units.distance,
        )
    }
    val visibility = if (isDaily) weather.daily[dailyIndex].visibility?.toDouble() else weather.current.visibility?.toDouble()
    val visibilityAlpha = getAlphaFromVisibility(
        valueKm =
            DistanceUnit.M.convert(
                from = visibility,
                to = DistanceUnit.KM,
            )?.roundToInt()!!,
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(ShapeRadius.Full),
        shadowElevation = ShadowElevation.level2,
        onClick = onClickBlock
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
                    .alpha(
                        alpha = visibilityAlpha,
                    )
                    .matchParentSize(),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                ),
                contentDescription = "",
                painter = painterResource(R.drawable.il_visibility),
            )

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
                    icon = R.drawable.ic_visibility_24,
                    padding = PaddingValues(
                        horizontal = 16.dp,
                    ),
                    text = stringResource(R.string.weather_visibility),
                )

                Row {
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.displayMedium,
                        text = formatLocalizedNumber(
                            decimalPlaces = 0,
                            locale = getCurrentAppLocale(),
                            number = formatter(visibility!!)!!,
                        ),
                    )

                    Gap(
                        horizontal = 2.dp,
                    )

                    Text(
                        modifier = Modifier.alignByBaseline(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        text = units.distance.toName(
                            context = context,
                            inShort = true,
                        )
                    )
                }

                if (!isDaily) {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = stringResource(
                            getVisibilityText(
                                DistanceUnit.M.convert(
                                    from = visibility,
                                    to = DistanceUnit.KM,
                                )?.roundToInt()!!
                            )
                        ),
                    )
                } else {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = stringResource(R.string.weather_minimum),
                    )
                }

                Gap(
                    vertical = 28.dp,
                )
            }
        }
    }
}

private fun getAlphaFromVisibility(
    valueKm: Int,
): Float {
    return when {
        valueKm >= 20 -> 0.10f
        valueKm >= 10 -> 0.30f
        valueKm >= 4 -> 0.50f
        valueKm >= 1 -> 0.70f
        else -> 0.80f
    }
}

private fun getVisibilityText(
    valueKm: Int,
): Int {
    return when {
        valueKm >= 20 -> R.string.text_very_good
        valueKm >= 10 -> R.string.text_good
        valueKm >= 4 -> R.string.text_moderate
        valueKm >= 1 -> R.string.text_poor
        else -> R.string.text_very_poor
    }
}
