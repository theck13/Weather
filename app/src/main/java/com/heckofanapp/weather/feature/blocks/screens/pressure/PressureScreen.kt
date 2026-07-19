package com.heckofanapp.weather.feature.blocks.screens.pressure

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.weather.PressureUnit
import com.heckofanapp.weather.core.ui.components.NavigateBackButton
import com.heckofanapp.weather.core.ui.components.TopBarScaffold
import com.heckofanapp.weather.core.ui.theme.Blue800
import com.heckofanapp.weather.core.ui.theme.Green600
import com.heckofanapp.weather.core.ui.theme.Orange600
import com.heckofanapp.weather.core.ui.theme.Purple900
import com.heckofanapp.weather.core.ui.theme.Red800
import com.heckofanapp.weather.core.utils.formatters.toDateString
import com.heckofanapp.weather.core.utils.weather.forecast.findMatchingHourly
import com.heckofanapp.weather.feature.blocks.BlocksScreenViewModel
import com.heckofanapp.weather.feature.blocks.components.AboutScaleLayout
import com.heckofanapp.weather.feature.blocks.components.NoHourlyDataAvailable
import com.heckofanapp.weather.feature.blocks.components.ScaleCardItem
import com.heckofanapp.weather.feature.blocks.components.ScaleDialog

private data class PressureScale(
    val color: Color,
    val description: String,
    val headline: String,
    val scale: String,
)

private data class PressureScaleRange(
    val color: Color,
    val description: Int,
    val headline: Int,
    val scaleHpa: String,
    val scaleInHg: String,
    val scaleMmHg: String,
)

@Composable
fun PressureScreen(
    index: Int = 0,
    locationId: String,
    navController: NavController,
) {
    val context = LocalContext.current
    val viewModel: BlocksScreenViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getUnitsOnce()
        viewModel.getWeather(locationId)
    }

    val uiState = viewModel.uiState.value
    val units = uiState.units
    val weather = uiState.weather

    val hourly = weather?.hourly ?: return
    val scale = getPressureScaleFor(units.pressure)
    val time = if (index != 0) weather.daily[index].time else weather.current.time
    val zoneId = weather.location.timezone

    val data = findMatchingHourly(
        currentMilli = time,
        data = hourly,
        source = weather.location.source,
    )
    val date = toDateString(
        timeMilli = weather.daily[index].time,
        zoneId = weather.location.timezone,
    )
    val pressureData = data.map { it.pressureMsl }

    var selectedPressure by remember { mutableStateOf<PressureScale?>(null) }

    TopBarScaffold(
        actions = {
            Text(
                modifier = Modifier.padding(
                    end = 16.dp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                text = date,
            )
        },
        navigationIcon = {
            NavigateBackButton(
                navController = navController,
            )
        },
        title = stringResource(R.string.weather_pressure),
    ) { paddingValues ->
        val scaleItems: @Composable () -> Unit = {
            scale.forEach {
                ScaleCardItem(
                    containerColor = it.color,
                    headline = it.headline,
                    icon = R.drawable.ic_compress_24,
                    onClick = {
                        selectedPressure = it
                    },
                    trailing = it.scale,
                )
            }
        }

        AboutScaleLayout(
            aboutText = stringResource(R.string.weather_about_pressure),
            paddingValues = paddingValues,
            scaleItems = scaleItems,
        ) {
            if (pressureData.contains(null).not()) {
                PressureHourlyCard(
                    context = context,
                    data = data,
                    unit = units.pressure,
                    zoneId = zoneId,
                )
            } else {
                NoHourlyDataAvailable()
            }
        }
    }

    selectedPressure?.let { info ->
        ScaleDialog(
            description = info.description,
            onDismiss = {
                selectedPressure = null
            },
            range = info.scale,
            title = info.headline,
        )
    }
}

private val pressureRanges = listOf(
    PressureScaleRange(
        color = Red800,
        description = R.string.pressure_description_1,
        headline = R.string.pressure_scale_1,
        scaleHpa = "> 1025 hPa",
        scaleInHg = "> 30.27 inHg",
        scaleMmHg = "> 769 mmHg",
    ),
    PressureScaleRange(
        color = Orange600,
        description = R.string.pressure_description_2,
        headline = R.string.pressure_scale_2,
        scaleHpa = "1010 - 1025 hPa",
        scaleInHg = "29.83 - 30.27 inHg",
        scaleMmHg = "758 - 769 mmHg",
    ),
    PressureScaleRange(
        color = Green600,
        description = R.string.pressure_description_3,
        headline = R.string.pressure_scale_3,
        scaleHpa = "995 - 1010 hPa",
        scaleInHg = "29.38 - 29.83 inHg",
        scaleMmHg = "746 - 758 mmHg",
    ),
    PressureScaleRange(
        color = Blue800,
        description = R.string.pressure_description_4,
        headline = R.string.pressure_scale_4,
        scaleHpa = "980 - 995 hPa",
        scaleInHg = "28.94 - 29.38 inHg",
        scaleMmHg = "735 - 746 mmHg",
    ),
    PressureScaleRange(
        color = Purple900,
        description = R.string.pressure_description_5,
        headline = R.string.pressure_scale_5,
        scaleHpa = "< 980 hPa",
        scaleInHg = "< 28.94 inHg",
        scaleMmHg = "< 735 mmHg",
    ),
)

@Composable
private fun getPressureScaleFor(
    unit: PressureUnit,
): List<PressureScale> {
    return pressureRanges.map { range ->
        PressureScale(
            color = range.color,
            description = stringResource(range.description),
            headline = stringResource(range.headline),
            scale = when (unit) {
                PressureUnit.HPA -> range.scaleHpa
                PressureUnit.INHG -> range.scaleInHg
                PressureUnit.MMHG -> range.scaleMmHg
            },
        )
    }
}
