package com.pranshulgg.weather_master_app.widgets.summary

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
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.Activity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import kotlinx.serialization.json.Json

class WidgetSummary : GlanceAppWidget() {
    override val stateDefinition = WeatherWidgetStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val widgetState = currentState<WeatherWidgetStateJson>()
            val json = widgetState.json
            val state = json?.let {
                Json.decodeFromString<WidgetWeather>(it)
            }

            Box(
                modifier = GlanceModifier
                    .appWidgetBackgroundShape()
                    .clickable(
                        onClick = actionStartActivity<Activity>(),
                    )
                    .fillMaxSize()
            ) {
                if (state != null) {
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(
                                all = 16.dp,
                            ),
                    ) {
                        Row {
                            Image(
                                colorFilter = ColorFilter.tint(
                                    colorProvider = GlanceTheme.colors.secondary,
                                ),
                                contentDescription = null,
                                provider = ImageProvider(R.drawable.ic_article_24),
                            )

                            Spacer(
                                modifier = GlanceModifier.width(
                                    width = 5.dp,
                                ),
                            )

                            Text(
                                style = TextStyle(
                                    color = GlanceTheme.colors.secondary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                text = context.getString(R.string.setting_day_summary),
                            )
                        }

                        Spacer(
                            modifier = GlanceModifier.height(
                                height = 5.dp,
                            ),
                        )

                        Text(
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            text = state.summary,
                        )
                    }
                } else {
                    ReloadButton()
                }
            }
        }
    }
}

@Composable
private fun GlanceModifier.appWidgetBackgroundShape(): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= 31) {
        this
            .background(
                colorProvider = GlanceTheme.colors.widgetBackground
            )
            .cornerRadius(
                radius = 16.dp,
            )
    } else {
        this
            .background(
                imageProvider = ImageProvider(R.drawable.il_widget_background),
            )
    }
}
