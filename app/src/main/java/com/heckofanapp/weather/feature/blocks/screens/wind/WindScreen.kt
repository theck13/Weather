package com.heckofanapp.weather.feature.blocks.screens.wind

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
import com.heckofanapp.weather.core.model.weather.WindUnit
import com.heckofanapp.weather.core.model.weather.toName
import com.heckofanapp.weather.core.ui.barColorsWindSpeed
import com.heckofanapp.weather.core.ui.components.NavigateBackButton
import com.heckofanapp.weather.core.ui.components.TopBarScaffold
import com.heckofanapp.weather.core.utils.formatters.toDateString
import com.heckofanapp.weather.core.utils.weather.forecast.findMatchingHourly
import com.heckofanapp.weather.feature.blocks.BlocksScreenViewModel
import com.heckofanapp.weather.feature.blocks.components.AboutScaleLayout
import com.heckofanapp.weather.feature.blocks.components.ScaleCardItem
import com.heckofanapp.weather.feature.blocks.components.ScaleDialog
import kotlin.math.roundToInt

private data class WindScale(
    val color: Color,
    val description: String,
    val headline: String,
    val scale: String,
)

private data class WindScaleRange(
    val beaufortRange: String,
    val descriptionRes: Int,
    val headlineRes: Int,
    val lowerMps: Double?,
    val representativeKph: Double,
    val upperMps: Double?,
)

@Composable
fun WindScreen(
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
    val scale = getWindScaleFor(units.speed)
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

    var selectedWind by remember { mutableStateOf<WindScale?>(null) }

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
        title = stringResource(R.string.weather_wind),
    ) { paddingValues ->
        val scaleItems: @Composable () -> Unit = {
            scale.forEach {
                ScaleCardItem(
                    containerColor = it.color,
                    headline = it.headline,
                    icon = R.drawable.ic_air_24,
                    onClick = {
                        selectedWind = it
                    },
                    trailing = it.scale,
                )
            }
        }

        AboutScaleLayout(
            aboutText = stringResource(R.string.weather_about_windspeed),
            paddingValues = paddingValues,
            scaleItems = scaleItems,
        ) {
            WindHourlyCard(
                context = context,
                data = data,
                unit = units.speed,
                zoneId = zoneId,
            )
        }
    }

    selectedWind?.let { info ->
        ScaleDialog(
            description = info.description,
            onDismiss = {
                selectedWind = null
            },
            range = info.scale,
            title = info.headline,
        )
    }
}

private val windRanges = listOf(
    WindScaleRange(
        beaufortRange = "0 - 1",
        descriptionRes = R.string.wind_description_1,
        headlineRes = R.string.wind_scale_1,
        lowerMps = null,
        representativeKph = 3.0,
        upperMps = 1.6,
    ),
    WindScaleRange(
        beaufortRange = "2 - 3",
        descriptionRes = R.string.wind_description_2,
        headlineRes = R.string.wind_scale_2,
        lowerMps = 1.6,
        representativeKph = 12.0,
        upperMps = 5.5,
    ),
    WindScaleRange(
        beaufortRange = "4 - 5",
        descriptionRes = R.string.wind_description_3,
        headlineRes = R.string.wind_scale_3,
        lowerMps = 5.5,
        representativeKph = 29.0,
        upperMps = 10.8,
    ),
    WindScaleRange(
        beaufortRange = "6 - 7",
        descriptionRes = R.string.wind_description_4,
        headlineRes = R.string.wind_scale_4,
        lowerMps = 10.8,
        representativeKph = 50.0,
        upperMps = 17.2,
    ),
    WindScaleRange(
        beaufortRange = "8 - 9",
        descriptionRes = R.string.wind_description_5,
        headlineRes = R.string.wind_scale_5,
        lowerMps = 17.2,
        representativeKph = 75.0,
        upperMps = 24.5,
    ),
    WindScaleRange(
        beaufortRange = "10+",
        descriptionRes = R.string.wind_description_6,
        headlineRes = R.string.wind_scale_6,
        lowerMps = 24.5,
        representativeKph = 100.0,
        upperMps = null,
    ),
)

@Composable
private fun getWindScaleFor(
    unit: WindUnit,
): List<WindScale> {
    val context = LocalContext.current

    return windRanges.map { range ->
        WindScale(
            color = barColorsWindSpeed(
                windSpeed = range.representativeKph,
            ),
            description = stringResource(range.descriptionRes),
            headline = stringResource(range.headlineRes),
            scale =
                if (unit == WindUnit.BFT) {
                    range.beaufortRange
                } else {
                    val suffix = unit.toName(
                        context = context,
                        inShort = true,
                    )
                    val lower = range.lowerMps?.let { convertMps(it, unit) }
                    val upper = range.upperMps?.let { convertMps(it, unit) }
    
                    when {
                        lower == null -> "< $upper $suffix"
                        upper == null -> "≥ $lower $suffix"
                        else -> "$lower - $upper $suffix"
                    }
                },
        )
    }
}

private fun convertMps(
    mps: Double,
    unit: WindUnit,
): Int = WindUnit.MPS.convert(
    from = mps,
    to = unit,
)?.roundToInt() ?: 0
