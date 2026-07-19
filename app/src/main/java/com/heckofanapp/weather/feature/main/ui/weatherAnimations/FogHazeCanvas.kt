package com.heckofanapp.weather.feature.main.ui.weatheranimations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.core.ui.theme.isThemeDark

@Composable
fun FogHazeCanvas(
    isFroggyLayout: Boolean,
) {
    val infiniteTransition = rememberInfiniteTransition(
        label = "Infinite Transition",
    )
    val isDark = isThemeDark()
    val itemWidth = 300f
    val windowInfo = LocalWindowInfo.current

    val screenWidthPx = windowInfo.containerSize.width.toFloat()
    val speedPxPerSecond = 20f

    val distance = screenWidthPx + itemWidth * 2
    val durationMillis = ((distance / speedPxPerSecond) * 1000).toInt()

    val offsetX1 by infiniteTransition.animateFloat(
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 6000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        initialValue = -200f,
        targetValue = 200f,
        label = "Offset 1",
    )

    val offsetX2 by infiniteTransition.animateFloat(
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 8000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        initialValue = 200f,
        targetValue = -150f,
        label = "Offset 2",
    )

    val offsetX3 by infiniteTransition.animateFloat(
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        initialValue = screenWidthPx,
        targetValue = -screenWidthPx,
        label = "Offset 3",
    )

    val modifier = Modifier
        .blur(
            radius = 30.dp,
        )
        .fillMaxWidth()

    Canvas(
        modifier =
            if (!isFroggyLayout) {
                modifier.fillMaxHeight()
            } else {
                modifier.height(
                    height = 290.dp,
                )
            },
    ) {
        val fog1 = Brush.radialGradient(
            center = Offset(
                x = size.width * 0.30f + offsetX1,
                y = size.height
            ),
            colors = listOf(
                Color.Gray.copy(
                    alpha = if (isDark) 0.30f else 0.70f,
                ),
                Color.Transparent
            ),
            radius = size.width * 0.70f
        )

        val fog2 = Brush.radialGradient(
            center = Offset(
                x = size.width * 0.70f + offsetX2,
                y = size.height / 5.00f
            ),
            colors = listOf(
                Color.White.copy(
                    alpha = if (isDark) 0.20f else 0.70f,
                ),
                Color.Transparent
            ),
            radius = size.width * 0.80f,
        )

        val fog3 = Brush.radialGradient(
            center = Offset(
                x = size.width * 0.70f + offsetX3,
                y = size.height / 5.00f,
            ),
            colors = listOf(
                if (isDark) {
                    Color.Black.copy(
                        alpha = 1.00f,
                    )
                } else {
                    Color.White.copy(
                        alpha = 1.00f,
                    )
                },
                Color.Transparent
            ),
            radius = 300f,
        )

        drawRect(
            brush = fog1,
            size = size,
        )

        drawRect(
            brush = fog2,
            size = size,
        )

        drawRect(
            brush = fog3,
            size = size,
        )
    }
}
