package com.heckofanapp.weather.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.heckofanapp.weather.widgets.card.WidgetCardReceiver
import com.heckofanapp.weather.widgets.daily.WidgetDailyReceiver
import com.heckofanapp.weather.widgets.hourly.WidgetHourlyReceiver
import com.heckofanapp.weather.widgets.pill.WidgetPillReceiver
import com.heckofanapp.weather.widgets.summary.WidgetSummaryReceiver
import com.heckofanapp.weather.widgets.transparent.WidgetTransparentReceiver

private val widgetReceivers = listOf(
    WidgetDailyReceiver::class.java,
    WidgetTransparentReceiver::class.java,
    WidgetSummaryReceiver::class.java,
    WidgetCardReceiver::class.java,
    WidgetHourlyReceiver::class.java,
    WidgetPillReceiver::class.java,
)

/**
 * Whether device has any Weather widgets on home screen currently.
 * This gates background location permission since it is only needed to keep widgets updated.
 */
fun Context.hasActiveWidgets(): Boolean {
    val manager = AppWidgetManager.getInstance(this)
    return widgetReceivers.any { receiver ->
        manager.getAppWidgetIds(ComponentName(this, receiver)).isNotEmpty()
    }
}
