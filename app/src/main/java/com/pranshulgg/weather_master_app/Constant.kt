package com.pranshulgg.weather_master_app

import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.core.model.sources.SearchSource
import com.pranshulgg.weather_master_app.core.ui.theme.ThemeVariant

const val DEFAULT_APP_THEME = "System"
const val DEFAULT_BACKGROUND_UPDATES_ENABLED = false
const val DEFAULT_BACKGROUND_UPDATES_INTERVAL = 60
const val DEFAULT_CUSTOM_THEME_COLOR = "#33b5e5"
const val DEFAULT_IS_CUSTOM_THEME = false
const val DEFAULT_IS_DYNAMIC_THEME = true
const val DEFAULT_IS_FROGGY_LAYOUT = false
const val DEFAULT_IS_GOOGLE_SANS_FLEX = true
const val DEFAULT_IS_SHOW_SUMMARY = false
const val DEFAULT_IS_SHOW_WEATHER_ANIMATIONS = false
const val DEFAULT_IS_WEATHER_BASED_THEME = false
const val DEFAULT_TIME_FORMAT = "24"

const val KEY_APP_THEME = "app_theme"
const val KEY_BACKGROUND_UPDATES_ENABLED = "backgroundUpdatesEnabled"
const val KEY_BACKGROUND_UPDATES_INTERVAL = "backgroundUpdatesInterval"
const val KEY_CUSTOM_THEME_COLOR = "custom_theme_color"
const val KEY_IS_CUSTOM_THEME = "isCustomTheme"
const val KEY_IS_DYNAMIC_THEME = "isDynamicTheme"
const val KEY_IS_FROGGY_LAYOUT = "isFroggyLayout"
const val KEY_IS_GOOGLE_SANS_FLEX = "isGoogleSansFlex"
const val KEY_IS_SHOW_SUMMARY = "isShowSummary"
const val KEY_IS_SHOW_WEATHER_ANIMATIONS = "isShowWeatherAnimations"
const val KEY_IS_WEATHER_BASED_THEME = "isWeatherBasedTheme"
const val KEY_SEARCH_SOURCE = "searchSource"
const val KEY_THEME_VARIANT_TYPE = "theme_variant_type"
const val KEY_TIME_FORMAT = "time_format"

const val NOTIFICATION_CHANNEL_ID = "weather_updates"
const val NOTIFICATION_CHANNEL_NAME = "Weather Updates"
const val NOTIFICATION_ID = 1001

const val SYSTEM_BAR_ALPHA = 0.75f

val DEFAULT_SEARCH_SOURCE = SearchSource.OPEN_METEO
val DEFAULT_THEME_VARIANT = ThemeVariant.EXPRESSIVE

val TouchMinimum = 48.dp
