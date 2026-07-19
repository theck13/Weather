package com.heckofanapp.weather.widgets.hourly.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.SettingSection
import com.heckofanapp.weather.core.ui.components.SettingTile
import com.heckofanapp.weather.core.ui.components.SettingsTileIcon
import com.heckofanapp.weather.core.ui.components.WeatherIconBox
import com.heckofanapp.weather.core.ui.theme.ShapeRadius
import com.heckofanapp.weather.widgets.WidgetConfig
import com.heckofanapp.weather.widgets.model.WidgetVariant
import kotlin.math.round
import kotlin.math.roundToInt

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun WidgetHourlyConfig(
    onDone: (WidgetConfig) -> Unit = {},
) {
    val buttonSize = ButtonDefaults.MediumContainerHeight
    val layoutDirection = LocalLayoutDirection.current

    var selectedHourlyCount by remember { mutableFloatStateOf(6.00f) }
    var selectedFontSize by remember { mutableFloatStateOf(1.00f) }
    var selectedIconSize by remember { mutableFloatStateOf(1.00f) }
    var selectedVariant by remember { mutableStateOf(WidgetVariant.LARGE) }

    Scaffold(
        containerColor = Color.Transparent,
    ) { paddingValues ->
        Column(
            Modifier.fillMaxSize(),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 16.dp,
                        end = paddingValues.calculateEndPadding(
                            layoutDirection = layoutDirection,
                        ) + 16.dp,
                        start = paddingValues.calculateStartPadding(
                            layoutDirection = layoutDirection,
                        ) + 16.dp,
                        top = 16.dp,
                    ),
                color = Color.Transparent,
            ) {
                Column(
                    modifier = Modifier.weight(
                        weight = 1.00f,
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Gap(
                        vertical = paddingValues.calculateTopPadding(),
                    )

                    WidgetHourlyPreview(
                        fontSize = selectedFontSize,
                        hourlyCount = selectedHourlyCount,
                        iconSize = selectedIconSize,
                        variant = selectedVariant,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .background(
                        color = colorResource(R.color.background),
                    )
                    .fillMaxSize()
                    .verticalScroll(
                        state = rememberScrollState(),
                    )
                    .padding(
                        bottom = paddingValues.calculateBottomPadding(),
                        end = paddingValues.calculateEndPadding(
                            layoutDirection = layoutDirection,
                        ),
                        start = paddingValues.calculateStartPadding(
                            layoutDirection = layoutDirection,
                        ),
                    ),
            ) {
                Gap(
                    vertical = 16.dp,
                )

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 6.dp,
                    ),
                ) {
                    WidgetVariant.entries.forEach { item ->
                        ToggleButton(
                            modifier = Modifier.semantics {
                                role = Role.RadioButton
                            },
                            checked = selectedVariant == item,
                            colors = ToggleButtonDefaults.toggleButtonColors(
                                checkedContainerColor = MaterialTheme.colorScheme.tertiary,
                                checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            ),
                            onCheckedChange = {
                                selectedVariant = item
                            },
                            shapes = ToggleButtonDefaults.shapes(),
                        ) {
                            Text(
                                text = item.label,
                            )
                        }
                    }
                }

                Gap(
                    vertical = 8.dp,
                )

                SettingSection(
                    tiles = listOf(
                        SettingTile.DialogSliderTile(
                            description = getStringFromFloat(
                                float = selectedIconSize,
                            ),
                            dialogTitle = "Icon Size",
                            initialValue = selectedIconSize,
                            isDescriptionAsValue = true,
                            labelFormatter = {
                                getStringFromFloat(
                                    float = it,
                                )
                            },
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_photo_size_select_large_24,
                                )
                            },
                            onValueChange = {
                                selectedIconSize = it
                            },
                            onValueSubmitted = {
                                selectedIconSize = it
                            },
                            title = "Icon Size",
                            valueRange = 0.00f..1.00f,
                        ),
                        SettingTile.DialogSliderTile(
                            description = getStringFromFloat(
                                float = selectedFontSize,
                            ),
                            dialogTitle = "Text Size",
                            initialValue = selectedFontSize,
                            isDescriptionAsValue = true,
                            labelFormatter = {
                                getStringFromFloat(
                                    float = it,
                                )
                            },
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_format_size_24,
                                )
                            },
                            onValueChange = {
                                selectedFontSize = it
                            },
                            onValueSubmitted = {
                                selectedFontSize = it
                            },
                            title = "Text Size",
                            valueRange = 0.00f..1.00f,
                        ),
                        SettingTile.DialogSliderTile(
                            description = "${selectedHourlyCount.roundToInt()} hours",
                            dialogTitle = "Forecast Count",
                            enabled = selectedVariant == WidgetVariant.LARGE,
                            initialValue = selectedHourlyCount,
                            isDescriptionAsValue = true,
                            labelFormatter = {
                                "${it.roundToInt()}"
                            },
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_date_range_24,
                                )
                            },
                            onValueChange = {
                                selectedHourlyCount = it
                            },
                            onValueSubmitted = {
                                selectedHourlyCount = it
                            },
                            steps = 9,
                            title = "Forecast Count",
                            valueRange = 2f..12f,
                        ),
                    ),
                )

                Spacer(
                    modifier = Modifier.weight(
                        weight = 1.00f,
                    ),
                )

                Button(
                    modifier = Modifier
                        .heightIn(
                            min = buttonSize,
                        )
                        .fillMaxWidth()
                        .padding(
                            all = 16.dp,
                        ),
                    contentPadding = ButtonDefaults.contentPaddingFor(
                        buttonHeight = buttonSize,
                    ),
                    onClick = {
                        onDone(
                            WidgetConfig(
                                fontSize = selectedFontSize,
                                hourlyCount = selectedHourlyCount.roundToInt(),
                                iconSize = selectedIconSize,
                                variant = selectedVariant,
                            ),
                        )
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(
                        style = ButtonDefaults.textStyleFor(
                            buttonHeight = buttonSize,
                        ),
                        text = "Create Widget",
                    )
                }
            }
        }
    }
}

@Composable
fun WidgetHourlyPreview(
    fontSize: Float,
    hourlyCount: Float,
    iconSize: Float,
    variant: WidgetVariant,
) {
    when (variant) {
        WidgetVariant.LARGE -> {
            WidgetHourlyPreviewLarge(
                fontSize = fontSize,
                hourlyCount = hourlyCount,
                iconSize = iconSize,
            )
        }

        WidgetVariant.MEDIUM -> {
            WidgetHourlyPreviewMedium(
                fontSize = fontSize,
                iconSize = iconSize,
            )
        }

        WidgetVariant.SMALL -> {
            WidgetHourlyPreviewSmall(
                fontSize = fontSize,
                iconSize = iconSize,
            )
        }
    }
}

@Composable
fun WidgetHourlyPreviewLarge(
    fontSize: Float,
    hourlyCount: Float,
    iconSize: Float,
) {
    val icons = listOf(
        R.drawable.il_weather_clear_night,
        R.drawable.il_weather_partly_cloudy_night,
        R.drawable.il_weather_partly_cloudy_night,
        R.drawable.il_weather_partly_cloudy_night,
        R.drawable.il_weather_clear_night,
        R.drawable.il_weather_clear_night,
        R.drawable.il_weather_clear_night,
        R.drawable.il_weather_mostly_clear_night,
        R.drawable.il_weather_mostly_clear_night,
        R.drawable.il_weather_mostly_clear_night,
        R.drawable.il_weather_clear_night,
        R.drawable.il_weather_clear_night,
        R.drawable.il_weather_clear_night,
    )
    val temps = listOf(
        28,
        27,
        27,
        26,
        26,
        24,
        22,
        21,
        20,
        19,
        18,
        17,
        15,
    )
    val times = listOf(
        "7PM",
        "8PM",
        "9PM",
        "10PM",
        "11PM",
        "12AM",
        "1AM",
        "2AM",
        "3AM",
        "4AM",
        "5AM",
        "6AM",
        "7AM",
    )

    val hourlyIconSize = 22 * iconSize
    val hourlyTextSize = 14 * fontSize
    val hourlyItem: @Composable (String, Int, Int) -> Unit = { time, temp, icon ->
        Column(
            modifier = Modifier.padding(
                horizontal = 5.dp,
                vertical = 5.dp,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 3.dp,
            ),
        ) {
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = hourlyTextSize.sp,
                fontWeight = FontWeight.Medium,
                text = "${temp}°",
            )

            WeatherIconBox(
                icon = icon,
                size = hourlyIconSize.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = hourlyTextSize.sp,
                fontWeight = FontWeight.Medium,
                text = time,
            )
        }
    }

    val mainIconSize = 32 * iconSize
    val textFontSize = 18 * fontSize
    val locationFontSize = 16 * fontSize
    val tempFontSize = 42 * fontSize

    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(
                    size = ShapeRadius.ExtraLarge,
                ),
            )
            .fillMaxWidth()
            .height(
                height = 230.dp,
            ),
    ) {
        Column(
            modifier = Modifier.padding(
                all = 18.dp,
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Row {
                        Text(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = textFontSize.sp,
                            fontWeight = FontWeight.Medium,
                            text = "37°",
                        )

                        Gap(
                            horizontal = 8.dp,
                        )

                        Text(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = textFontSize.sp,
                            fontWeight = FontWeight.Medium,
                            text = "13°",
                        )
                    }

                    Gap(
                        vertical = 6.dp,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        WeatherIconBox(
                            modifier = Modifier.padding(
                                end = mainIconSize.dp / 5,
                            ),
                            icon = R.drawable.il_weather_clear_night,
                            size = mainIconSize.dp,
                        )

                        Text(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = textFontSize.sp,
                            text = "Clear Sky",
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.weight(
                        weight = 1.00f,
                    ),
                )

                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        fontSize = locationFontSize.sp,
                        text = "Denver",
                    )

                    Text(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = tempFontSize.sp,
                        fontWeight = FontWeight.Bold,
                        text = "13°",
                    )
                }
            }

            Spacer(
                modifier = Modifier.weight(
                    weight = 1.00f,
                ),
            )

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(
                    size = 16.dp,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            state = rememberScrollState(),
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    times.take(hourlyCount.roundToInt()).forEachIndexed { index, string ->
                        hourlyItem(
                            string,
                            temps[index],
                            icons[index]
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetHourlyPreviewMedium(
    fontSize: Float,
    iconSize: Float,
) {
    val colorScheme = MaterialTheme.colorScheme
    val mainIconSize = 28 * iconSize
    val tempFontSize = 54 * fontSize
    val textFontSize = 18 * fontSize

    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(
                    size = ShapeRadius.ExtraLarge,
                ),
            )
            .height(
                height = 180.dp,
            )
            .width(
                width = 180.dp,
            ),
    ) {
        Column(
            Modifier.padding(
                all = 18.dp,
            ),
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WeatherIconBox(
                    icon = R.drawable.il_weather_clear_night,
                    size = mainIconSize.dp,
                )

                Spacer(
                    modifier = Modifier.weight(
                        weight = 1.00f,
                    ),
                )

                Text(
                    color = colorScheme.onSurface,
                    fontSize = textFontSize.sp,
                    text = "Clear Sky",
                )
            }

            Spacer(
                modifier = Modifier.weight(
                    weight = 1.00f,
                ),
            )

            Text(
                color = colorScheme.primary,
                fontSize = tempFontSize.sp,
                fontWeight = FontWeight.Bold,
                text = "13°",
            )

            Gap(
                horizontal = 6.dp,
            )

            Row {
                Text(
                    color = colorScheme.onSurface,
                    fontSize = textFontSize.sp,
                    fontWeight = FontWeight.Medium,
                    text = "37°",
                )

                Gap(
                    horizontal = 6.dp,
                )

                Text(
                    color = colorScheme.onSurfaceVariant,
                    fontSize = textFontSize.sp,
                    fontWeight = FontWeight.Medium,
                    text = "13°",
                )
            }
        }
    }
}

@Composable
fun WidgetHourlyPreviewSmall(
    fontSize: Float,
    iconSize: Float,
) {
    val colorScheme = MaterialTheme.colorScheme
    val fontSize = 54 * fontSize
    val iconSize = 48 * iconSize

    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(
                    size = ShapeRadius.ExtraLarge,
                ),
            )
            .height(
                height = 180.dp,
            )
            .width(
                width = 150.dp,
            ),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(
                    all = 24.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            WeatherIconBox(
                icon = R.drawable.il_weather_clear_night,
                size = iconSize.dp,
            )

            Gap(
                vertical = 8.dp,
            )

            Text(
                color = colorScheme.primary,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                text = "16°",
            )
        }
    }
}

fun getStringFromFloat(
    float: Float,
): String {
    return "${round(float * 100).toInt()}%"
}
