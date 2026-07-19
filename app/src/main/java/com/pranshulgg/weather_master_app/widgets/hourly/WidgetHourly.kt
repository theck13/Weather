package com.pranshulgg.weather_master_app.widgets.hourly

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
import com.pranshulgg.weather_master_app.Activity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.hourly.ui.variants.WidgetHourlyLarge
import com.pranshulgg.weather_master_app.widgets.hourly.ui.variants.WidgetHourlyMedium
import com.pranshulgg.weather_master_app.widgets.hourly.ui.variants.WidgetHourlySmall
import com.pranshulgg.weather_master_app.widgets.model.WidgetVariant
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.params.WidgetSizePoints
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
