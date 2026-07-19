package com.heckofanapp.weather.feature.daily

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.model.weather.toIconPair
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.WeatherIconBox

@Composable
fun SelectableDayItem(
    conditions: WeatherCondition,
    isSelected: Boolean,
    motionScheme: MotionScheme,
    onSelect: () -> Unit,
    temperatureMaximum: String,
    temperatureMinimum: String,
    weekday: String,
) {
    val animatedShape by animateDpAsState(
        animationSpec = motionScheme.defaultSpatialSpec(),
        label = "Shape",
        targetValue = if (isSelected) 24.dp else 50.dp,
    )

    val animatedColor by animateColorAsState(
        animationSpec = motionScheme.defaultEffectsSpec(),
        label = "Color",
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
    )

    val selectedOnSurface = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val selectedOnSurfaceVariant = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(0.80f) else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = animatedColor,
        onClick = onSelect,
        shape = RoundedCornerShape(
            size = animatedShape,
        ),
    ) {
        Column(
            Modifier
                .padding(
                    vertical = 24.dp,
                )
                .width(
                    64.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    color = selectedOnSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    text = "${temperatureMaximum}°",
                )
                Text(
                    color = selectedOnSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    text = "${temperatureMinimum}°",
                )
            }

            Gap(
                vertical = 8.dp,
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WeatherIconBox(
                    icons = conditions.toIconPair(
                        targetTimeMilli = System.currentTimeMillis(),
                    ),
                    size = 34.dp,
                )

                Gap(
                    vertical = 8.dp,
                )

                Text(
                    color = selectedOnSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    text = weekday,
                )
            }
        }
    }
}
