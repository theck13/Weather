package com.heckofanapp.weather.widgets

import com.heckofanapp.weather.widgets.model.WidgetVariant
import com.heckofanapp.weather.widgets.ui.colors.WidgetTheme
import com.heckofanapp.weather.widgets.ui.colors.WidgetThemeText
import kotlinx.serialization.Serializable

@Serializable
data class WidgetConfig(
    val clockSize: Float = 32.00f,
    val dailyCount: Int = 4,
    val dateFormat: String = "EEE d MMM",
    val fontSize: Float = 1.00f,
    val hourlyCount: Int = 6,
    val iconSize: Float = 1.00f,
    val showClock: Boolean = true,
    val variant: WidgetVariant = WidgetVariant.LARGE,
    val widgetTheme: WidgetTheme = WidgetTheme.SYSTEM,
    val widgetThemeText: WidgetThemeText = WidgetThemeText.SYSTEM,
)