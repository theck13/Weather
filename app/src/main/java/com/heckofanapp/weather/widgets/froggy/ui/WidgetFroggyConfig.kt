package com.heckofanapp.weather.widgets.froggy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.SpaceDefault
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.SettingSection
import com.heckofanapp.weather.core.ui.components.SettingTile
import com.heckofanapp.weather.core.ui.components.SettingsTileIcon
import com.heckofanapp.weather.core.ui.components.WeatherIconBox
import com.heckofanapp.weather.core.ui.components.tiles.DialogOption
import com.heckofanapp.weather.core.ui.theme.ShapeRadius
import com.heckofanapp.weather.widgets.WidgetConfig
import com.heckofanapp.weather.widgets.ui.colors.WidgetTheme
import com.heckofanapp.weather.widgets.ui.colors.WidgetThemeText
import kotlin.math.round

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun WidgetFroggyConfig(
    onDone: (WidgetConfig) -> Unit = {},
) {
    val buttonSize = ButtonDefaults.MediumContainerHeight
    val layoutDirection = LocalLayoutDirection.current
    val widgetThemeOptions = WidgetTheme.entries.map { DialogOption(stringResource(it.label), it.toString()) }
    val widgetThemeTextOptions = WidgetThemeText.entries.map { DialogOption(stringResource(it.label), it.toString()) }

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
                        bottom = SpaceDefault,
                        end = paddingValues.calculateEndPadding(
                            layoutDirection = layoutDirection,
                        ) + SpaceDefault,
                        start = paddingValues.calculateStartPadding(
                            layoutDirection = layoutDirection,
                        ) + SpaceDefault,
                        top = SpaceDefault,
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

                    WidgetFroggyPreview(
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
                    vertical = SpaceDefault,
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
                    ),
                )

                Spacer(
                    modifier = Modifier.weight(
                        weight = 1.00f,
                    ),
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = buttonSize,
                        )
                        .padding(
                            all = SpaceDefault,
                        ),
                    contentPadding = ButtonDefaults.contentPaddingFor(
                        buttonHeight = buttonSize,
                    ),
                    onClick = {
                        onDone(
                            WidgetConfig(
                                fontSize = selectedFontSize,
                                iconSize = selectedIconSize,
                                widgetTheme = widgetTheme,
                                widgetThemeText = widgetThemeText,
                            )
                        )
                    },
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
fun WidgetFroggyPreview(
    fontSize: Float,
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

    val textVariantColor = when (textTheme) {
        WidgetThemeText.DARK -> Color.Black.copy(
            alpha = 0.60f,
        )

        WidgetThemeText.LIGHT -> Color.White.copy(
            alpha = 0.70f,
        )

        WidgetThemeText.SYSTEM -> when (widgetTheme) {
            WidgetTheme.DARK -> Color.White.copy(
                alpha = 0.70f,
            )

            WidgetTheme.LIGHT -> Color.Black.copy(
                alpha = 0.60f,
            )

            WidgetTheme.TRANSPARENT -> Color.White.copy(
                alpha = 0.70f,
            )

            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

    val widgetColor = when (widgetTheme) {
        WidgetTheme.DARK -> Color.Black
        WidgetTheme.LIGHT -> Color.White
        WidgetTheme.SYSTEM -> MaterialTheme.colorScheme.surfaceContainerHighest
        WidgetTheme.TRANSPARENT -> Color.Transparent
    }

    val conditionFontSize = 18 * fontSize
    val locationFontSize = 14 * fontSize
    val mainIconSize = 42 * iconSize
    val temperatureFontSize = 30 * fontSize

    Column(
        modifier = Modifier
            .background(
                color = widgetColor,
                shape = RoundedCornerShape(
                    size = ShapeRadius.ExtraLarge,
                ),
            )
            .clip(
                shape = RoundedCornerShape(
                    size = ShapeRadius.ExtraLarge,
                ),
            )
            .fillMaxWidth()
            .height(
                height = 180.dp,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    all = 18.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherIconBox(
                icon = R.drawable.il_weather_clear_night,
                size = mainIconSize.dp,
            )

            Gap(
                horizontal = SpaceDefault / 2,
            )

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        color = textColor,
                        fontSize = temperatureFontSize.sp,
                        fontWeight = FontWeight.Medium,
                        text = "13°",
                    )

                    Spacer(
                        modifier = Modifier.weight(
                            weight = 1.00f,
                        ),
                    )

                    Text(
                        color = textColor,
                        fontSize = conditionFontSize.sp,
                        fontWeight = FontWeight.Medium,
                        text = "Clear Sky",
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        color = textVariantColor,
                        fontSize = locationFontSize.sp,
                        fontWeight = FontWeight.Medium,
                        text = "Denver",
                    )

                    Spacer(
                        modifier = Modifier.weight(
                            weight = 1.00f,
                        ),
                    )

                    Text(
                        color = textColor,
                        fontSize = locationFontSize.sp,
                        fontWeight = FontWeight.Medium,
                        text = "H 37° / L 13°",
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.weight(
                weight = 1.00f,
            ),
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(
                        y = 14.dp,
                    ),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                painter = painterResource(R.drawable.im_froggy_clear_night),
            )
        }
    }
}

fun getStringFromFloat(
    float: Float,
): String {
    return "${round(float * 100).toInt()}%"
}
