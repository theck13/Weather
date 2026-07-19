package com.pranshulgg.weather_master_app.widgets.hourly.ui.variants

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.widgets.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton

@Composable
fun WidgetHourlySmall(
    config: WidgetConfig,
    state: WidgetWeather?,
) {
    val fontSize = 54 * config.fontSize
    val iconSize = 58 * config.iconSize

    if (state != null) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = GlanceModifier.size(
                    size = iconSize.dp,
                ),
                contentDescription = null,
                provider = ImageProvider(state.currentIcon),
            )

            Text(
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold,
                ),
                text = state.currentTemp,
            )
        }
    } else {
        ReloadButton()
    }
}
