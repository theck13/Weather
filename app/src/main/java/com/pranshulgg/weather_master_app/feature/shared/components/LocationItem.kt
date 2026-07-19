package com.pranshulgg.weather_master_app.feature.shared.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius

@Composable
fun LocationItem(
    description: String,
    dragHandle: @Composable () -> Unit,
    icon: Int,
    isDefault: Boolean = false,
    isDeviceLocation: Boolean = false,
    isReordering: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    shape: RoundedCornerShape,
    temperature: Int?,
    title: String,
) {
    val animation = tween<Color>(300)
    val colorContainer by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceBright,
        animationSpec = animation,
    )
    val colorContent by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
        animationSpec = animation,
    )
    val colorSupporting by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = animation,
    )
    val shape =
        if (isSelected) {
            RoundedCornerShape(
                size = ShapeRadius.Large,
            )
        } else {
            shape
        }

    Surface(
        modifier = Modifier
            .clip(
                shape = shape,
            )
            .then(
                // While sorting, the row is driven by the drag handle instead of taps.
                if (isReordering.not()) {
                    Modifier.combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick,
                    )
                } else {
                    Modifier
                }
            ),
        color = colorContainer,
        shape = shape,
    ) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = colorContainer,
            ),
            headlineContent = {
                if (isDefault || isDeviceLocation) {
                    LocationTitle(
                        contentColor = colorContent,
                        isDefault = isDefault,
                        isDeviceLocation = isDeviceLocation,
                        title = title,
                    )
                } else {
                    Text(
                        color = colorContent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = title,
                    )
                }
            },
            leadingContent = {
                Box(
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = CircleShape
                        )
                        .size(
                            size = 52.dp,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    AnimatedContent(
                        label = "Location Icon",
                        targetState = icon,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                        },
                    ) { ic ->
                        WeatherIconBox(
                            icon = ic,
                            size = 34.dp,
                        )
                    }
                }
            },
            supportingContent = {
                AnimatedContent(
                    label = "Location Description",
                    targetState = description,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    },
                ) { desc ->
                    Text(
                        color = colorSupporting,
                        text = desc,
                    )
                }
            },
            trailingContent = {
                AnimatedContent(
                    label = "Location Trailing",
                    targetState = isReordering,
                    transitionSpec = {
                        // The drag handle just fades; the temperature slides in from and
                        // out to the right while fading.
                        val enter =
                            if (targetState) {
                                fadeIn(tween(300))
                            } else {
                                fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it }
                            }
                        val exit =
                            if (initialState) {
                                fadeOut(tween(300))
                            } else {
                                fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it }
                            }
                        enter togetherWith exit
                    },
                ) { reordering ->
                    if (reordering) {
                        dragHandle()
                    } else {
                        AnimatedContent(
                            label = "Location Temperature",
                            targetState = temperature,
                            transitionSpec = {
                                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                            },
                        ) { temperature ->
                            Text(
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.displaySmall,
                                text = "${temperature ?: "-"}°",
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun LocationTitle(
    contentColor: Color,
    isDefault: Boolean,
    isDeviceLocation: Boolean,
    title: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            space = 4.dp,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = title,
        )

        if (isDeviceLocation && isDefault.not()) {
            Symbol(
                color = contentColor,
                icon = R.drawable.ic_location_on_24,
                size = 18.dp,
            )
        }

        if (isDefault) {
            Symbol(
                color = contentColor,
                icon = R.drawable.ic_home_pin_24,
                size = 18.dp,
            )
        }
    }
}
