package com.heckofanapp.weather.widgets.pill

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class WidgetPillReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WidgetPill()
}
