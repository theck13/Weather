package com.heckofanapp.weather.feature.main.ui.weatheranimations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.R
import kotlin.math.PI
import kotlin.math.sin

private data class Star(
    val x: Float,
    val y: Float,
    val alpha: Float,
    val radius: Float,
    val phase: Float,
)

@Composable
fun StarsCanvas(
    starCount: Int = 200,
    showClouds: Boolean = false,
) {
    val cloud1 = ImageBitmap.imageResource(R.drawable.im_animation_cloudy_1)
    val cloud2 = ImageBitmap.imageResource(R.drawable.im_animation_cloudy_3)
    val infiniteTransition = rememberInfiniteTransition()

    val cloudAnimation by infiniteTransition.animateFloat(
        initialValue = 0.00f,
        targetValue = 1.00f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        )
    )
    val stars = remember {
        List(starCount) {
            Star(
                alpha = (3..9).random() / 10.00f,
                phase = (0..1000).random() / 1000.00f,
                radius = (5..25).random() / 10.00f,
                x = (0..1000).random().toFloat(),
                y = (0..1000).random().toFloat(),
            )
        }
    }
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        )
    )

    val cloud1X = cloudAnimation * 80.00f
    val cloud2X = -((cloudAnimation * 80.00f) + 50.00f)

    Canvas(
        Modifier
            .fillMaxWidth()
            .height(
                height = 290.dp,
            ),
    ) {
        stars.forEach {
            val animatedAlpha =
                it.alpha * (0.50f + 0.50f * sin((twinkle + it.phase) * 2 * PI).toFloat())

            drawCircle(
                center = Offset(it.x / 1000.00f * size.width, it.y / 1000.00f * size.height),
                color = Color.White.copy(
                    alpha = animatedAlpha.coerceIn(0.00f, 1.00f),
                ),
                radius = it.radius,
            )
        }

        // ---- CLOUD 1
        if (showClouds) {
            translate(
                left = cloud1X,
                top = -60.00f,
            ) {
                scale(
                    scale = 0.50f,
                ) {
                    drawImage(
                        image = cloud1,
                        srcOffset = IntOffset.Zero,
                        alpha = 0.80f,
                        srcSize = IntSize(
                            height = cloud1.height,
                            width = cloud1.width,
                        ),
                    )
                }
            }

            // ---- CLOUD 2
            translate(
                left = cloud2X + -(cloud2.width / 6.00f),
                top = cloud2.height / 4.00f,
            ) {
                scale(
                    scale = 0.40f,
                ) {
                    drawImage(
                        image = cloud2,
                        srcOffset = IntOffset.Zero,
                        srcSize = IntSize(
                            height = cloud2.height,
                            width = cloud2.width,
                        ),
                    )
                }
            }
        }
    }
}
