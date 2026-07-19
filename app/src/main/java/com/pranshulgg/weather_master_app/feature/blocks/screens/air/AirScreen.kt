package com.pranshulgg.weather_master_app.feature.blocks.screens.air

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
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityHourly
import com.pranshulgg.weather_master_app.core.model.weather.air.AirQualityIndexCategory
import com.pranshulgg.weather_master_app.core.model.weather.air.AirQualityIndexStandard
import com.pranshulgg.weather_master_app.core.ui.barColorsAirQuality
import com.pranshulgg.weather_master_app.core.ui.components.NavigateBackButton
import com.pranshulgg.weather_master_app.core.ui.components.TopBarScaffold
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutScaleLayout
import com.pranshulgg.weather_master_app.feature.blocks.components.NoHourlyDataAvailable
import com.pranshulgg.weather_master_app.feature.blocks.components.ScaleCardItem
import com.pranshulgg.weather_master_app.feature.blocks.components.ScaleDialog

@Composable
fun AirScreen(
    index: Int = 0,
    locationId: String,
    navController: NavController,
) {
    val context = LocalContext.current
    val viewModel: BlocksScreenViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getAirQuality(locationId)
        viewModel.getUnitsOnce()
        viewModel.getWeather(locationId)
    }

    val uiState = viewModel.uiState.value

    val airQuality = uiState.air ?: return
    val weather = uiState.weather ?: return

    val time = if (index != 0) weather.daily[index].time else weather.current.time

    val data = findMatchingHourlyAir(
        currentMilli = time,
        data = airQuality.hourly,
        limitHours = weather.location.source.hourlyAggregationLimitHours,
    )
    val date = toDateString(
        timeMilli = weather.daily[index].time,
        zoneId = weather.location.timezone,
    )
    val standard = AirQualityIndexStandard.forCountryCode(weather.location.countryCode)
    val zoneId = weather.location.timezone

    val hasData = data.any { airQuality.getAqi(it, standard) > 0 }

    var selectedCategory by remember { mutableStateOf<AirQualityIndexCategory?>(null) }

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
        title = stringResource(R.string.weather_air),
    ) { paddingValues ->
        val scaleItems: @Composable () -> Unit = {
            airQuality.getAqiScale(standard).forEach { category ->
                ScaleCardItem(
                    containerColor = barColorsAirQuality(
                        category = category,
                    ),
                    headline = category.toName(
                        context = context,
                    ),
                    icon = R.drawable.ic_airwave_24,
                    onClick = {
                        selectedCategory = category
                    },
                    trailing = category.label,
                )
            }
        }

        AboutScaleLayout(
            aboutText = stringResource(R.string.weather_about_air),
            paddingValues = paddingValues,
            scaleItems = scaleItems,
        ) {
            if (hasData) {
                AirHourlyCard(
                    airQuality = airQuality,
                    data = data,
                    standard = standard,
                    zoneId = zoneId,
                )
            } else {
                NoHourlyDataAvailable()
            }
        }
    }

    selectedCategory?.let { category ->
        ScaleDialog(
            description = stringResource(category.description),
            onDismiss = {
                selectedCategory = null
            },
            range = category.label,
            title = category.toName(
                context = context,
            ),
        )
    }
}

private fun findMatchingHourlyAir(
    currentMilli: Long,
    data: List<AirQualityHourly>,
    limitHours: Int,
): List<AirQualityHourly> {
    val startIndex = data.indexOfFirst { it.time >= currentMilli }
    if (startIndex == -1) return emptyList()
    return data.drop(maxOf(0, startIndex)).take(limitHours)
}
