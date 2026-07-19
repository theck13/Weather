package com.pranshulgg.weather_master_app.feature.blocks.screens.ultraviolet

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
import com.pranshulgg.weather_master_app.core.model.weather.ultraviolet.UltravioletIndex
import com.pranshulgg.weather_master_app.core.model.weather.ultraviolet.toColor
import com.pranshulgg.weather_master_app.core.model.weather.ultraviolet.toDescription
import com.pranshulgg.weather_master_app.core.model.weather.ultraviolet.toLabel
import com.pranshulgg.weather_master_app.core.ui.components.NavigateBackButton
import com.pranshulgg.weather_master_app.core.ui.components.TopBarScaffold
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutScaleLayout
import com.pranshulgg.weather_master_app.feature.blocks.components.NoHourlyDataAvailable
import com.pranshulgg.weather_master_app.feature.blocks.components.ScaleCardItem
import com.pranshulgg.weather_master_app.feature.blocks.components.ScaleDialog

@Composable
fun UltravioletScreen(
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
    val weather = uiState.weather

    val hourly = weather?.hourly ?: return
    val time = if (index != 0) weather.daily[index].time else weather.current.time

    val data = findMatchingHourly(
        currentMilli = time,
        data = hourly,
        source = weather.location.source,
    )
    val ultravioletIndices = UltravioletIndex.entries
    val date = toDateString(
        timeMilli = weather.daily[index].time,
        zoneId = weather.location.timezone,
    )
    val uvIndexData = data.map { it.ultraviolet }
    val zoneId = weather.location.timezone

    var selectedUltravioletIndex by remember { mutableStateOf<UltravioletIndex?>(null) }

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
        title = stringResource(R.string.weather_ultraviolet),
    ) { paddingValues ->
        val scaleItems: @Composable () -> Unit = {
            ultravioletIndices.forEach {
                ScaleCardItem(
                    containerColor = it.toColor(),
                    headline = it.toLabel(context),
                    icon = R.drawable.ic_sunny_white_balance_24,
                    onClick = {
                        selectedUltravioletIndex = it
                    },
                    trailing = getUltravioletScaleFor(it),
                )
            }
        }

        AboutScaleLayout(
            aboutText = stringResource(R.string.weather_about_ultraviolet),
            paddingValues = paddingValues,
            scaleItems = scaleItems,
        ) {
            if (uvIndexData.contains(null).not()) {
                UltravioletHourlyCard(data, zoneId)
            } else {
                NoHourlyDataAvailable()
            }
        }
    }

    selectedUltravioletIndex?.let { uvIndex ->
        ScaleDialog(
            description = uvIndex.toDescription(
                context = context,
            ),
            onDismiss = {
                selectedUltravioletIndex = null
            },
            range = getUltravioletScaleFor(uvIndex),
            title = uvIndex.toLabel(
                context = context,
            ),
        )
    }
}

private fun getUltravioletScaleFor(
    index: UltravioletIndex,
): String {
    return when (index) {
        UltravioletIndex.LOW -> "1 - 2"
        UltravioletIndex.MODERATE -> "3 - 5"
        UltravioletIndex.HIGH -> "6 - 7"
        UltravioletIndex.VERY_HIGH -> "8 - 10"
        UltravioletIndex.EXTREME -> "11+"
    }
}
