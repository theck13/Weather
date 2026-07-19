package com.heckofanapp.weather.core.model.sources

enum class SearchSource(
    val displayName: String,
) {
    GEO_NAMES("GeoNames"),
    OPEN_METEO("Open Meteo"),
}
