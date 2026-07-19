package com.pranshulgg.weather_master_app.widgets.pill.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton

@Composable
fun WidgetPill(
    state: WidgetWeather?,
) {
    val textColor = GlanceTheme.colors.primary

    if (state != null) {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = GlanceModifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(
                    colorProvider = GlanceTheme.colors.widgetBackground
                ),
                contentDescription = null,
                provider = ImageProvider(R.drawable.il_pill_shape),
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = GlanceModifier.padding(
                        start = 38.dp,
                    ),
                    style = TextStyle(
                        color = textColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 58.sp,
                    ),
                    text = state.currentTemp,
                )

                Spacer(
                    modifier = GlanceModifier.height(
                        height = 50.dp,
                    ),
                )
            }

            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(
                        top = 70.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = GlanceModifier.size(
                        size = 64.dp,
                    ),
                    contentDescription = null,
                    provider = ImageProvider(state.currentIcon),
                )

                Spacer(
                    modifier = GlanceModifier.width(
                        60.dp,
                    ),
                )
            }
        }
    } else {
        ReloadButton()
    }
}
