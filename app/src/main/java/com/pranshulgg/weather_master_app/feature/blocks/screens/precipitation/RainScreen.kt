package com.pranshulgg.weather_master_app.feature.blocks.screens.precipitation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.NavigateBackButton
import com.pranshulgg.weather_master_app.core.ui.components.TopBarScaffold
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCard
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCardText

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

    val isOnlyPrecipitation = !weather.location.source.providesSnowFall()

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = rememberScrollState(),
                )
                .padding(
                    paddingValues = paddingValues,
                )
                .padding(
                    bottom = 16.dp,
                    top = 2.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(
                space = 14.dp,
            ),
        ) {
            RainHourlyCard(
                context = context,
                data = data,
                unit = units.precipitation,
                zoneId = zoneId,
            )

            AboutCard {
                AboutCardText(
                    text = stringResource(R.string.weather_about_precipitation),
                )
            }
        }
    }
}
