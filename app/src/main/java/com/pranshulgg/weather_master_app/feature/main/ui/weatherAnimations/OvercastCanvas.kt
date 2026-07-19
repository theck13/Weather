package com.pranshulgg.weather_master_app.feature.main.ui.weatheranimations

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R

@Preview
@Composable
fun OvercastCanvas() {
    val cloud1 = ImageBitmap.imageResource(R.drawable.im_animation_cloudy_1)
    val cloud2 = ImageBitmap.imageResource(R.drawable.im_animation_cloudy_2)
    val cloud3 = ImageBitmap.imageResource(R.drawable.im_animation_cloudy_9)
    val cloud4 = ImageBitmap.imageResource(R.drawable.im_animation_cloudy_6)
    val cloud5 = ImageBitmap.imageResource(R.drawable.im_animation_cloudy_8)

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

    val cloud1X = cloudAnimation * 80.00f
    val cloud2X = -((cloudAnimation * 80.00f) + 50.00f)
    val cloud3X = -((cloudAnimation * 40.00f) + 30.00f)
    val cloud4X = cloudAnimation * 40.00f + 100.00f
    val cloud5X = cloudAnimation * 100.00f + 100.00f

    Canvas(
        Modifier
            .fillMaxWidth()
            .height(
                height = 290.dp,
            ),
    ) {
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

        translate(
            left = cloud4X + (cloud4.width / 6.00f),
            top = 90.00f,
        ) {
            scale(
                scale = 0.50f,
            ) {
                drawImage(
                    image = cloud4,
                    srcOffset = IntOffset.Zero,
                    alpha = 0.50f,
                    srcSize = IntSize(
                        height = cloud4.height,
                        width = cloud4.width,
                    ),
                )
            }
        }

        translate(
            left = cloud2X + -(cloud2.width / 6.00f),
            top = cloud2.height / 1.80f,
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
        translate(
            left = cloud3X + -(cloud3.width / 3.00f),
            top = -60.00f,
        ) {
            scale(
                scale = 0.40f,
            ) {
                drawImage(
                    image = cloud3,
                    srcOffset = IntOffset.Zero,
                    alpha = 0.60f,
                    srcSize = IntSize(
                        height = cloud3.height,
                        width = cloud3.width,
                    ),
                )
            }
        }

        translate(
            left = cloud5X + (cloud5.width / 6.00f),
            top = cloud5.height / 1.30f,
        ) {
            scale(
                scale = 0.50f,
            ) {
                drawImage(
                    image = cloud5,
                    srcOffset = IntOffset.Zero,
                    alpha = 0.50f,
                    srcSize = IntSize(
                        height = cloud5.height,
                        width = cloud5.width,
                    ),
                )
            }
        }
    }
}
