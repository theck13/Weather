package com.pranshulgg.weather_master_app.feature.blocks.screens.visibility

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
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.ui.components.NavigateBackButton
import com.pranshulgg.weather_master_app.core.ui.components.TopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.theme.Blue800
import com.pranshulgg.weather_master_app.core.ui.theme.Green400
import com.pranshulgg.weather_master_app.core.ui.theme.Green600
import com.pranshulgg.weather_master_app.core.ui.theme.Orange600
import com.pranshulgg.weather_master_app.core.ui.theme.Purple800
import com.pranshulgg.weather_master_app.core.ui.theme.Red800
import com.pranshulgg.weather_master_app.core.ui.theme.Yellow600
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutScaleLayout
import com.pranshulgg.weather_master_app.feature.blocks.components.NoHourlyDataAvailable
import com.pranshulgg.weather_master_app.feature.blocks.components.ScaleCardItem
import com.pranshulgg.weather_master_app.feature.blocks.components.ScaleDialog

private data class VisibilityScale(
    val color: Color,
    val description: String,
    val headline: String,
    val scale: String,
)

private data class VisibilityScaleRange(
    val color: Color,
    val descriptionRes: Int,
    val headlineRes: Int,
    val kmScale: String,
    val mScale: String,
    val miScale: String,
)

@Composable
fun VisibilityScreen(
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
    val scale = getVisibilityScaleFor(units.distance)
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
    val visibility = data.map { it.visibility }

    var selectedVisibility by remember { mutableStateOf<VisibilityScale?>(null) }

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
        title = stringResource(R.string.weather_visibility),
    ) { paddingValues ->
        val scaleItems: @Composable () -> Unit = {
            scale.forEach {
                ScaleCardItem(
                    containerColor = it.color,
                    headline = it.headline,
                    icon = R.drawable.ic_visibility_24,
                    onClick = {
                        selectedVisibility = it
                    },
                    trailing = it.scale,
                )
            }
        }

        AboutScaleLayout(
            aboutText = stringResource(R.string.weather_visibility_about),
            paddingValues = paddingValues,
            scaleItems = scaleItems,
        ) {
            if (!visibility.contains(null)) {
                VisibilityHourlyCard(
                    context = context,
                    data = data,
                    unit = units.distance,
                    zoneId = zoneId,
                )
            } else {
                NoHourlyDataAvailable()
            }
        }
    }

    selectedVisibility?.let { info ->
        ScaleDialog(
            description = info.description,
            onDismiss = {
                selectedVisibility = null
            },
            range = info.scale,
            title = info.headline,
        )
    }
}

private val visibilityRanges = listOf(
    VisibilityScaleRange(
       color = Blue800,
       descriptionRes = R.string.visibility_description_excellent,
       headlineRes = R.string.text_excellent,
       kmScale = "> 50 km",
       mScale = "> 50000 m",
       miScale = "> 30 mi",
    ),
    VisibilityScaleRange(
        color = Green600,
        descriptionRes = R.string.visibility_description_very_good,
        headlineRes = R.string.text_very_good,
        kmScale = "20 - 50 km",
        mScale = "20000 - 50000 m",
        miScale = "12 - 30 mi",
    ),
    VisibilityScaleRange(
        color = Green400,
        descriptionRes = R.string.visibility_description_good,
        headlineRes = R.string.text_good,
        kmScale = "10 - 20 km",
        mScale = "10000 - 20000 m",
        miScale = "6 - 12 mi",
    ),
    VisibilityScaleRange(
        color = Yellow600,
        descriptionRes = R.string.visibility_description_moderate,
        headlineRes = R.string.text_moderate,
        kmScale = "4 - 10 km",
        mScale = "4000 - 10000 m",
        miScale = "2.5 - 6 mi",
    ),
    VisibilityScaleRange(
        color = Orange600,
        descriptionRes = R.string.visibility_description_poor,
        headlineRes = R.string.text_poor,
        kmScale = "1 - 4 km",
        mScale = "1000 - 4000 m",
        miScale = "0.6 - 2.5 mi",
    ),
    VisibilityScaleRange(
        color = Red800,
        descriptionRes = R.string.visibility_description_very_poor,
        headlineRes = R.string.text_very_poor,
        kmScale = "0.2 - 1 km",
        mScale = "200 - 1000 m",
        miScale = "0.12 - 0.6 mi",
    ),
    VisibilityScaleRange(
        color = Purple800,
        descriptionRes = R.string.visibility_description_dense,
        headlineRes = R.string.text_dense_fog,
        kmScale = "< 0.2 km",
        mScale = "< 200 m",
        miScale = "< 0.12 mi",
    )
)

@Composable
private fun getVisibilityScaleFor(
    unit: DistanceUnit,
): List<VisibilityScale> {
    return visibilityRanges.map { range ->
        VisibilityScale(
            color = range.color,
            description = stringResource(range.descriptionRes),
            headline = stringResource(range.headlineRes),
            scale = when (unit) {
                DistanceUnit.KM -> range.kmScale
                DistanceUnit.MI -> range.miScale
                DistanceUnit.M -> range.mScale
            },
        )
    }
}
