package com.heckofanapp.weather.widgets.pill

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import com.heckofanapp.weather.Activity
import com.heckofanapp.weather.widgets.WeatherWidgetStateDefinition
import com.heckofanapp.weather.widgets.WeatherWidgetStateJson
import com.heckofanapp.weather.widgets.model.WidgetWeather
import com.heckofanapp.weather.widgets.pill.ui.WidgetPill
import kotlinx.serialization.json.Json

class WidgetPill : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact
    override val stateDefinition = WeatherWidgetStateDefinition
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            val widgetState = currentState<WeatherWidgetStateJson>()
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
                WidgetPill(
                    state = state,
                )
            }
        }
    }
}
