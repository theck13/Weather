package com.pranshulgg.weather_master_app.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.pranshulgg.weather_master_app.widgets.card.WidgetCardReceiver
import com.pranshulgg.weather_master_app.widgets.daily.WidgetDailyReceiver
import com.pranshulgg.weather_master_app.widgets.hourly.WidgetHourlyReceiver
import com.pranshulgg.weather_master_app.widgets.pill.WidgetPillReceiver
import com.pranshulgg.weather_master_app.widgets.summary.WidgetSummaryReceiver
import com.pranshulgg.weather_master_app.widgets.transparent.WidgetTransparentReceiver

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
