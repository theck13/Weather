package com.heckofanapp.weather.widgets.card.ui.variants

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
import com.heckofanapp.weather.R
import com.heckofanapp.weather.TouchMinimum
import com.heckofanapp.weather.widgets.WidgetConfig
import com.heckofanapp.weather.widgets.model.WidgetWeather
import com.heckofanapp.weather.widgets.ui.colors.WidgetTheme

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
