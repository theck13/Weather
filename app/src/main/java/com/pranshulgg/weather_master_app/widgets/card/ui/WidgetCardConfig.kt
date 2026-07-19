package com.pranshulgg.weather_master_app.widgets.card.ui

import androidx.compose.foundation.background
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
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.TouchMinimum
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.widgets.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetVariant
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import kotlin.math.round

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun WidgetCardConfig(
    onDone: (WidgetConfig) -> Unit = {},
) {
    val buttonSize = ButtonDefaults.MediumContainerHeight
    val layoutDirection = LocalLayoutDirection.current

    var selectedFontSize by remember { mutableFloatStateOf(1.00f) }
    var selectedVariant by remember { mutableStateOf(WidgetVariant.LARGE) }
    var showBackground by remember { mutableStateOf(true) }

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
                        end = paddingValues.calculateEndPadding(layoutDirection = layoutDirection) + 16.dp,
                        start = paddingValues.calculateStartPadding(layoutDirection = layoutDirection) + 16.dp,
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

                    WidgetCardPreview(
                        fontSize = selectedFontSize,
                        showBackground = showBackground,
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
                        SettingTile.SwitchTile(
                            checked = showBackground,
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_texture_24,
                                )
                            },
                            onCheckedChange = {
                                showBackground = it
                            },
                            title = "Show Background",
                        ),
                        SettingTile.DialogSliderTile(
                            description = getStringFromFloat(
                                float = selectedFontSize,
                            ),
                            dialogTitle = "Font Size",
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
                            title = "Font Size",
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
                                variant = selectedVariant,
                                widgetTheme = if (showBackground) WidgetTheme.SYSTEM else WidgetTheme.TRANSPARENT,
                            )
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
fun WidgetCardPreview(
    fontSize: Float,
    showBackground: Boolean,
    variant: WidgetVariant,
) {
    when (variant) {
        WidgetVariant.LARGE -> {
            WidgetCardPreviewLarge(
                fontSize = fontSize,
                showBackground = showBackground,
            )
        }

        WidgetVariant.MEDIUM -> {
            WidgetCardPreviewMedium(
                fontSize = fontSize,
                showBackground = showBackground,
            )
        }

        WidgetVariant.SMALL -> {
            WidgetCardPreviewSmall(
                fontSize = fontSize,
                showBackground = showBackground,
            )
        }
    }
}

@Composable
fun WidgetCardPreviewMedium(
    fontSize: Float,
    showBackground: Boolean,
) {
    val size = 18 * fontSize
    val tempSize = 24 * fontSize
    val textColor = if (showBackground) MaterialTheme.colorScheme.onSurface else Color.White
    val widgetColor = if (showBackground) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent

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
                height = 90.dp,
            ),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            Modifier.padding(
                all = 18.dp,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherIconBox(
                icon = R.drawable.il_weather_clear_day,
                size = TouchMinimum,
            )

            Gap(
                horizontal = 6.dp,
            )

            Column {
                Text(
                    fontSize = tempSize.sp,
                    color = textColor,
                    text = "37°",
                )

                Text(
                    fontSize = size.sp,
                    color = textColor,
                    text = "Clear Sky",
                )
            }
        }
    }
}

@Composable
fun WidgetCardPreviewLarge(
    fontSize: Float,
    showBackground: Boolean,
) {
    val fontSizeLocation = 16 * fontSize
    val size = 18 * fontSize
    val tempSize = 40 * fontSize
    val textColor = if (showBackground) MaterialTheme.colorScheme.onSurface else Color.White
    val widgetColor = if (showBackground) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent

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
                height = 90.dp,
            ),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            Modifier.padding(
                all = 18.dp,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherIconBox(
                icon = R.drawable.il_weather_clear_day,
                size = TouchMinimum,
            )

            Gap(
                horizontal = 6.dp,
            )

            Column {
                Text(
                    color = textColor.copy(
                        alpha = 0.80f,
                    ),
                    fontSize = fontSizeLocation.sp,
                    text = "Denver",
                )

                Text(
                    color = textColor,
                    fontSize = size.sp,
                    text = "Clear Sky",
                )
            }

            Spacer(
                modifier = Modifier.weight(
                    weight = 1.00f,
                ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    color = textColor,
                    fontSize = tempSize.sp,
                    fontWeight = FontWeight.Bold,
                    text = "37°",
                )

                Gap(
                    horizontal = 6.dp,
                )

                Column {
                    Text(
                        color = textColor,
                        fontSize = size.sp,
                        fontWeight = FontWeight.Medium,
                        text = "37°",
                    )

                    Gap(
                        horizontal = 6.dp,
                    )

                    Text(
                        color = textColor.copy(
                            alpha = 0.80f,
                        ),
                        fontSize = size.sp,
                        fontWeight = FontWeight.Medium,
                        text = "13°",
                    )
                }
            }
        }
    }
}

@Composable
fun WidgetCardPreviewSmall(
    fontSize: Float,
    showBackground: Boolean,
) {
    val tempSize = 40 * fontSize
    val textColor = if (showBackground) MaterialTheme.colorScheme.onSurface else Color.White
    val widgetColor = if (showBackground) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent

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
                height = 90.dp,
            ),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            Modifier.padding(
                all = 18.dp,
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherIconBox(
                icon = R.drawable.il_weather_clear_day,
                size = TouchMinimum,
            )

            Gap(
                horizontal = 6.dp,
            )

            Text(
                color = textColor,
                fontSize = tempSize.sp,
                text = "37°",
            )
        }
    }
}

fun getStringFromFloat(
    float: Float,
): String {
    return "${round(float * 100).toInt()}%"
}
