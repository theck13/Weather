package com.pranshulgg.weather_master_app.feature.shared.components.blocks

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlockType
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.air.AirQualityIndexStandard
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.navigation.NavigationRoutes
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isCurrentAirQualitySafe
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel
import sh.calvin.reorderable.DragGestureDetector
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

private data class BlockRules(
    val isAirQualityValid: Boolean,
    val isDaily: Boolean,
    val isHumidityValid: Boolean,
    val isPrecipitation: Boolean,
    val isPressureValid: Boolean,
    val isUvIndexValid: Boolean,
    val isVisibilityValid: Boolean,
    val isWindValid: Boolean,
    val rainForTheDay: Double,
    val snowForTheDay: Double,
)

private fun shouldShow(
    block: WeatherBlock,
    rules: BlockRules,
): Boolean {
    return when {
        block.isHidden -> false
        rules.isAirQualityValid.not() && block.type == WeatherBlockType.AIR_QUALITY_BLOCK -> false
        rules.isDaily != block.isDaily -> false
        rules.isHumidityValid.not() && block.type == WeatherBlockType.HUMIDITY_BLOCK -> false
        rules.isPrecipitation && block.type == WeatherBlockType.SNOW_BLOCK -> false
        rules.isPressureValid.not() && block.type == WeatherBlockType.PRESSURE_BLOCK -> false
        rules.isUvIndexValid.not() && block.type == WeatherBlockType.UV_INDEX_BLOCK -> false
        rules.isVisibilityValid.not() && block.type == WeatherBlockType.VISIBILITY_BLOCK -> false
        rules.isWindValid.not() && block.type == WeatherBlockType.WIND_BLOCK -> false
//        rules.rainForTheDay == 0.0 && block.type == WeatherBlockType.RAIN_BLOCK -> false
//        rules.snowForTheDay == 0.0 && block.type == WeatherBlockType.SNOW_BLOCK -> false
        else -> true
    }
}

@Composable
fun WeatherBlocks(
    airQuality: AirQuality?,
    blocks: List<WeatherBlock>,
    context: Context,
    dailyIndex: Int = 0,
    isDaily: Boolean = false,
    navController: NavController,
    units: WeatherUnits,
    // Need this so daily screen can also update block order.
    updatedBlockOrder: (List<WeatherBlock>) -> Unit = {},
    weather: Weather,
) {
    val preferences = LocalAppPrefs.current
    val viewModel: WeatherViewModel = hiltViewModel()

    val isAirQualityValid = airQuality != null && isCurrentAirQualitySafe(airQuality)
    val isHumidityValid = if (isDaily) weather.daily[dailyIndex].isHumidityValid() else true
    val isPrecipitation = weather.location.source.providesSnowFall().not() // Some sources do not provide rain/snow precipitation separately.
    val isPressureValid = if (isDaily) weather.daily[dailyIndex].isPressureValid() else weather.current.isPressureValid()
    val isUvIndexValid = weather.daily[dailyIndex].isUltravioletMaxValid() && weather.current.isUltravioletValid()
    val isVisibilityValid = if (isDaily) weather.daily[dailyIndex].isVisibilityValid() else weather.current.isVisibilityValid()
    val isWindValid = if (isDaily) weather.daily[dailyIndex].isWindSpeedValid() else weather.current.isWindSpeedValid()
    val rainForTheDay = PrecipitationUnit.MM.convert(
        from = weather.daily[dailyIndex].rainSum,
        to = units.precipitation,
    ) ?: 0.0
    val snowForTheDay = PrecipitationUnit.CM.convert(
        from = weather.daily[dailyIndex].snowfallSum,
        to = units.precipitation,
    ) ?: 0.0

    val rules = BlockRules(
        isAirQualityValid,
        isDaily,
        isHumidityValid,
        isPrecipitation,
        isPressureValid,
        isUvIndexValid,
        isVisibilityValid,
        isWindValid,
        rainForTheDay,
        snowForTheDay,
    )

    val items = blocks.filter { shouldShow(it, rules) }
    val blocksHidden = blocks.filter { it !in items }
    val haptic = LocalHapticFeedback.current
    val lazyGridState = rememberLazyGridState()
    val onClickBlock: (String) -> Unit = {
        navController.navigate(
            NavigationRoutes.block(it, dailyIndex, weather.location.id)
        )
    }

    val reorderableState = rememberReorderableLazyGridState(
        lazyGridState = lazyGridState,
        onMove = { from, to ->
            val updated = items.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }

            updatedBlockOrder(updated)
            viewModel.saveBlocks(
                isDaily = isDaily,
                items = updated.plus(
                    elements = blocksHidden,
                ),
            )
        },
    )

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .heightIn(
                max = 1500.dp,
            ),
        columns = GridCells.Adaptive(
            minSize = 140.dp,
        ),
        contentPadding = PaddingValues(
            vertical = 8.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(
            space = 16.dp,
        ),
        state = lazyGridState,
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
        ),
    ) {
        items(
            items = items,
            key = { "${it.id}_${it.type}" },
        ) { item ->
            ReorderableItem(
                key = "${item.id}_${item.type}",
                state = reorderableState,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .draggableHandle(
                            dragGestureDetector = DragGestureDetector.LongPress,
                            onDragStarted = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                        ),
                ) {
                    when (item.type) {
                        WeatherBlockType.AIR_QUALITY_BLOCK -> AirQualityBlock(
                            airQuality = airQuality,
                            context = context,
                            onClickBlock = {
                                onClickBlock(NavigationRoutes.AIR)
                            },
                            standard = AirQualityIndexStandard.forCountryCode(
                                countryCode = weather.location.countryCode,
                            ),
                        )

                        WeatherBlockType.HUMIDITY_BLOCK -> HumidityBlock(
                            dailyIndex = dailyIndex,
                            isDaily = isDaily,
                            onClickBlock = {
                                onClickBlock(NavigationRoutes.HUMIDITY)
                            },
                            units = units,
                            weather = weather,
                        )

                        WeatherBlockType.MOON_BLOCK -> CelestialBlock(
                            index = dailyIndex,
                            onClick = {
                                onClickBlock(NavigationRoutes.CELESTIAL)
                            },
                            state = preferences,
                            type = CelestialType.Moon,
                            weather = weather,
                        )

                        WeatherBlockType.PRESSURE_BLOCK -> PressureBlock(
                            context = context,
                            dailyIndex = dailyIndex,
                            isDaily = isDaily,
                            onClickBlock = {
                                onClickBlock(NavigationRoutes.PRESSURE)
                            },
                            units = units,
                            weather = weather,
                        )

                        WeatherBlockType.RAIN_BLOCK -> RainBlock(
                            context = context,
                            isPrecipitation = isPrecipitation,
                            onClickBlock = {
                                onClickBlock(NavigationRoutes.RAIN)
                            },
                            rainForTheDay = rainForTheDay,
                            units = units,
                        )

                        WeatherBlockType.SNOW_BLOCK -> SnowBlock(
                            context = context,
                            onClickBlock = {
                                onClickBlock(NavigationRoutes.SNOW)
                            },
                            snowForTheDay = snowForTheDay,
                            units = units,
                        )

                        WeatherBlockType.SUN_BLOCK -> CelestialBlock(
                            index = dailyIndex,
                            onClick = {
                                onClickBlock(NavigationRoutes.CELESTIAL)
                            },
                            state = preferences,
                            type = CelestialType.Sun,
                            weather = weather,
                        )

                        WeatherBlockType.UV_INDEX_BLOCK -> UltravioletBlock(
                            context = context,
                            dailyIndex = dailyIndex,
                            isDaily = isDaily,
                            onClickBlock = {
                                onClickBlock(NavigationRoutes.ULTRAVIOLET)
                            },
                            weather = weather,
                        )

                        WeatherBlockType.VISIBILITY_BLOCK -> VisibilityBlock(
                            context = context,
                            dailyIndex = dailyIndex,
                            isDaily = isDaily,
                            onClickBlock = {
                                onClickBlock(NavigationRoutes.VISIBILITY)
                            },
                            units = units,
                            weather = weather,
                        )

                        WeatherBlockType.WIND_BLOCK -> WindBlock(
                            context = context,
                            dailyIndex = dailyIndex,
                            isDaily = isDaily,
                            onClickBlock = {
                                onClickBlock(NavigationRoutes.WIND)
                            },
                            units = units,
                            weather = weather,
                        )
                    }
                }
            }
        }
    }
}
