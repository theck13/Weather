package com.heckofanapp.weather.widgets.transparent

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class WidgetTransparentReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WidgetTransparent()
}
