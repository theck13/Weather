package com.heckofanapp.weather.widgets.hourly

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import com.heckofanapp.weather.Activity
import com.heckofanapp.weather.R
import com.heckofanapp.weather.widgets.WeatherWidgetStateDefinition
import com.heckofanapp.weather.widgets.WeatherWidgetStateJson
import com.heckofanapp.weather.widgets.hourly.ui.variants.WidgetHourlyLarge
import com.heckofanapp.weather.widgets.hourly.ui.variants.WidgetHourlyMedium
import com.heckofanapp.weather.widgets.hourly.ui.variants.WidgetHourlySmall
import com.heckofanapp.weather.widgets.model.WidgetVariant
import com.heckofanapp.weather.widgets.model.WidgetWeather
import com.heckofanapp.weather.widgets.params.WidgetSizePoints
import kotlinx.serialization.json.Json

class WidgetHourly : GlanceAppWidget() {
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
            val config = widgetState.config
            val json = widgetState.json
            val state = json?.let {
                Json.decodeFromString<WidgetWeather>(it)
            }

            Box(
                GlanceModifier
                    .appWidgetBackgroundShape()
                    .clickable(
                        onClick = actionStartActivity<Activity>(),
                    )
                    .fillMaxSize(),
            ) {
                when (config.variant) {
                    WidgetVariant.LARGE -> WidgetHourlyLarge(
                        config = config,
                        count = config.hourlyCount,
                        state = state,
                    )

                    WidgetVariant.MEDIUM -> WidgetHourlyMedium(
                        state = state,
                    )

                    WidgetVariant.SMALL -> WidgetHourlySmall(
                        config = config,
                        state = state,
                    )
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
                colorProvider = GlanceTheme.colors.widgetBackground,
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
