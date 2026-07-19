package com.pranshulgg.weather_master_app.feature.blocks.screens.humidity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.pranshulgg.weather_master_app.feature.blocks.components.NoHourlyDataAvailable

@Composable
fun HumidityScreen(
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
    val dewPointData = data.map { it.dewPoint }
    val humidityData = data.map { it.humidity }

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
        title = stringResource(R.string.weather_humidity),
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            val isLandscape = maxWidth > maxHeight

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
                if (!humidityData.contains(null)) {
                    HumidityHourlyCard(
                        data = data,
                        zoneId = zoneId,
                    )
                } else {
                    NoHourlyDataAvailable()
                }

                if (isLandscape) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                        ),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 14.dp,
                        ),
                    ) {
                        AboutCard(
                            modifier = Modifier.weight(
                                weight = 1.00f,
                            ),
                        ) {
                            AboutCardText(
                                text = stringResource(R.string.weather_about_humidity),
                            )
                        }

                        AboutCard(
                            modifier = Modifier.weight(
                                weight = 1.00f,
                            ),
                        ) {
                            AboutCardText(
                                text = stringResource(R.string.weather_about_dewpoint),
                            )
                        }
                    }
                } else {
                    AboutCard {
                        AboutCardText(
                            text = stringResource(R.string.weather_about_humidity),
                        )
                    }
                }

                if (dewPointData.contains(null).not()) {
                    DewPointHourlyCard(
                        context = context,
                        data = data,
                        unit = units.temperature,
                        zoneId = zoneId,
                    )
                } else {
                    NoHourlyDataAvailable()
                }

                if (!isLandscape) {
                    AboutCard {
                        AboutCardText(
                            text = stringResource(R.string.weather_about_dewpoint),
                        )
                    }
                }
            }
        }
    }
}
