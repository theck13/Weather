package com.heckofanapp.weather.data.local.mapper.weather.sources.nws

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.weather.nws.NwsGridPoints
import com.heckofanapp.weather.data.local.entity.weather.nws.NwsGridPointsEntity

// ---------------------------- DOMAIN TO ENTITY ----------------------------

fun NwsGridPoints.toEntity(
    location: Location,
): NwsGridPointsEntity {
    return NwsGridPointsEntity(
        locationId = location.id,
        officeId = this.officeId,
        gridX = this.gridX,
        gridY = this.gridY,
        stationIdentifier = stationIdentifier!!, // CAN'T BE NULL, WE NEED THIS :P
        lastUpdatedMilli = lastUpdatedMilli,
    )
}

// ---------------------------- ENTITY TO DOMAIN ----------------------------

fun NwsGridPointsEntity.toDomain(): NwsGridPoints {
    return NwsGridPoints(
        gridX = this.gridX,
        gridY = this.gridY,
        lastUpdatedMilli = this.lastUpdatedMilli,
        locationId = this.locationId,
        officeId = this.officeId,
        stationIdentifier = this.stationIdentifier,
    )
}
