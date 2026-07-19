package com.pranshulgg.weather_master_app.widgets.daily.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogOption
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.widgets.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.dateFormatLabel
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetThemeText
import kotlin.math.round
import kotlin.math.roundToInt

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun WidgetDailyConfig(
    onDone: (WidgetConfig) -> Unit = {},
) {
    val buttonSize = ButtonDefaults.MediumContainerHeight
    val formats = listOf(
        "EEE d MMM",
        "EEE MMM d",
        "EEE MM-dd",
    )
    val formatsOptions = formats.map { DialogOption(dateFormatLabel(it), it) }
    val layoutDirection = LocalLayoutDirection.current
    val widgetThemeOptions = WidgetTheme.entries.map { DialogOption(stringResource(it.label), it.toString()) }
    val widgetThemeTextOptions = WidgetThemeText.entries.map { DialogOption(stringResource(it.label), it.toString()) }

    var dateFormat by remember { mutableStateOf("EEE d MMM") }
    var selectedDailyCount by remember { mutableFloatStateOf(4.00f) }
    var selectedFontSize by remember { mutableFloatStateOf(1.00f) }
    var selectedIconSize by remember { mutableFloatStateOf(1.00f) }
    var widgetTheme by remember { mutableStateOf(WidgetTheme.SYSTEM) }
    var widgetThemeText by remember { mutableStateOf(WidgetThemeText.SYSTEM) }

    val applyWidgetTheme = { value: String ->
        widgetTheme = when (value) {
            "DARK" -> WidgetTheme.DARK
            "LIGHT" -> WidgetTheme.LIGHT
            "SYSTEM" -> WidgetTheme.SYSTEM
            "TRANSPARENT" -> WidgetTheme.TRANSPARENT
            else -> WidgetTheme.SYSTEM
        }
    }
    val applyWidgetThemeText = { value: String ->
        widgetThemeText = when (value) {
            "DARK" -> WidgetThemeText.DARK
            "LIGHT" -> WidgetThemeText.LIGHT
            "SYSTEM" -> WidgetThemeText.SYSTEM
            else -> WidgetThemeText.SYSTEM
        }
    }

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

                    WidgetDailyPreview(
                        dailyCount = selectedDailyCount,
                        format = dateFormat,
                        fontSize = selectedFontSize,
                        iconSize = selectedIconSize,
                        textTheme = widgetThemeText,
                        widgetTheme = widgetTheme,
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

                SettingSection(
                    tiles = listOf(
                        SettingTile.DialogOptionTile(
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_palette_24,
                                )
                            },
                            onOptionChange = applyWidgetTheme,
                            onOptionSelected = applyWidgetTheme,
                            options = widgetThemeOptions,
                            selectedOption = widgetTheme.toString(),
                            title = "Background Color",
                        ),
                        SettingTile.DialogOptionTile(
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_format_paint_24,
                                )
                            },
                            onOptionChange = applyWidgetThemeText,
                            onOptionSelected = applyWidgetThemeText,
                            options = widgetThemeTextOptions,
                            selectedOption = widgetThemeText.toString(),
                            title = "Text Color",
                        ),
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
                        SettingTile.DialogOptionTile(
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_calendar_date_24,
                                )
                            },
                            onOptionChange = {
                                dateFormat = it
                            },
                            onOptionSelected = {
                                dateFormat = it
                            },
                            options = formatsOptions,
                            selectedOption = dateFormat,
                            title = "Date Format",
                        ),
                        SettingTile.DialogSliderTile(
                            description = "${selectedDailyCount.roundToInt()} days",
                            dialogTitle = "Forecast Count",
                            initialValue = selectedDailyCount,
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
                                selectedDailyCount = it
                            },
                            onValueSubmitted = {
                                selectedDailyCount = it
                            },
                            steps = 3,
                            title = "Forecast Count",
                            valueRange = 2f..6f,
                        ),
                    ),
                )

                Spacer(
                    modifier = Modifier.weight(
                        weight = 1.00f,
                    ),
                )

                Button(
                    onClick = {
                        onDone(
                            WidgetConfig(
                                dailyCount = selectedDailyCount.roundToInt(),
                                dateFormat = dateFormat,
                                fontSize = selectedFontSize,
                                iconSize = selectedIconSize,
                                widgetTheme = widgetTheme,
                                widgetThemeText = widgetThemeText,
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = buttonSize,
                        )
                        .padding(
                            all = 16.dp,
                        ),
                    contentPadding = ButtonDefaults.contentPaddingFor(
                        buttonHeight = buttonSize,
                    ),
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(
                        text = "Create Widget",
                        style = ButtonDefaults.textStyleFor(
                            buttonHeight = buttonSize,
                        ),
                    )
                }
            }
        }
    }
}

private data class Daily(
    val day: String,
    val icon: Int,
    val temps: String,
)

@Composable
fun WidgetDailyPreview(
    dailyCount: Float,
    fontSize: Float,
    format: String,
    iconSize: Float,
    textTheme: WidgetThemeText,
    widgetTheme: WidgetTheme,
) {
    val textColor = when (textTheme) {
        WidgetThemeText.DARK -> Color.Black
        WidgetThemeText.LIGHT -> Color.White
        WidgetThemeText.SYSTEM -> when (widgetTheme) {
            WidgetTheme.DARK -> Color.White
            WidgetTheme.LIGHT -> Color.Black
            WidgetTheme.TRANSPARENT -> Color.White
            else -> MaterialTheme.colorScheme.onSurface
        }
    }

    val widgetColor = when (widgetTheme) {
        WidgetTheme.DARK -> Color.Black
        WidgetTheme.LIGHT -> Color.White
        WidgetTheme.SYSTEM -> MaterialTheme.colorScheme.surfaceContainerHighest
        WidgetTheme.TRANSPARENT -> Color.Transparent
    }

    val date = when (format) {
        "EEE d MMM" -> "Fri 2 Jan"
        "EEE MMM d" -> "Fri Jan 2"
        "EEE MM-dd" -> "Fri 01-02"
        else -> "Fri 2 Jan"
    }

    val days = listOf(
        Daily(
            day = "Sun",
            icon = R.drawable.il_weather_partly_cloudy_day,
            temps = "34°/23°",
        ),
        Daily(
            day = "Mon",
            icon = R.drawable.il_weather_partly_cloudy_day,
            temps = "31°/23°",
        ),
        Daily(
            day = "Tue",
            icon = R.drawable.il_weather_partly_cloudy_day,
            temps = "33°/24°",
        ),
        Daily(
            day = "Wed",
            icon = R.drawable.il_weather_clear_day,
            temps = "35°/26°",
        ),
        Daily(
            day = "Thu",
            icon = R.drawable.il_weather_overcast,
            temps = "30°/25°",
        ),
        Daily(
            day = "Fri",
            icon = R.drawable.il_weather_overcast,
            temps = "30°/25°",
        ),
        Daily(
            day = "Sat",
            temps = "30°/25°",
            icon = R.drawable.il_weather_overcast,
        ),
    )

    val clockFontSize = 50 * fontSize
    val dateConditionFontSize = 18 * fontSize
    val mainIconSize = 52 * iconSize

    Column(
        modifier = Modifier
            .background(
                color = widgetColor,
                shape = RoundedCornerShape(
                    size = ShapeRadius.ExtraLarge,
                ),
            )
            .fillMaxWidth()
            .height(
                height = 220.dp,
            ),
    ) {
        Column(
            modifier = Modifier.padding(
                bottom = 12.dp,
                end = 18.dp,
                start = 18.dp,
                top = 18.dp,
            ),
        ) {
            Row {
                Text(
                    color = textColor,
                    fontSize = clockFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    text = "13:37",
                )

                Spacer(
                    modifier = Modifier.weight(
                        weight = 1.00f,
                    ),
                )

                WeatherIconBox(
                    icon = R.drawable.il_weather_clear_day,
                    size = mainIconSize.dp,
                )
            }

            Row {
                Text(
                    color = textColor,
                    fontSize = dateConditionFontSize.sp,
                    text = date,
                )

                Spacer(
                    modifier = Modifier.weight(
                        weight = 1.00f,
                    ),
                )

                Text(
                    color = textColor,
                    fontSize = dateConditionFontSize.sp,
                    text = "37° Clear Sky",
                )
            }

            Spacer(
                modifier = Modifier.weight(
                    weight = 1.00f,
                ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                days.take(dailyCount.roundToInt()).forEach {
                    Column(
                        modifier = Modifier
                            .padding(
                                vertical = 5.dp,
                            )
                            .weight(
                                weight = 1.00f,
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        WidgetDailyItem(
                            day = it.day,
                            fontSize = fontSize,
                            icon = it.icon,
                            iconSize = iconSize,
                            temperatures = it.temps,
                            textColor = textColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetDailyItem(
    day: String,
    fontSize: Float,
    icon: Int,
    iconSize: Float,
    temperatures: String,
    textColor: Color,
) {
    val fontSize = 15 * fontSize
    val iconSize = 28 * iconSize

    Text(
        color = textColor,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Medium,
        text = day,
    )

    Spacer(
        modifier = Modifier.height(
            height = 3.dp,
        ),
    )

    WeatherIconBox(
        icon = icon,
        size = iconSize.dp,
    )

    Spacer(
        modifier = Modifier.height(
            height = 3.dp,
        ),
    )

    Text(
        color = textColor,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Medium,
        text = temperatures,
    )
}

fun getStringFromFloat(
    float: Float,
): String {
    return "${round(float * 100).toInt()}%"
}
