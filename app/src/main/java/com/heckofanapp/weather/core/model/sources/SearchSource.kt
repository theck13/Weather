package com.heckofanapp.weather.core.model.sources

enum class SearchSource(
    val displayName: String,
) {
    GEO_NAMES(
        displayName = "GeoNames",
    ),
    OPEN_METEO(
        displayName = "Open Meteo",
    ),
}
