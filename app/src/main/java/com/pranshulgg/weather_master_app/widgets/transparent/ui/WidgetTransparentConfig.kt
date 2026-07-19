package com.pranshulgg.weather_master_app.widgets.transparent.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
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
import kotlin.math.roundToInt

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun WidgetTransparentConfig(
    onDone: (WidgetConfig) -> Unit = {},
) {
    val buttonSize = ButtonDefaults.MediumContainerHeight
    val formats = listOf("EEE d MMM", "EEE MMM d", "EEE MM-dd")
    val formatsOptions = formats.map { DialogOption(dateFormatLabel(it), it) }
    val layoutDirection = LocalLayoutDirection.current

    var clockSize by remember { mutableFloatStateOf(36.00f) }
    var dateFormat by remember { mutableStateOf("EEE d MMM") }
    var showClock by remember { mutableStateOf(true) }

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
                shape = RoundedCornerShape(
                    size = ShapeRadius.ExtraLarge,
                ),
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

                    WidgetTransparentPreview(
                        clockSize = clockSize,
                        format = dateFormat,
                        showClock = showClock,
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
                        SettingTile.SwitchTile(
                            checked = showClock,
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_schedule_48,
                                )
                            },
                            onCheckedChange = {
                                showClock = it
                            },
                            title = "Show Clock",
                        ),
                        SettingTile.DialogSliderTile(
                            description = clockSize.roundToInt().toString(),
                            dialogTitle = "Clock Size",
                            initialValue = clockSize,
                            isDescriptionAsValue = true,
                            labelFormatter = {
                                it.roundToInt().toString()
                            },
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_format_size_24,
                                )
                            },
                            onValueChange = {
                                clockSize = it
                            },
                            onValueSubmitted = {
                                clockSize = it
                            },
                            title = "Clock Size",
                            valueRange = 20.00f..120.00f,
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
                                clockSize = clockSize,
                                dateFormat = dateFormat,
                                showClock = showClock,
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

@Composable
fun WidgetTransparentPreview(
    clockSize: Float = 32.00f,
    format: String,
    showClock: Boolean = true,
) {
    val date = when (format) {
        "EEE d MMM" -> "Fri 2 Jan"
        "EEE MMM d" -> "Fri Jan 2"
        "EEE MM-dd" -> "Fri 01-02"
        else -> "Fri 2 Jan"
    }
    val style = TextStyle(
        shadow = Shadow(
            blurRadius = 4.00f,
            color = colorResource(R.color.black).copy(
                alpha = 0.80f,
            ),
            offset = Offset(
                x = 2.00f,
                y = 2.00f,
            ),
        ),
    )

    Column(
        modifier = Modifier.padding(
            all = 16.dp,
        ),
    ) {
        FlowRow(
            itemVerticalAlignment = Alignment.Bottom,
        ) {
            if (showClock) {
                Text(
                    color = colorResource(R.color.gray_e),
                    fontSize = clockSize.sp,
                    fontWeight = FontWeight.Bold,
                    style = style,
                    text = "13:37",
                )
            }

            Gap(
                horizontal = 10.dp,
            )

            Text(
                color = colorResource(R.color.gray_e),
                fontSize = 20.sp,
                style = style,
                text = date,
            )
        }

        Gap(
            vertical = 5.dp,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherIconBox(
                icon = R.drawable.il_weather_partly_cloudy_day,
                size = 24.dp,
            )

            Gap(
                horizontal = 5.dp,
            )

            Text(
                color = colorResource(R.color.gray_e),
                fontSize = 18.sp,
                style = style,
                text = "37° • ",
            )

            Text(
                color = colorResource(R.color.gray_e),
                fontSize = 18.sp,
                style = style,
                text = "Partly Cloudy",
            )
        }
    }
}
