package com.pranshulgg.weather_master_app.widgets.ui.colors

import androidx.glance.unit.ColorProvider
import com.pranshulgg.weather_master_app.R

enum class WidgetTheme(
    val label: Int,
) {
    DARK(R.string.setting_dark_theme),
    LIGHT(R.string.setting_light_theme),
    SYSTEM(R.string.setting_system_theme),
    TRANSPARENT(R.string.color_transparent),
}

enum class WidgetThemeText(
    val label: Int,
) {
    DARK(R.string.setting_dark_theme),
    LIGHT(R.string.setting_light_theme),
    SYSTEM(R.string.setting_system_theme),
}

class WidgetColors {
    fun getBackgroundColor(
        widgetTheme: WidgetTheme,
    ): ColorProvider? {
        val color = when (widgetTheme) {
            WidgetTheme.DARK -> R.color.black
            WidgetTheme.LIGHT -> R.color.white
            WidgetTheme.SYSTEM -> null
            WidgetTheme.TRANSPARENT -> R.color.transparent
        }

        return if (color == null) null else ColorProvider(color)
    }

    fun getTextColor(
        theme: WidgetThemeText,
        widgetTheme: WidgetTheme,
    ): Pair<ColorProvider, Int>? {
        val color = when (theme) {
            WidgetThemeText.DARK -> R.color.black
            WidgetThemeText.LIGHT -> R.color.white
            WidgetThemeText.SYSTEM -> when (widgetTheme) {
                WidgetTheme.TRANSPARENT -> R.color.white
                WidgetTheme.DARK -> R.color.white
                WidgetTheme.LIGHT -> R.color.black
                else -> null
            }
        }

        return if (color == null) null else Pair(ColorProvider(color), color)
    }

    fun getTextVariantColor(
        theme: WidgetThemeText,
        widgetTheme: WidgetTheme,
    ): ColorProvider? {
        val color = when (theme) {
            WidgetThemeText.DARK -> R.color.black_60
            WidgetThemeText.LIGHT -> R.color.white_70
            WidgetThemeText.SYSTEM -> when (widgetTheme) {
                WidgetTheme.TRANSPARENT -> R.color.white_70
                WidgetTheme.DARK -> R.color.white_70
                WidgetTheme.LIGHT -> R.color.black_60
                else -> null
            }
        }

        return if (color == null) null else ColorProvider(color)
    }
}
