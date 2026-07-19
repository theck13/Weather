package com.heckofanapp.weather.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Symbol(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    description: String? = null,
    icon: Int,
    paddingStart: Dp = 0.dp,
    size: Dp = 24.dp,
) {
    Box(
        modifier = Modifier.padding(
            start = paddingStart,
        ),
    ) {
        Icon(
            modifier = modifier.size(
                size = size,
            ),
            contentDescription = description,
            painter = painterResource(icon),
            tint = color,
        )
    }
}
