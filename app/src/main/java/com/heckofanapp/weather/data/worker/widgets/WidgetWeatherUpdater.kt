package com.heckofanapp.weather.data.worker.widgets

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.heckofanapp.weather.widgets.WeatherWidgetStateDefinition
import com.heckofanapp.weather.widgets.WidgetConfig
import com.heckofanapp.weather.widgets.card.WidgetCard
import com.heckofanapp.weather.widgets.daily.WidgetDaily
import com.heckofanapp.weather.widgets.froggy.WidgetFroggy
import com.heckofanapp.weather.widgets.hourly.WidgetHourly
import com.heckofanapp.weather.widgets.pill.WidgetPill
import com.heckofanapp.weather.widgets.summary.WidgetSummary
import com.heckofanapp.weather.widgets.transparent.WidgetTransparent

class WeatherWidgetUpdater(
    private val context: Context,
) {
    private val widgetCard = WidgetCard()
    private val widgetDaily = WidgetDaily()
    private val widgetFroggy = WidgetFroggy()
    private val widgetHourly = WidgetHourly()
    private val widgetPill = WidgetPill()
    private val widgetSummary = WidgetSummary()
    private val widgetTransparent = WidgetTransparent()

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
            widget = widgetCard,
        )
        updateWidgets(
            ids = manager.getGlanceIds(
                provider = WidgetDaily::class.java,
            ),
            widget = widgetDaily,
        )
        updateWidgets(
            ids = manager.getGlanceIds(
                provider = WidgetFroggy::class.java,
            ),
            widget = widgetFroggy,
        )
        updateWidgets(
            ids = manager.getGlanceIds(
                provider = WidgetHourly::class.java,
            ),
            widget = widgetHourly,
        )
        updateWidgets(
            ids = manager.getGlanceIds(
                provider = WidgetPill::class.java,
            ),
            widget = widgetPill,
        )
        updateWidgets(
            ids = manager.getGlanceIds(
                provider = WidgetSummary::class.java,
            ),
            widget = widgetSummary,
        )
        updateWidgets(
            ids = manager.getGlanceIds(
                provider = WidgetTransparent::class.java,
            ),
            widget = widgetTransparent,
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
