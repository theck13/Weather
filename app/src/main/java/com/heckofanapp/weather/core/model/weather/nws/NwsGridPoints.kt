package com.heckofanapp.weather.core.model.weather.nws

data class NwsGridPoints(
    val gridX: Long,
    val gridY: Long,
    val lastUpdatedMilli: Long,
    val locationId: String,
    val officeId: String,
    val stationIdentifier: String?,
)
