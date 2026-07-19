package com.heckofanapp.weather.data.local.dao.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.data.local.entity.location.WeatherLocationEntity
import com.heckofanapp.weather.data.local.entity.weather.WeatherWithRelations
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationsDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertWeatherLocation(
        weatherLocation: WeatherLocationEntity
    )

    @Query("SELECT * FROM weather_locations ORDER BY sortOrder ASC")
    fun getLocations(): Flow<List<WeatherLocationEntity>>

    @Query("DELETE FROM weather_locations WHERE id = :id")
    suspend fun deleteLocation(id: String)

    @Query("UPDATE weather_locations SET isDefault = 0")
    suspend fun clearDefaultLocations()

    @Query("UPDATE weather_locations SET isDefault = 1 WHERE id = :id")
    suspend fun updateDefaultLocation(id: String)

    @Query("SELECT COUNT(*) FROM weather_locations")
    suspend fun getLocationsCount(): Int

    @Query("SELECT * FROM weather_locations WHERE isDefault = 1 LIMIT 1")
    fun getDefaultLocation(): Flow<WeatherLocationEntity?>

    @Query("UPDATE weather_locations SET lat = :lat, lon = :lon, name = :name, country = :country, countryCode = :countryCode, timezone = :timezone WHERE isDeviceLocation = 1")
    suspend fun updateDeviceLocation(
        lat: Double,
        lon: Double,
        name: String,
        country: String,
        countryCode: String,
        timezone: String
    )

    @Transaction
    @Query("SELECT * FROM weather_locations WHERE id = :locationId LIMIT 1")
    suspend fun getWeatherForLocation(locationId: String): WeatherWithRelations

    @Transaction
    @Query("SELECT * FROM weather_locations WHERE id = :locationId")
    suspend fun getWeatherDataForLocation(locationId: String): WeatherWithRelations?

    @Transaction
    @Query("SELECT * FROM weather_locations")
    fun getAllLocationsCurrentWeather(): Flow<List<WeatherWithRelations>>

    @Query("UPDATE weather_locations SET source = :source WHERE id = :id")
    suspend fun updateSourceForLocation(id: String, source: WeatherSource)

    @Query("UPDATE weather_locations SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateLocationOrder(id: String, sortOrder: Int)

    @Query("DELETE FROM weather_daily WHERE locationId = :id")
    suspend fun deleteDailyDataForLocation(id: String)

    @Query("DELETE FROM weather_hourly WHERE locationId = :id")
    suspend fun deleteHourlyDataForLocation(id: String)

    @Query("SELECT * FROM weather_locations ORDER BY sortOrder ASC")
    suspend fun getLocationsOnce(): List<WeatherLocationEntity>

    @Query("SELECT * FROM weather_locations WHERE isDeviceLocation = 1 LIMIT 1")
    suspend fun getDeviceLocation(): WeatherLocationEntity?
}