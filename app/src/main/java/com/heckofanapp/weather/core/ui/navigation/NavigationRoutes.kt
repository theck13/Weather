package com.heckofanapp.weather.core.ui.navigation

object NavigationRoutes {
    const val ABOUT = "about"
    const val AIR = "air_quality"
    const val APPEARANCE = "appearance"
    const val BACKGROUND_UPDATES = "background_updates"
    const val CELESTIAL = "celestial"
    const val DAILY = "daily"
    const val HUMIDITY = "humidity"
    const val LANGUAGE = "language"
    const val LICENSE = "LICENSE"
    const val MAIN = "main"
    const val PRESSURE = "pressure"
    const val PRIVACY_POLICY = "privacy_policy"
    const val RAIN = "rain"
    const val SEARCH = "search"
    const val SETTINGS = "settings"
    const val SNOW = "snow"
    const val SOURCES = "sources"
    const val TERMS_CONDITIONS = "terms_conditions"
    const val UNITS = "units"
    const val ULTRAVIOLET = "ultraviolet"
    const val VISIBILITY = "visibility"
    const val WIND = "wind"

    fun daily(
        index: Int,
        locationId: String,
    ): String {
        return "$DAILY/$index/$locationId"
    }

    fun block(
        block: String,
        index: Int,
        locationId: String,
    ): String {
        return "$block/$index/$locationId"
    }
}
