package com.heckofanapp.weather.data.repository

import android.content.Context
import androidx.room.Transaction
import com.heckofanapp.weather.core.model.domain.AppException
import com.heckofanapp.weather.core.model.domain.airquality.AirQuality
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.network.sources.address.nominatim.json.NominatimRepository
import com.heckofanapp.weather.data.local.dao.airquality.AirQualityDao
import com.heckofanapp.weather.data.local.dao.location.LocationsDao
import com.heckofanapp.weather.data.local.mapper.airquality.toDomain
import com.heckofanapp.weather.data.local.mapper.locations.toDomain
import com.heckofanapp.weather.data.local.mapper.locations.toEntity
import com.heckofanapp.weather.data.local.mapper.weather.toDomain
import com.heckofanapp.weather.data.provider.devicelocation.DeviceLocation
import com.heckofanapp.weather.data.provider.devicelocation.GetDeviceLocation
import com.heckofanapp.weather.data.provider.devicelocation.getCountryCode
import com.heckofanapp.weather.feature.intro.toDomain
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.ZoneId
import kotlin.coroutines.resumeWithException

class LocationsRepository @Inject constructor(
    private val dao: LocationsDao,
    private val airQualityDao: AirQualityDao,
    @param:ApplicationContext private val context: Context,
    private val nominatimRepository: NominatimRepository,
) {
    private val LOCATION_UPDATE_THRESHOLD_METERS = 1000f // 1000m

    class Callbacks {
        fun onSuccess(cont: CancellableContinuation<DeviceLocation>, result: DeviceLocation) {
            if (cont.isActive) {
                cont.resumeWith(Result.success(result))
            }
        }

        fun onTimeout(cont: CancellableContinuation<DeviceLocation>) {
            if (cont.isActive) {
                cont.resumeWithException(AppException.CurrentLocationUnavailable())
            }
        }
    }

    private val callback = Callbacks()

    fun getLocations(): Flow<List<Location>> {
        return dao.getLocations().map { it.toDomain() }
    }

    suspend fun getLocationsOnce(): List<Location> {
        return dao.getLocationsOnce().map { it.toDomain() }
    }

    @Transaction
    suspend fun deleteLocation(id: String) {
        dao.deleteLocation(id)
        airQualityDao.deleteCurrentAirQuality(id)
    }

    suspend fun updateSourceForLocation(id: String, source: WeatherSource) {
        dao.updateSourceForLocation(id, source)
    }

    @Transaction
    suspend fun saveLocation(location: Location) {
        val count = dao.getLocationsCount()
        val isFirst = count == 0
        // Append new locations to the end of the user-defined order.
        dao.insertWeatherLocation(location.toEntity().copy(sortOrder = count))

        if (isFirst) {
            updateDefaultLocation(location.id)
        }
    }

    @Transaction
    suspend fun updateLocationsOrder(orderedIds: List<String>) {
        orderedIds.forEachIndexed { index, id ->
            dao.updateLocationOrder(id, index)
        }
    }

    suspend fun isLocationsEmpty(): Boolean {
        val isEmpty = dao.getLocationsCount() == 0
        return isEmpty
    }

    @Transaction
    suspend fun updateDefaultLocation(id: String) {
        dao.clearDefaultLocations()
        dao.updateDefaultLocation(id)
    }

    suspend fun getAirQualityForLocation(locationId: String): AirQuality? {
        return airQualityDao.getAirQualityForLocation(locationId)?.toDomain()
    }

    fun getDefaultLocation(): Flow<Location?> {
        return dao.getDefaultLocation().map { it?.toDomain() }
    }

    suspend fun getWeatherForLocation(locationId: String): Weather {
        return dao.getWeatherForLocation(locationId).toDomain()
    }

    val getDeviceLocation = GetDeviceLocation()

    /**
     * Moves device-location pin to device's current position.  Returns updated location when it
     * moved more than [LOCATION_UPDATE_THRESHOLD_METERS], or null when there is no device location,
     * no GPS fix, or device hasn't moved far enough.
     */
    suspend fun updateDeviceLocationPosition(): Location? {
        // Nothing to move if user hasn't added a device location.
        val currentLocation = dao.getDeviceLocation() ?: return null

        val location = suspendCancellableCoroutine { cont ->
            getDeviceLocation.getDeviceLocation(
                context = context,
                onTimeout = {
                    callback.onTimeout(cont)
                },
                onResult = { result ->
                    callback.onSuccess(cont, result)
                },
            )
        }

        val newLatitude = location.latitude ?: return null
        val newLongitude = location.longitude ?: return null
        val results = FloatArray(1)

        android.location.Location.distanceBetween(
            currentLocation.lat,
            currentLocation.lon,
            newLatitude,
            newLongitude,
            results,
        )

        val distanceInMeters = results[0]
        // Update location if needed.
        if (distanceInMeters < LOCATION_UPDATE_THRESHOLD_METERS) {
            return null
        }

        val address = nominatimRepository.getAddress(location.latitude, location.longitude)

        dao.updateDeviceLocation(
            newLatitude,
            newLongitude,
            address?.city ?: "$newLatitude, $newLongitude",
            address?.country ?: "",
            address?.countryCode ?: getCountryCode(context, location.latitude, location.longitude)
            ?: "",
            ZoneId.systemDefault().id,
        )

        return dao.getDeviceLocation()?.toDomain()
    }

    suspend fun saveDeviceLocation() {
        val location = suspendCancellableCoroutine { cont ->
            getDeviceLocation.getDeviceLocation(
                context,
                onTimeout = {
                    callback.onTimeout(cont)
                }) { result ->
                callback.onSuccess(cont, result)
            }
        }

        if (location.latitude == null || location.longitude == null) {
            throw AppException.CurrentLocationUnavailable()
        }

        val address = nominatimRepository.getAddress(location.latitude, location.longitude)

        if (address != null && address.city != null) {
            saveLocation(
                location.toDomain(context).copy(
                    country = address.country,
                    countryCode = address.countryCode,
                    name = address.city,
                )
            )
        } else {
            saveLocation(
                location.toDomain(context)
            )
        }
    }

    fun getWeatherForAllLocations(): Flow<List<Weather>> {
        return dao.getAllLocationsCurrentWeather()
            .map { list -> list.map { it.toDomain() } }
    }
}
