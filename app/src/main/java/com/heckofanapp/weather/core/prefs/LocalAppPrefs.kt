package com.heckofanapp.weather.core.prefs

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppPrefs = staticCompositionLocalOf<AppPrefsState> {
    error("AppPrefsState not provided")
}
