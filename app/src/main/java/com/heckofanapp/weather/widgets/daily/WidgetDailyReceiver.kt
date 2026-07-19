package com.heckofanapp.weather.widgets.daily

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class WidgetDailyReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WidgetDaily()
}
