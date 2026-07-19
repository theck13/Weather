package com.pranshulgg.weather_master_app.data.worker.widgets

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.card.WidgetCard
import com.pranshulgg.weather_master_app.widgets.daily.WidgetDaily
import com.pranshulgg.weather_master_app.widgets.hourly.WidgetHourly
import com.pranshulgg.weather_master_app.widgets.pill.WidgetPill
import com.pranshulgg.weather_master_app.widgets.summary.WidgetSummary
import com.pranshulgg.weather_master_app.widgets.transparent.WidgetTransparent

class WeatherWidgetUpdater(
    private val context: Context,
) {
    private val pill = WidgetPill()
    private val summary = WidgetSummary()
    private val widget = WidgetHourly()
    private val widgetClockDaily = WidgetDaily()
    private val widgetGlance = WidgetTransparent()
    private val widgetHorizontal = WidgetCard()

    suspend fun update(
        json: String,
    ) {
        val manager = GlanceAppWidgetManager(context)

        suspend fun <T : GlanceAppWidget> updateWidgets(
            widget: T,
            ids: List<GlanceId>,
        ) {
            ids.forEach { id ->
                try {
                    updateAppWidgetState(context, WeatherWidgetStateDefinition, id) { current ->
                        current.copy(
                            config = current.config,
                            json = json,
                        )
                    }
                    widget.update(
                        context = context,
                        id = id,
                    )
                } catch (e: Exception) {
                    Log.e("WeatherWidgetUpdater", "Failed to update widget $id", e)
                }
            }
        }

        updateWidgets(
            ids = manager.getGlanceIds(
                provider = WidgetCard::class.java,
            ),
            widget = widgetHorizontal,
        )
        updateWidgets(
            ids = manager.getGlanceIds(
                provider = WidgetDaily::class.java,
            ),
            widget = widgetClockDaily,
        )
        updateWidgets(
            ids = manager.getGlanceIds(
                provider = WidgetHourly::class.java,
            ),
            widget = widget,
        )
        updateWidgets(
            ids = manager.getGlanceIds(
                provider = WidgetPill::class.java,
            ),
            widget = pill,
        )
        updateWidgets(
            ids = manager.getGlanceIds(
                provider = WidgetSummary::class.java,
            ),
            widget = summary,
        )
        updateWidgets(
            ids = manager.getGlanceIds(
                provider = WidgetTransparent::class.java,
            ),
            widget = widgetGlance,
        )
    }

    suspend fun saveWidgetConfig(
        config: WidgetConfig,
        context: Context,
        widgetId: Int,
    ) {
        val manager = GlanceAppWidgetManager(context)
        val glanceId = manager.getGlanceIdBy(widgetId)

        updateAppWidgetState(
            context = context,
            definition = WeatherWidgetStateDefinition,
            glanceId = glanceId,
        ) {
            it.copy(
                config = config
            )
        }
    }
}
