package com.heckofanapp.weather.core.ui.components.tiles

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.heckofanapp.weather.R
import kotlin.time.Duration.Companion.milliseconds

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun DialogSliderTile(
    description: String? = null,
    dialogTitle: String,
    headline: String,
    initialValue: Float = 0.5f,
    isDescriptionAsValue: Boolean = false,
    itemBgColor: Color,
    labelFormatter: (Float) -> String = { it.toString() },
    leading: @Composable (() -> Unit)? = null,
    onValueChange: (Float) -> Unit = {},
    onValueSubmitted: (Float) -> Unit,
    shapes: RoundedCornerShape,
    steps: Int = 0,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    var showDialog by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(initialValue) }
    var valueBeforeEdit by remember { mutableFloatStateOf(initialValue) }

    fun revert() {
        sliderValue = valueBeforeEdit
        onValueChange(valueBeforeEdit)
        showDialog = false
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes,
    ) {
        ListItem(
            modifier = Modifier.clickable {
                valueBeforeEdit = sliderValue
                showDialog = true
            },
            colors = ListItemDefaults.colors(
                containerColor = itemBgColor
            ),
            leadingContent = leading,
            headlineContent = {
                Text(
                    text = headline,
                )
            },
            supportingContent = {
                if (description != null) {
                    Text(
                        color = if (isDescriptionAsValue) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                        text = description,
                    )
                } else {
                    Text(
                        color = MaterialTheme.colorScheme.tertiary,
                        text = labelFormatter(sliderValue),
                    )
                }
            },
        )
    }

    if (showDialog) {
        AlertDialog(
            confirmButton = {
                TextButton(
                    onClick = {
                        onValueSubmitted(sliderValue)
                        showDialog = false
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = stringResource(R.string.action_save),
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        revert()
                    },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = stringResource(R.string.action_cancel),
                    )
                }
            },
            onDismissRequest = {
                revert()
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 24.dp,
                        ),
                ) {
                    LabeledSlider(
                        labelFormatter = labelFormatter,
                        onValueChange = { value ->
                            sliderValue = value
                            onValueChange(value)
                        },
                        steps = steps,
                        value = sliderValue,
                        valueRange = valueRange,
                    )
                }
            },
            title = {
                Text(
                    text = dialogTitle,
                )
            },
        )
    }
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun LabeledSlider(
    labelFormatter: (Float) -> String = { it.toString() },
    onValueChange: (Float) -> Unit,
    steps: Int,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        var sliderWidth by remember { mutableIntStateOf(0) }
        var showLabel by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged {
                    sliderWidth = it.width
                },
        ) {
            val interactionSource = remember { MutableInteractionSource() }

            Slider(
                modifier = Modifier.fillMaxWidth(),
                interactionSource = interactionSource,
                onValueChange = {
                    onValueChange(it)
                    showLabel = true
                },
                steps = steps,
                value = value,
                valueRange = valueRange,
            )

            val fraction = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
            val thumbOffsetPx = (fraction * sliderWidth).coerceIn(
                maximumValue = sliderWidth.toFloat(),
                minimumValue = 0.00f,
            )

            val scale by animateFloatAsState(
                targetValue = if (showLabel) 1.00f else 0.80f,
            )
            val alpha by animateFloatAsState(
                targetValue = if (showLabel) 1.00f else 0.00f,
            )

            LaunchedEffect(value) {
                showLabel = true
                kotlinx.coroutines.delay(
                    duration = 1000.milliseconds,
                )
                showLabel = false
            }

            Popup(
                offset = IntOffset(
                    x = (thumbOffsetPx - 48).toInt(),
                    y = -90,
                )
            ) {
                Surface(
                    modifier = Modifier.graphicsLayer {
                        this.scaleX = scale
                        this.scaleY = scale
                        this.alpha = alpha
                    },
                    color = MaterialTheme.colorScheme.inverseSurface,
                    shadowElevation = 2.dp,
                    shape = RoundedCornerShape(
                        size = 50.dp,
                    ),
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 10.dp,
                        ),
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        fontSize = 14.sp,
                        text = labelFormatter(value),
                    )
                }
            }
        }
    }
}
