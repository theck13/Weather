package com.heckofanapp.weather.core.prefs

import com.heckofanapp.weather.core.model.sources.SearchSource
import com.heckofanapp.weather.core.ui.theme.ThemeVariant

data class AppPrefsState(
    val appTheme: String,
    val setAppTheme: (String) -> Unit,

    val customThemeColor: String,
    val setCustomThemeColor: (String) -> Unit,

    val isCustomTheme: Boolean,
    val setIsCustomTheme: (Boolean) -> Unit,

    val isDynamicTheme: Boolean,
    val setIsDynamicColor: (Boolean) -> Unit,

    val themeVariant: ThemeVariant,
    val setThemeVariantType: (ThemeVariant) -> Unit,

    val timeFormat: String,
    val setTimeFormat: (String) -> Unit,

    val searchSource: SearchSource,
    val setSearchSource: (SearchSource) -> Unit,

    val backgroundUpdatesEnabled: Boolean,
    val setBackgroundUpdates: (Boolean) -> Unit,

    val backgroundUpdatesInterval: Int,
    val setBackgroundUpdatesInterval: (Int) -> Unit,

    val isFroggyLayout: Boolean,
    val setIsFroggyLayout: (Boolean) -> Unit,

    val isShowWeatherAnimations: Boolean,
    val setIsShowWeatherAnimations: (Boolean) -> Unit,

    val isWeatherBasedTheme: Boolean,
    val setIsWeatherBasedTheme: (Boolean) -> Unit,

    val isShowSummary: Boolean,
    val setIsShowSummary: (Boolean) -> Unit,

    val isGoogleSansFlex: Boolean,
    val setIsGoogleSansFlex: (Boolean) -> Unit
)
