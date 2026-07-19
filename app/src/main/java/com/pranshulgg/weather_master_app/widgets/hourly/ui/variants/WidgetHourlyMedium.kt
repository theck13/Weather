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
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton

@Composable
fun WidgetHourlyMedium(
    state: WidgetWeather?,
) {
    val textColor = GlanceTheme.colors.onSurface

    if (state != null) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(
                    all = 18.dp,
                ),
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = GlanceModifier.size(
                        size = 28.dp,
                    ),
                    contentDescription = null,
                    provider = ImageProvider(state.currentIcon),
                )

                Text(
                    modifier = GlanceModifier.fillMaxWidth(),
                    maxLines = 1,
                    style = TextStyle(
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End,
                    ),
                    text = state.currentCondition,
                )
            }

            Spacer(
                modifier = GlanceModifier.defaultWeight(),
            )

            Text(
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                ),
                text = state.currentTemp,
            )

            Row {
                Text(
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    text = state.daily.first().temperatureMaximum,
                )

                Spacer(
                    modifier = GlanceModifier.width(
                        width = 8.dp,
                    ),
                )

                Text(
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    text = state.daily.first().temperatureMinimum,
                )
            }
        }
    } else {
        ReloadButton()
    }
}
