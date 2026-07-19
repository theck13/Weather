package com.pranshulgg.weather_master_app.widgets.hourly.ui.variants

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
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
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.widgets.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.hourly.components.WidgetHourlyItem
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton

@Composable
fun WidgetHourlyLarge(
    config: WidgetConfig,
    count: Int,
    state: WidgetWeather?,
) {
    val mainIconSize = 32 * config.iconSize
    val locationFontSize = 16 * config.fontSize
    val tempFontSize = 42 * config.fontSize
    val textColor = GlanceTheme.colors.onSurface
    val textColorVariant = GlanceTheme.colors.onSurfaceVariant
    val textFontSize = 18 * config.fontSize

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Row(
                        modifier = GlanceModifier.defaultWeight(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            modifier = GlanceModifier.size(
                                size = mainIconSize.dp,
                            ),
                            contentDescription = null,
                            provider = ImageProvider(
                                state.currentIcon
                            ),
                        )

                        Spacer(
                            modifier = GlanceModifier.width(
                                width = 6.dp,
                            ),
                        )

                        Text(
                            modifier = GlanceModifier.fillMaxWidth(),
                            style = TextStyle(
                                color = textColor,
                                fontSize = textFontSize.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            maxLines = 1,
                            text = state.currentCondition,
                        )
                    }

                    Spacer(
                        modifier = GlanceModifier.width(
                            width = 6.dp,
                        ),
                    )

                    Row {
                        Text(
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = textFontSize.sp,
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
                                fontSize = textFontSize.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            text = state.daily.first().temperatureMinimum,
                        )
                    }
                }

                Spacer(
                    modifier = GlanceModifier.width(
                        width = 5.dp,
                    ),
                )

                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        style = TextStyle(
                            color = textColorVariant,
                            fontSize = locationFontSize.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        text = state.locationName,
                    )

                    Text(
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = tempFontSize.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        text = state.currentTemp,
                    )

                }
            }

            Spacer(
                modifier = GlanceModifier.defaultWeight(),
            )

            Row(
                modifier = GlanceModifier
                    .background(
                        colorProvider = GlanceTheme.colors.background,
                    )
                    .cornerRadius(
                        radius = 16.dp,
                    )
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.Bottom,
            ) {
                val hourlyIconSize = 22 * config.iconSize
                val hourlyTextSize = 14 * config.fontSize

                state.hourly.take(count).forEach {
                    WidgetHourlyItem(
                        fontSize = hourlyTextSize,
                        icon = it.icon,
                        iconSize = hourlyIconSize,
                        temp = it.temperature,
                        time = it.time,
                    )
                }
            }
        }
    } else {
        ReloadButton()
    }
}
