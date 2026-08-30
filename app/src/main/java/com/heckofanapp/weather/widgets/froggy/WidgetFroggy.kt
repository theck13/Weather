package com.heckofanapp.weather.widgets.froggy

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
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
import com.heckofanapp.weather.Activity
import com.heckofanapp.weather.R
import com.heckofanapp.weather.SpaceDefault
import com.heckofanapp.weather.widgets.WeatherWidgetStateDefinition
import com.heckofanapp.weather.widgets.WeatherWidgetStateJson
import com.heckofanapp.weather.widgets.model.WidgetWeather
import com.heckofanapp.weather.widgets.params.WidgetSizePoints
import com.heckofanapp.weather.widgets.ui.ReloadButton
import com.heckofanapp.weather.widgets.ui.colors.WidgetColors
import com.heckofanapp.weather.widgets.ui.colors.WidgetTheme
import kotlinx.serialization.json.Json

class WidgetFroggy : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(
        sizes = WidgetSizePoints.SIZES,
    )
    override val stateDefinition = WeatherWidgetStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            val widgetState = currentState<WeatherWidgetStateJson>()
            val widgetColors = WidgetColors()
            val json = widgetState.json
            val state = json?.let {
                Json.decodeFromString<WidgetWeather>(it)
            }

            val config = widgetState.config
            val modifier = when (config.widgetTheme) {
                WidgetTheme.TRANSPARENT -> GlanceModifier.fillMaxSize()
                else -> GlanceModifier
                    .appWidgetBackgroundShape(
                        theme = config.widgetTheme,
                        widgetColors = widgetColors,
                    )
                    .fillMaxSize()
            }

            if (state != null) {
                val textColor = widgetColors.getTextColor(
                    theme = config.widgetThemeText,
                    widgetTheme = config.widgetTheme,
                ) ?: Pair(GlanceTheme.colors.onSurface, null)

                val textVariantColor = widgetColors.getTextVariantColor(
                    theme = config.widgetThemeText,
                    widgetTheme = config.widgetTheme,
                ) ?: GlanceTheme.colors.onSurfaceVariant

                val conditionFontSize = 18 * config.fontSize
                val locationFontSize = 14 * config.fontSize
                val mainIconSize = 42 * config.iconSize
                val temperatureFontSize = 30 * config.fontSize

                Box(
                    modifier = modifier.clickable(
                        onClick = actionStartActivity<Activity>(),
                    ),
                ) {
                    Column(
                        modifier = GlanceModifier.fillMaxSize(),
                    ) {
                        Row(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .padding(
                                    end = 18.dp,
                                    start = 18.dp,
                                    top = 18.dp,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                modifier = GlanceModifier.size(
                                    size = mainIconSize.dp,
                                ),
                                contentDescription = null,
                                provider = ImageProvider(state.currentIcon),
                            )

                            Spacer(
                                modifier = GlanceModifier.width(
                                    width = SpaceDefault / 2,
                                ),
                            )

                            Column(
                                modifier = GlanceModifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        style = TextStyle(
                                            color = textColor.first,
                                            fontSize = temperatureFontSize.sp,
                                            fontWeight = FontWeight.Medium,
                                        ),
                                        maxLines = 1,
                                        text = state.currentTemp,
                                    )

                                    Spacer(
                                        modifier = GlanceModifier.defaultWeight(),
                                    )

                                    Text(
                                        style = TextStyle(
                                            color = textColor.first,
                                            fontSize = conditionFontSize.sp,
                                            fontWeight = FontWeight.Medium,
                                        ),
                                        maxLines = 1,
                                        text = state.currentCondition,
                                    )
                                }

                                Row(
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        style = TextStyle(
                                            color = textVariantColor,
                                            fontSize = locationFontSize.sp,
                                            fontWeight = FontWeight.Medium,
                                        ),
                                        maxLines = 1,
                                        text = state.locationName,
                                    )

                                    Spacer(
                                        modifier = GlanceModifier.defaultWeight(),
                                    )

                                    state.daily.firstOrNull()?.let {
                                        Text(
                                            style = TextStyle(
                                                color = textColor.first,
                                                fontSize = locationFontSize.sp,
                                                fontWeight = FontWeight.Medium,
                                            ),
                                            maxLines = 1,
                                            text = "H ${it.temperatureMaximum} / L ${it.temperatureMinimum}",
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(
                            modifier = GlanceModifier.defaultWeight(),
                        )

                        Box(
                            modifier = GlanceModifier.fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            Image(
                                modifier = GlanceModifier.fillMaxWidth(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                provider = ImageProvider(state.currentFrog),
                            )
                        }
                    }
                }
            } else {
                ReloadButton()
            }
        }
    }
}

@Composable
private fun GlanceModifier.appWidgetBackgroundShape(
    theme: WidgetTheme,
    widgetColors: WidgetColors,
): GlanceModifier {
    val color = widgetColors.getBackgroundColor(
        widgetTheme = theme,
    ) ?: GlanceTheme.colors.widgetBackground

    return if (Build.VERSION.SDK_INT >= 31) {
        this
            .background(
                colorProvider = color,
            )
            .cornerRadius(
                radius = SpaceDefault,
            )
    } else {
        this.background(
            colorFilter = ColorFilter.tint(
                colorProvider = color,
            ),
            imageProvider = ImageProvider(R.drawable.il_widget_background),
        )
    }
}
