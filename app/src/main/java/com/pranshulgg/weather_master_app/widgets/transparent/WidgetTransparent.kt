package com.pranshulgg.weather_master_app.widgets.transparent

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
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
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.pranshulgg.weather_master_app.Activity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.params.WidgetSizePoints
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import com.pranshulgg.weather_master_app.widgets.ui.views.WidgetClock
import com.pranshulgg.weather_master_app.widgets.ui.views.WidgetDate
import kotlinx.serialization.json.Json

class WidgetTransparent : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(
        sizes = WidgetSizePoints.SIZES

    )
    override val stateDefinition =
        WeatherWidgetStateDefinition

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            val context = LocalContext.current
            val widgetState = currentState<WeatherWidgetStateJson>()

            val config = widgetState.config
            val json = widgetState.json
            val state = json?.let {
                Json.decodeFromString<WidgetWeather>(it)
            }

            Box(
                GlanceModifier
                    .clickable(
                        onClick = actionStartActivity<Activity>(),
                    )
                    .fillMaxSize(),
            ) {
                if (state != null) {
                    Column(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(
                                all = 16.dp,
                            ),
                    ) {
                        if (config.showClock) {
                            WidgetClock(
                                color = R.color.white,
                                context = context,
                                size = config.clockSize,
                            )
                        }

                        WidgetDate(
                            color = R.color.white,
                            context = context,
                            format = config.dateFormat,
                            size = 20.00f,
                        )

                        Spacer(
                            modifier = GlanceModifier.height(
                                height = 5.dp,
                            ),
                        )

                        Row {
                            Image(
                                modifier = GlanceModifier.size(
                                    size = 24.dp,
                                ),
                                contentDescription = null,
                                provider = ImageProvider(state.currentIcon),
                            )

                            Spacer(
                                modifier = GlanceModifier.width(
                                    width = 5.dp,
                                ),
                            )

                            Box {
                                Text(
                                    modifier = GlanceModifier.padding(
                                        start = 2.dp,
                                        top = 2.dp,
                                    ),
                                    style = TextStyle(
                                        color = ColorProvider(R.color.shadow),
                                        fontSize = 18.sp,
                                    ),
                                    text = "${state.currentTemp} • ",
                                )

                                Text(
                                    style = TextStyle(
                                        color = ColorProvider(R.color.white),
                                        fontSize = 18.sp,
                                    ),
                                    text = "${state.currentTemp} • ",
                                )
                            }

                            Box {
                                Text(
                                    modifier = GlanceModifier.padding(
                                        start = 2.dp,
                                        top = 2.dp,
                                    ),
                                    style = TextStyle(
                                        color = ColorProvider(R.color.shadow),
                                        fontSize = 18.sp,
                                    ),
                                    text = state.currentCondition,
                                )

                                Text(
                                    style = TextStyle(
                                        color = ColorProvider(R.color.white),
                                        fontSize = 18.sp,
                                    ),
                                    text = state.currentCondition,
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
}
