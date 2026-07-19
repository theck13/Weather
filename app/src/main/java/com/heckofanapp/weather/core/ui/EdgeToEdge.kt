package com.heckofanapp.weather.core.ui

import android.content.res.Configuration
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import com.heckofanapp.weather.R
import com.heckofanapp.weather.SYSTEM_BAR_ALPHA

fun ComponentActivity.setSystemBarStyle(
    background: Int? = null,
    isDark: Boolean,
) {
    val alpha = (SYSTEM_BAR_ALPHA * 255).toInt()

    fun scrim(isDark: Boolean): Int {
        val bg = if (background != null) {
            background
        } else {
            val configuration = Configuration(resources.configuration)
            configuration.uiMode =
                (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
                if (isDark) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
            createConfigurationContext(configuration).getColor(R.color.background)
        }
        return Color.argb(alpha, Color.red(bg), Color.green(bg), Color.blue(bg))
    }

    val style = SystemBarStyle.auto(
        darkScrim = scrim(
            isDark = true,
        ),
        detectDarkMode = {
            isDark
        },
        lightScrim = scrim(
            isDark = false,
        ),
    )

    enableEdgeToEdge(
        navigationBarStyle = style,
        statusBarStyle = style,
    )

    @Suppress("DEPRECATION")
    window.navigationBarColor = if (isDark) scrim(isDark = true) else scrim(isDark = false)
}
