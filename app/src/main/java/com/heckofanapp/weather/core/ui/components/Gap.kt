package com.heckofanapp.weather.core.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Gap(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp,
) {
    Spacer(
        Modifier
            .height(
                height = vertical,
            )
            .width(
                width = horizontal,
            ),
    )
}
