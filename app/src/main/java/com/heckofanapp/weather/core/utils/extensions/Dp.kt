package com.heckofanapp.weather.core.utils.extensions

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

operator fun Dp.times(
    multiplier: Number,
): Dp {
    return (this.value * multiplier.toFloat()).dp
}
