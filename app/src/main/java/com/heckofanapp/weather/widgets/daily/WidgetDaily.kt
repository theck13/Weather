package com.heckofanapp.weather.widgets.daily

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.AlarmClock
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.heckofanapp.weather.Activity
import com.heckofanapp.weather.R
import com.heckofanapp.weather.widgets.WeatherWidgetStateDefinition
import com.heckofanapp.weather.widgets.WeatherWidgetStateJson
import com.heckofanapp.weather.widgets.WidgetConfig
import com.heckofanapp.weather.widgets.model.WidgetWeather
import com.heckofanapp.weather.widgets.params.WidgetSizePoints
import com.heckofanapp.weather.widgets.ui.ReloadButton
import com.heckofanapp.weather.widgets.ui.colors.WidgetColors
import com.heckofanapp.weather.widgets.ui.colors.WidgetTheme
import com.heckofanapp.weather.widgets.ui.views.WidgetClock
import com.heckofanapp.weather.widgets.ui.views.WidgetDate
import kotlinx.serialization.json.Json

class WidgetDaily : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(
        sizes = WidgetSizePoints.SIZES,
    )
    override val stateDefinition =
        WeatherWidgetStateDefinition

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

                val clockFontSize = 50 * config.fontSize
                val dateConditionFontSize = 18 * config.fontSize
                val mainIconSize = 52 * config.iconSize

                Box(
                    modifier = modifier.clickable(
                        onClick = actionStartActivity<Activity>(),
                    ),
                ) {
                    Column(
                        GlanceModifier
                            .fillMaxSize()
                            .padding(
                                bottom = 12.dp,
                                end = 18.dp,
                                top = 18.dp,
                                start = 18.dp,
                            ),
                    ) {
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = GlanceModifier
                                    .clickable(
                                        onClick = openAvailableClockApp(),
                                    )
                                    .wrapContentSize(),
                            ) {
                                WidgetClock(
                                    color = textColor.second,
                                    context = context,
                                    size = clockFontSize,
                                )
                            }

                            Spacer(
                                modifier = GlanceModifier.defaultWeight(),
                            )

                            Image(
                                modifier = GlanceModifier.size(
                                    size = mainIconSize.dp,
                                ),
                                contentDescription = null,
                                provider = ImageProvider(state.currentIcon),
                            )
                        }

                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                        ) {
                            Box(
                                modifier = GlanceModifier
                                    .clickable(
                                        onClick = openAvailableCalendarApp(),
                                    )
                                    .wrapContentSize()
                            ) {
                                WidgetDate(
                                    color = textColor.second,
                                    context = context,
                                    format = config.dateFormat,
                                    size = dateConditionFontSize,
                                )
                            }

                            Spacer(
                                modifier = GlanceModifier.width(
                                    width = 16.dp,
                                ),
                            )

                            Text(
                                modifier = GlanceModifier.defaultWeight(),
                                style = TextStyle(
                                    color = textColor.first,
                                    fontSize = dateConditionFontSize.sp,
                                    textAlign = TextAlign.End,
                                ),
                                maxLines = 1,
                                text = "${state.currentTemp} ${state.currentCondition}",
                            )
                        }

                        Spacer(
                            modifier = GlanceModifier.defaultWeight(),
                        )

                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            state.daily.take(config.dailyCount).forEach {
                                Column(
                                    modifier = GlanceModifier
                                        .defaultWeight()
                                        .padding(
                                            vertical = 5.dp,
                                        ),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    DailyItem(
                                        config = config,
                                        day = it.time,
                                        icon = it.icon,
                                        temperatures = "${it.temperatureMaximum}/${it.temperatureMinimum}",
                                        textColor = textColor,
                                    )
                                }
                            }
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
                radius = 16.dp,
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

private fun openAvailableClockApp(): Action =
    actionStartActivity(
        Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )

private fun openAvailableCalendarApp(): Action {
    return actionStartActivity(
        Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_CALENDAR)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}

@Composable
private fun DailyItem(
    config: WidgetConfig,
    day: String,
    icon: Int,
    temperatures: String,
    textColor: Pair<ColorProvider, Int?>,
) {
    val fontSize = 15 * config.fontSize
    val iconSize = 28 * config.iconSize

    Text(
        style = TextStyle(
            color = textColor.first,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Medium,
        ),
        text = day,
    )

    Spacer(
        modifier = GlanceModifier.height(
            height = 3.dp,
        )
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
        )
    )

    Text(
        style = TextStyle(
            color = textColor.first,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Medium,
        ),
        text = temperatures,
    )
}
