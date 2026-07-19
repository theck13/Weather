package com.heckofanapp.weather.widgets.hourly

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class WidgetHourlyReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WidgetHourly()
}
