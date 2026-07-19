package com.heckofanapp.weather.widgets.card.ui.variants

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
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
fun WidgetCardMedium(
    modifier: GlanceModifier = GlanceModifier,
    config: WidgetConfig,
    state: WidgetWeather?,
) {
    val size = 18 * config.fontSize
    val sizeTemporary = 24 * config.fontSize

    val textColor =
        if (config.widgetTheme == WidgetTheme.TRANSPARENT) {
            ColorProvider(R.color.white,)
        } else {
            GlanceTheme.colors.onSurface
        }

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
                provider = ImageProvider(state.currentIcon),
                contentDescription = null,
            )

            Spacer(
                modifier = GlanceModifier.width(
                    width = 12.dp,
                ),
            )

            Column {
                Text(
                    style = TextStyle(
                        color = textColor,
                        fontSize = sizeTemporary.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
                    ),
                    text = state.currentTemp,
                )

                Text(
                    modifier = GlanceModifier.defaultWeight(),
                    maxLines = 2,
                    style = TextStyle(
                        color = textColor,
                        fontSize = size.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
                    ),
                    text = state.currentCondition,
                )
            }
        }
    }
}
