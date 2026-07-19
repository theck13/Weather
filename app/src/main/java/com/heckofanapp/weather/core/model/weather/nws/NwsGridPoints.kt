package com.heckofanapp.weather.core.model.weather.nws

data class NwsGridPoints(
    val locationId: String,
    val officeId: String,
    val gridX: Long,
    val gridY: Long,
    val stationIdentifier: String?,
    val lastUpdatedMilli: Long
)