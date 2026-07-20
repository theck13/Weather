package com.heckofanapp.weather.feature.blocks.screens.precipitation

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.ui.components.NavigateBackButton
import com.heckofanapp.weather.core.ui.components.TopBarScaffold
import com.heckofanapp.weather.core.utils.formatters.toDateString
import com.heckofanapp.weather.core.utils.weather.forecast.findMatchingHourly
import com.heckofanapp.weather.feature.blocks.BlocksScreenViewModel
import com.heckofanapp.weather.feature.blocks.components.AboutScaleLayout
import com.heckofanapp.weather.feature.blocks.components.ScaleCardItem
import com.heckofanapp.weather.feature.blocks.components.ScaleDialog

@Composable
fun RainScreen(
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

    val isOnlyPrecipitation = weather.location.source.providesSnowFall().not()
    val scale = getRainScale(units.precipitation)

    var selectedLevel by remember { mutableStateOf<PrecipitationLevel?>(null) }

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
        title = stringResource(if (isOnlyPrecipitation) R.string.weather_precipitation else R.string.weather_rain_block)
    ) { paddingValues ->
        val scaleItems: @Composable () -> Unit = {
            scale.forEach {
                ScaleCardItem(
                    containerColor = it.color,
                    headline = it.headline,
                    icon = R.drawable.ic_water_drop_24,
                    onClick = {
                        selectedLevel = it
                    },
                    trailing = it.scale,
                )
            }
        }

        AboutScaleLayout(
            aboutText = stringResource(R.string.weather_about_precipitation),
            paddingValues = paddingValues,
            scaleItems = scaleItems,
        ) {
            RainHourlyCard(
                context = context,
                data = data,
                unit = units.precipitation,
                zoneId = zoneId,
            )
        }
    }

    selectedLevel?.let { info ->
        ScaleDialog(
            description = info.description,
            onDismiss = {
                selectedLevel = null
            },
            range = info.scale,
            title = info.headline,
        )
    }
}
