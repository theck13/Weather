package com.heckofanapp.weather.data.repository

import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.data.local.dao.location.LocationsDao
import com.heckofanapp.weather.data.local.dao.weather.nws.NwsDao
import jakarta.inject.Inject

class WeatherDataReconcilerRepository @Inject constructor(
    private val nwsDao: NwsDao,
    private val locationDao: LocationsDao,
) {
    suspend fun cleanNws(
        locationId: String,
    ) {
        nwsDao.deleteGridPointsForLocation(
            locationId = locationId,
        )
    }

    /**
     * This clears up extra data for sources not used
     * For e.g. when you switch from NWS to Open Meteo
     * NWS might have saved grid points
     * which are important to be removed from the DB so they don't end up stale
     */
    suspend fun cleanUpStaleData(
        locationId: String,
        previousSource: WeatherSource,
    ) {
        when (previousSource) {
            WeatherSource.NWS -> cleanNws(locationId)
            else -> {}
        }
    }
}
