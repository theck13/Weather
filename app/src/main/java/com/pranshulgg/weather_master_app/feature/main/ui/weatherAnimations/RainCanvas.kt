package com.pranshulgg.weather_master_app.feature.main.ui.weatheranimations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.core.ui.theme.isThemeDark
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class Drop(
    var x: Float,
    var y: MutableState<Float>,
    var speed: MutableState<Float>,
)

@Composable
fun RainCanvas(
    isStorming: Boolean = false,
    rainDropCount: Int = 80,
    isFroggyLayout: Boolean = true,
) {
    val drops = remember {
        List(rainDropCount) {
            Drop(
                x = (0..1000).random().toFloat(),
                y = mutableFloatStateOf((0..2000).random().toFloat()),
                speed = mutableFloatStateOf((10..50).random().toFloat())
            )

        }.toMutableStateList()
    }
    val isDark = isThemeDark()

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var lightningAlpha by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        if (canvasSize == Size.Zero) return@LaunchedEffect

        val gravity = 0.50f

        while (true) {
            withFrameNanos {
                drops.forEach {
                    it.speed.value += gravity
                    it.y.value += it.speed.value

                    it.x += -1.00f + -Random.nextFloat() * -2.00f

                    if (it.y.value > canvasSize.height) {
                        it.y.value = 0f
                        it.x = Random.nextFloat() * canvasSize.width
                        it.speed.value = (10..30).random().toFloat()
                    }
                }
            }
        }
    }

    if (isStorming) {
        LaunchedEffect(Unit) {
            while (true) {
                delay((1000..10000).random().toLong())
                lightningAlpha = 124
                delay((50..100).random().toLong())

                lightningAlpha = 0
                delay((200..300).random().toLong())

                lightningAlpha = 100
                delay((50..100).random().toLong())

                lightningAlpha = 0
            }
        }
    }

    Canvas(
        modifier =
            if (isFroggyLayout) {
                Modifier
                    .fillMaxWidth()
                    .height(
                        height = 290.dp,
                    )
            } else {
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            }
    ) {
        canvasSize = size

        drops.forEach {
            drawLine(
                brush = Brush.verticalGradient(
                    listOf(
                        if (isDark) {
                            Color.Gray.copy(
                                alpha = 0.50f,
                            )
                        } else {
                            Color.White.copy(
                                alpha = 0.50f,
                            )
                        },
                        Color.White.copy(
                            alpha = if (isDark) 0.60f else 1.00f,
                        )
                    )
                ),
                cap = StrokeCap.Round,
                end = Offset(it.x, it.y.value + 20.00f),
                start = Offset(it.x, it.y.value),
                strokeWidth = 5.00f,
            )
        }

        if (isStorming) {
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(
                            alpha = lightningAlpha,
                            blue = 255,
                            green = 191,
                            red = 212,
                        ),
                        Color.Transparent
                    )
                ),
                size = size,
            )
        }
    }
}
