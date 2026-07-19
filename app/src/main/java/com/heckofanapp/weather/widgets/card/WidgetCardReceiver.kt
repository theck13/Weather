package com.heckofanapp.weather.widgets.card

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class WidgetCardReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WidgetCard()
}
