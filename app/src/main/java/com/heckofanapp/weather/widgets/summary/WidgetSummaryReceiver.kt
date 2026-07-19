package com.heckofanapp.weather.widgets.summary

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class WidgetSummaryReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WidgetSummary()
}
