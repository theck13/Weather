package com.pranshulgg.weather_master_app.widgets.card.ui.variants

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.TouchMinimum
import com.pranshulgg.weather_master_app.widgets.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme

@Composable
fun WidgetCardSmall(
    modifier: GlanceModifier = GlanceModifier,
    config: WidgetConfig,
    state: WidgetWeather?,
) {
    val textColor =
        if (config.widgetTheme == WidgetTheme.TRANSPARENT) {
            ColorProvider(R.color.white)
        } else {
            GlanceTheme.colors.onSurface
        }
    val tempSize = 40 * config.fontSize

    if (state != null) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = GlanceModifier.size(
                    size = TouchMinimum,
                ),
                contentDescription = null,
                provider = ImageProvider(state.currentIcon),
            )

            Spacer(
                modifier = GlanceModifier.width(
                    width = 12.dp,
                ),
            )

            Text(
                style = TextStyle(
                    color = textColor,
                    fontSize = tempSize.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                ),
                text = state.currentTemp,
            )
        }
    }
}
