package com.pranshulgg.weather_master_app.feature.shared.components.blocks

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.ui.barColorsPressure
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.core.ui.theme.onSurfaceDim
import com.pranshulgg.weather_master_app.core.utils.formatters.formatLocalizedNumber
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import com.pranshulgg.weather_master_app.feature.shared.components.Header
import kotlin.math.roundToInt

@Composable
fun PressureBlock(
    weather: Weather,
    units: WeatherUnits,
    context: Context,
    isDaily: Boolean,
    dailyIndex: Int,
    onClickBlock: () -> Unit,
) {
    val pressure = if (isDaily) weather.daily[dailyIndex].pressureMsl else weather.current.pressureMsl
    val pressureConverted = PressureUnit.HPA.convert(
        from = pressure!!,
        to = units.pressure,
    )
    val pressureHpa = weather.current.pressureMsl!!.roundToInt()

    val progressDrawable = when {
        pressureHpa < 980 -> R.drawable.il_pressure_progress_low
        pressureHpa in 980..1005 -> R.drawable.il_pressure_progress_low_medium
        pressureHpa in 1005..1020 -> R.drawable.il_pressure_progress_medium
        pressureHpa in 1020..1035 -> R.drawable.il_pressure_progress_high
        else -> R.drawable.il_pressure_progress_high_very
    }

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
                .fillMaxSize()
                .aspectRatio(
                    ratio = 1.00f,
                )
        ) {
            Image(
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
                contentDescription = "",
                painter = painterResource(R.drawable.il_pressure_progress_container),
            )

            Image(
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.tint(
                    color = barColorsPressure(
                        pressure = pressureHpa.toDouble(),
                    ),
                ),
                contentDescription = "",
                painter = painterResource(progressDrawable),
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
                    text = stringResource(R.string.weather_pressure),
                    icon = R.drawable.ic_compress_24,
                    padding = PaddingValues(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceDim,
                )

                Text(
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.displayMedium,
                    text = formatLocalizedNumber(
                        decimalPlaces = 2,
                        locale = getCurrentAppLocale(),
                        number = pressureConverted ?: 0.00,
                    ),
                )

                Text(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    text = units.pressure.toName(
                        context = context,
                        inShort = true,
                    ),
                )

                Gap(
                    vertical = 28.dp,
                )
            }
        }
    }
}
