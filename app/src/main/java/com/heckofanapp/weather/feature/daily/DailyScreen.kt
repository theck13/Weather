package com.heckofanapp.weather.feature.daily

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherBlock
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.core.prefs.LocalAppPrefs
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.NavigateBackButton
import com.heckofanapp.weather.core.ui.components.TopBarScaffold
import com.heckofanapp.weather.feature.shared.components.blocks.WeatherBlocks
import com.heckofanapp.weather.feature.shared.ui.HourlyCard
import com.heckofanapp.weather.feature.shared.ui.SummaryCard

data class DailyScreenUiState(
    val blocks: List<WeatherBlock> = WeatherBlock.getDefaultForDaily(),
    val units: WeatherUnits = WeatherUnits.getDefault(),
    val weather: Weather? = null,
)

@Composable
fun DailyScreen(
    index: Int = 0,
    locationId: String,
    navController: NavController,
) {
    val viewModel: DailyScreenViewModel = hiltViewModel()

    val uiState = viewModel.uiState.value
    val weather = remember(uiState.weather) { uiState.weather }
    val units = uiState.units
    val context = LocalContext.current
    val preferences = LocalAppPrefs.current
    val isShowSummary = preferences.isShowSummary

    var selectedIndex by rememberSaveable { mutableIntStateOf(index) }

    LaunchedEffect(Unit) {
        viewModel.loadBlocks()
        viewModel.getUnitsOnce()
        viewModel.getDailyWeather(locationId)
    }

    if (weather == null) return

    var selectedDaily by remember { mutableStateOf(weather.daily[index]) }

    LaunchedEffect(selectedIndex) {
        selectedDaily = weather.daily[selectedIndex]
    }

    TopBarScaffold(
        navigationIcon = {
            NavigateBackButton(navController)
        },
        title = stringResource(R.string.weather_daily_forecast),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = rememberScrollState(),
                )
                .padding(
                    paddingValues = paddingValues,
                ),
        ) {
            DailyDaysHeader(
                onSelect = {
                    selectedIndex = it
                },
                selectedIndex = selectedIndex,
                units = units,
                weather = weather,
            )

            DailyForecastHeroHeader(
                daily = selectedDaily,
                location = weather.location,
                units = units,
            )

            Gap(
                vertical = 8.dp,
            )

            Column(
                modifier = Modifier.padding(
                    bottom = 8.dp,
                    end = 16.dp,
                    start = 16.dp,
                    top = 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                ),
            ) {
                if (isShowSummary) {
                    SummaryCard(
                        context = context,
                        dailyIndex = selectedIndex,
                        units = units,
                        weather = weather,
                    )

                    Gap (
                        vertical = 16.dp,
                    )
                }

                HourlyCard(
                    currentMilli = if (selectedIndex != 0) selectedDaily.time else weather.current.time,
                    units = units,
                    weather = weather,
                )

                WeatherBlocks(
                    airQuality = null,
                    blocks = uiState.blocks,
                    dailyIndex = selectedIndex,
                    context = context,
                    isDaily = true,
                    navController = navController,
                    units = units,
                    updatedBlockOrder = {
                        viewModel.updateBlocksOrder(it)
                    },
                    weather = weather,
                )
            }
        }
    }
}
