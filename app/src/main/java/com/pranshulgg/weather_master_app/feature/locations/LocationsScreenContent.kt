package com.pranshulgg.weather_master_app.feature.locations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.TouchMinimum
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.toIcon
import com.pranshulgg.weather_master_app.core.model.weather.toLabel
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.feature.shared.components.LocationItem
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.roundToInt

@Composable
fun LocationsScreenContent(
    activeLocation: Location? = null,
    isDeviceLocationLoading: Boolean,
    isReordering: Boolean = false,
    locations: List<Location>,
    onAddCurrentLocation: () -> Unit,
    onEnableBackgroundLocation: () -> Unit = {},
    onLocationSelect: (Location) -> Unit,
    onLongClick: (Location) -> Unit,
    onReorder: (List<String>) -> Unit = {},
    showBackgroundLocationCard: Boolean = false,
    units: WeatherUnits,
    weatherForLocations: List<Weather> = emptyList(),
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val showDeviceLocationCard = locations.none { it.isDeviceLocation }
    val weatherMap = weatherForLocations.associateBy { it.location.id }

    // Local editable copy so drags update the list instantly; kept in sync with the
    // persisted order whenever the upstream list changes (add / delete / reorder).
    var ordered by remember { mutableStateOf(locations) }
    LaunchedEffect(locations) {
        ordered = locations
    }

    val lazyListState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
        ordered = ordered.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    LazyColumn(
        modifier = Modifier.padding(
            horizontal = 16.dp,
        ),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(
            space = 2.dp,
        ),
    ) {
        itemsIndexed(
            items = ordered,
            key = { _, item -> item.id }
        ) { index, location ->
            val weather = weatherMap[location.id]

            val description =
                if (weather != null && weather.current.lastUpdatedInMilli != -1L) {
                    weather.current.weatherCondition.toLabel(
                        context = context,
                    )
                } else {
                    stringResource(R.string.weather_no_data)
                }
            val icon = weather?.current?.weatherCondition ?: WeatherCondition.NO_CONDITION_FOUND
            val isFirst = index == 0
            val isLast = index == ordered.lastIndex
            val isOnly = ordered.size == 1
            val shape = when {
                isOnly -> RoundedCornerShape(
                    size = ShapeRadius.Large,
                )

                isFirst -> RoundedCornerShape(
                    bottomEnd = ShapeRadius.ExtraSmall,
                    bottomStart = ShapeRadius.ExtraSmall,
                    topEnd = ShapeRadius.Large,
                    topStart = ShapeRadius.Large,
                )

                isLast -> RoundedCornerShape(
                    bottomEnd = ShapeRadius.Large,
                    bottomStart = ShapeRadius.Large,
                    topEnd = ShapeRadius.ExtraSmall,
                    topStart = ShapeRadius.ExtraSmall,
                )

                else -> RoundedCornerShape(
                    size = ShapeRadius.ExtraSmall,
                )
            }

            ReorderableItem(
                key = location.id,
                state = reorderState,
            ) {
                LocationItem(
                    description = description,
                    dragHandle = {
                        Box(
                            modifier = Modifier
                                .defaultMinSize(
                                    minHeight = TouchMinimum,
                                    minWidth = TouchMinimum,
                                )
                                .draggableHandle(
                                    onDragStarted = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    onDragStopped = {
                                        onReorder(ordered.map { it.id })
                                    },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Symbol(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                description = stringResource(R.string.action_reorder_locations),
                                icon = R.drawable.ic_drag_handle_24,
                            )
                        }
                    },
                    icon = icon.toIcon(
                        daily = weather?.daily?.firstOrNull(),
                        targetTimeMilli = weather?.current?.time ?: System.currentTimeMillis(),
                    ),
                    isDefault = location.isDefault,
                    isDeviceLocation = location.isDeviceLocation,
                    isReordering = isReordering,
                    isSelected = location.id == activeLocation?.id,
                    onClick = {
                        onLocationSelect(location)
                    },
                    onLongClick = {
                        onLongClick(location)
                    },
                    shape = shape,
                    temperature = TemperatureUnit.CELSIUS.convert(
                        from = weather?.current?.temperature,
                        to = units.temperature,
                    )?.roundToInt(),
                    title = location.name,
                )
            }

            if (index == ordered.size - 1) {
                Gap(
                    vertical = 8.dp,
                )
            }
        }

        // The add-current-location and background-location prompts are hidden while
        // reordering to keep the focus on reordering the saved locations.
        // TODO: Find better way to add current location.
        item(
            key = "use_device_location_card",
        ) {
            AnimatedVisibility(
                enter = fadeIn(),
                exit = fadeOut(),
                visible = isReordering.not() && showDeviceLocationCard,
            ) {
                Column {
                    UseDeviceLocationCard(
                        isLoading = isDeviceLocationLoading,
                        onClick = {
                            if (isDeviceLocationLoading.not()) {
                                onAddCurrentLocation()
                            }
                        },
                    )

                    Gap(
                        vertical = 8.dp,
                    )
                }
            }
        }

        // Only reachable when a device location already exists, so it never overlaps
        // with the add-current-location card above.
        item(
            key = "use_background_location_card",
        ) {
            AnimatedVisibility(
                enter = fadeIn(),
                exit = fadeOut(),
                visible = isReordering.not() && showBackgroundLocationCard,
            ) {
                Column {
                    UseDeviceLocationCard(
                        onClick = onEnableBackgroundLocation,
                        supportingText = stringResource(R.string.background_location_use_current_secondary),
                    )

                    Gap(
                        vertical = 8.dp,
                    )
                }
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun UseDeviceLocationCard(
    onClick: () -> Unit,
    isLoading: Boolean = false,
    supportingText: String = stringResource(R.string.location_use_current_secondary),
) {
    Surface(
        modifier = Modifier.clip(
            shape = RoundedCornerShape(
                size = ShapeRadius.ExtraLarge
            ),
        ),
        color = MaterialTheme.colorScheme.surfaceBright,
        onClick = onClick,
        shape = RoundedCornerShape(
            size = ShapeRadius.ExtraLarge,
        ),
    ) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceBright,
            ),
            leadingContent = {
                Box(
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = CircleShape,
                        )
                        .size(
                            size = 52.dp,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (!isLoading) {
                        SettingsTileIcon(
                            icon = R.drawable.ic_location_searching_24,
                        )
                    } else {
                        LoadingIndicator()
                    }
                }
            },
            headlineContent = {
                Text(
                    color = MaterialTheme.colorScheme.onSurface,
                    text = stringResource(R.string.location_use_current),
                )
            },
            supportingContent = {
                Text(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = supportingText,
                )
            },
        )
    }
}
