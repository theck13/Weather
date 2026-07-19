package com.heckofanapp.weather.widgets.hourly.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

@Composable
fun WidgetHourlyItem(
    fontSize: Float,
    icon: Int,
    iconSize: Float,
    temp: String,
    time: String,
) {
    Column(
        modifier = GlanceModifier.padding(
            horizontal = 5.dp,
            vertical = 5.dp,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Medium
            ),
            text = temp,
        )

        Spacer(
            modifier = GlanceModifier.height(
                height = 3.dp,
            ),
        )

        Image(
            modifier = GlanceModifier.size(
                size = iconSize.dp,
            ),
            contentDescription = "",
            provider = ImageProvider(icon),
        )

        Spacer(
            modifier = GlanceModifier.height(
                height = 3.dp,
            ),
        )

        Text(
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Medium,
            ),
            text = time,
        )
    }
}
