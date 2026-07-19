package com.pranshulgg.weather_master_app.feature.main.ui.weatheranimations

import androidx.compose.animation.core.Animatable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.theme.Yellow600
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SunCanvas(
    isFroggyLayout: Boolean,
    showClouds: Boolean = false,
) {
    val infiniteTransition = rememberInfiniteTransition()

    val alphaRays = remember {
        List(3) {
            Animatable(0.00f)
        }
    }
    val alphaSun by infiniteTransition.animateFloat(
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        initialValue = 0.20f,
        targetValue = 0.50f,
    )

    val cloudAnimation by infiniteTransition.animateFloat(
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse
        ),
        initialValue = 0.00f,
        targetValue = 1.00f,
    )
    val cloud1 = ImageBitmap.imageResource(R.drawable.im_animation_cloudy_1)
    val cloud2 = ImageBitmap.imageResource(R.drawable.im_animation_cloudy_3)
    val cloud1X = cloudAnimation * 80.00f
    val cloud2X = -((cloudAnimation * 80.00f) + 50.00f)

    alphaRays.forEachIndexed { index, animatable ->
        LaunchedEffect(index) {
            delay((0..2000).random().toLong().milliseconds)

            while (true) {
                animatable.animateTo(
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = LinearEasing,
                    ),
                    targetValue = 0.20f,
                )

                delay((2000..3000).random().toLong().milliseconds)

                animatable.animateTo(
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = LinearEasing,
                    ),
                    targetValue = 0.10f,
                )

                delay((800..1200).random().toLong().milliseconds)

                animatable.animateTo(
                    targetValue = 0.2f,
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = LinearEasing,
                    )
                )

                delay((1000..3000).random().toLong().milliseconds)

                animatable.animateTo(
                    animationSpec = tween(
                        durationMillis = 2000,
                        easing = LinearEasing,
                    ),
                    targetValue = 0.00f,
                )

                delay((3000..4000).random().toLong().milliseconds)
            }
        }
    }

    Canvas(
        Modifier
            .fillMaxWidth()
            .height(
                height = 290.dp,
            ),
    ) {
        val circleCenter = Offset(
            x = size.width / 1.20f,
            y = size.height / 3.10f,
        )

        val brushSun = Brush.radialGradient(
            colors = listOf(
                Yellow600.copy(
                    alpha = alphaSun,
                ),
                Color.Transparent
            ),
            center = circleCenter,
            radius = if (isFroggyLayout) 200.00f else 400.00f,
        )

        drawCircle(
            brush = brushSun,
            center = circleCenter,
            radius = if (isFroggyLayout) 200.00f else 400.00f,
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Yellow.copy(
                        alpha = alphaRays[2].value,
                    ),
                    Color.Transparent,
                ),
                center = Offset(
                    x = size.width / 1.20f,
                    y = size.height / 1.70f,
                ),
                radius = if (isFroggyLayout) 70.00f else 200.00f
            ),
            center = Offset(
                x = size.width / 1.30f,
                y = size.height / 1.80f,
            ),
            radius = if (isFroggyLayout) 70.00f else 200.00f,
        )

        if (showClouds.not()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(
                            alpha = alphaRays[0].value
                        ),
                        Color.Transparent,
                    ),
                    center = Offset(
                        x = size.width / 2.24f,
                        y = size.height / 1.35f,
                    ),
                    radius = 70f
                ),
                center = Offset(
                    x = size.width / 2.50f,
                    y = size.height / 1.50f,
                ),
                radius = 70.00f,
            )

            rotate(20.00f) {
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(
                                alpha = alphaRays[1].value,
                            ),
                            Color.Transparent,
                        ),
                        center = Offset(
                            x = size.width / 1.64f,
                            y = size.height / 2.00f
                        ),
                        radius = 50.00f
                    ),
                    cornerRadius = CornerRadius(
                        x = if (isFroggyLayout) 20.00f else 50.00f,
                    ),
                    size = Size(50.00f, 50.00f), topLeft = Offset(
                        x = size.width / 1.80f,
                        y = size.height / 3.00f,
                    ),
                )
            }
        }

        rotate(
            degrees = 40.00f,
            pivot = circleCenter,
        ) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(
                            alpha = alphaRays[0].value
                        ),
                        Color.Transparent,
                    ),
                    endY = if (isFroggyLayout) 600.00f else 1000.00f,
                    startY = 300.00f,
                ),
                size = Size(
                    height = if (isFroggyLayout) 500.00f else 1000.00f,
                    width = if (isFroggyLayout) 20.00f else 40.00f
                ),
                topLeft = circleCenter / 1.04f,
            )
        }

        rotate(
            degrees = 10.00f,
            pivot = circleCenter,
        ) {
            drawRect(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(
                            alpha = alphaRays[1].value
                        ),
                        Color.Transparent,
                    ),
                    endY = if (isFroggyLayout) 600.00f else 1000.00f,
                    startY = 300.00f,
                ),
                size = Size(
                    height = if (isFroggyLayout) 500.00f else 1000.00f,
                    width = 20.00f,
                ),
                topLeft = circleCenter,
            )
        }

        rotate(
            degrees = 70.00f,
            pivot = circleCenter,
        ) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(
                            alpha = alphaRays[2].value,
                        ),
                        Color.Transparent,
                    ),
                    endY = if (isFroggyLayout) 600.00f else 1000.00f,
                    startY = 300.00f,
                ),
                size = Size(
                    height = if (isFroggyLayout) 500.00f else 1000.00f,
                    width = 30.00f
                ),
                topLeft = circleCenter / 1.07f,
            )
        }

        if (showClouds) {
            translate(
                left = cloud1X,
            ) {
                scale(
                    scale = 0.50f,
                ) {
                    drawImage(
                        alpha = 0.80f,
                        image = cloud1,
                        srcOffset = IntOffset.Zero,
                        srcSize = IntSize(
                            height = cloud1.height,
                            width = cloud1.width,
                        ),
                    )
                }
            }

            translate(
                left = cloud2X + -(cloud2.width / 6.00f),
                top = cloud2.height / 2.50f,
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
