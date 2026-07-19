package com.heckofanapp.weather.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WeatherIconBox(
    modifier: Modifier = Modifier,
    icon: Int,
    size: Dp = 40.dp,
) {
    Box(
        modifier = modifier.size(
            size = size,
        ),
    ) {
        Image(
            modifier = Modifier.matchParentSize(),
            contentDescription = "",
            painter = painterResource(icon),
        )
    }
}

@Composable
fun WeatherIconBox(
    modifier: Modifier = Modifier,
    icons: Pair<Int, Int?>,
    size: Dp = 40.dp,
) {
    val (primary, secondary) = icons

    if (secondary == null) {
        WeatherIconBox(
            modifier = modifier,
            icon = primary,
            size = size,
        )

        return
    }

    val subSize = size * 0.75f

    Box(
        modifier = modifier.size(
            size = size,
        ),
    ) {
        Image(
            modifier = Modifier
                .align(
                    alignment = Alignment.BottomEnd,
                )
                .size(
                    size = subSize,
                ),
            contentDescription = "",
            painter = painterResource(secondary),
        )

        Image(
            modifier = Modifier
                .align(
                    alignment = Alignment.TopStart,
                )
                .size(
                    size = subSize,
                ),
            contentDescription = "",
            painter = painterResource(primary),
        )
    }
}
