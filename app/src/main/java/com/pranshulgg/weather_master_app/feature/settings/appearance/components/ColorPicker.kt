package com.pranshulgg.weather_master_app.feature.settings.appearance.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.pranshulgg.weather_master_app.TouchMinimum
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.theme.ThemeVariant

private val scheme = listOf(
    "#f44336",
    "#ff5252",
    "#e91e63",
    "#ff4081",
    "#9c27b0",
    "#e040fb",
    "#673ab7",
    "#7c4dff",
    "#3f51b5",
    "#536dfe",
    "#2196f3",
    "#448aff",
    "#03a9f4",
    "#40c4ff",
    "#00bcd4",
    "#18ffff",
    "#009688",
    "#64ffda",
    "#4caf50",
    "#69f0ae",
    "#8bc34a",
    "#b2ff59",
    "#cddc39",
    "#eeff41",
    "#ffeb3b",
    "#ffff00",
    "#ffc107",
    "#ffd740",
    "#ff9800",
    "#ffab40",
    "#ff5722",
    "#ff6e40",
    "#795548",
    "#607d8b",
    "#9e9e9e",
    "#f8f8f8",
);

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun ColorPicker(
    onThemeColorChanged: (String) -> Unit,
) {
    val preferences = LocalAppPrefs.current

    val currentMotionScheme = motionScheme
    val options = listOf(
        "Expressive" to ThemeVariant.EXPRESSIVE,
        "Neutral" to ThemeVariant.NEUTRAL,
        "Tonal" to ThemeVariant.TONAL,
        "Vibrant" to ThemeVariant.VIBRANT,
    )
    val motionScheme = remember(currentMotionScheme) { currentMotionScheme }

    var currentSelectedColor by remember { mutableStateOf(preferences.customThemeColor) }
    var selected by remember { mutableStateOf(preferences.themeVariant) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                all = 16.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FlowRow(
            maxItemsInEachRow = 6,
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
            ),
        ) {
            scheme.forEach { hex ->
                val isSelected = currentSelectedColor == hex

                val alpha by animateFloatAsState(
                    label = "Alpha",
                    targetValue = if (isSelected) 1.00f else 0.85f,
                )
                val scale by animateFloatAsState(
                    animationSpec = motionScheme.defaultSpatialSpec(),
                    label = "Scale",
                    targetValue = if (isSelected) 1.15f else 1.00f,
                )

                Surface(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                        ) {
                            currentSelectedColor = hex
                            onThemeColorChanged(hex)
                        }
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                        .size(
                            size = TouchMinimum,
                        ),
                    color = Color(
                        color = hex.toColorInt(),
                    ),
                    shape =
                        if (isSelected) {
                            MaterialShapes.Cookie4Sided.toShape()
                        } else {
                            CircleShape
                        }
                ) {}
            }
        }

        Box(
            modifier = Modifier.height(
                height = 16.dp,
            ),
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            options.forEach { (label, variant) ->
                ToggleButton(
                    modifier = Modifier.semantics {
                        role = Role.RadioButton
                    },
                    checked = selected == variant,
                    colors = ToggleButtonDefaults.toggleButtonColors(
                        checkedContainerColor = MaterialTheme.colorScheme.tertiary,
                        checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                    onCheckedChange = {
                        preferences.setThemeVariantType(variant)
                        selected = variant
                    },
                    shapes = ToggleButtonDefaults.shapes(),
                ) {
                    Text(
                        text = label,
                    )
                }
            }
        }
    }
}
