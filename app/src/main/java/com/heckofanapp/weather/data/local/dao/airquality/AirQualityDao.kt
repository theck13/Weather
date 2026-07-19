package com.heckofanapp.weather.data.local.dao.airquality

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heckofanapp.weather.data.local.entity.airquality.AirQualityWithRelations
import com.heckofanapp.weather.data.local.entity.airquality.CurrentAirQualityEntity
import com.heckofanapp.weather.data.local.entity.airquality.HourlyAirQualityEntity

@Dao
interface AirQualityDao {
    @Transaction
    suspend fun insertAirQuality(
        currentAirQuality: CurrentAirQualityEntity,
        hourlyAirQuality: List<HourlyAirQualityEntity>,
        id: String,
    ) {
        deleteHourlyAirQualityForLocation(id)
        insertCurrentAirQuality(currentAirQuality)
        insertHourlyAirQuality(hourlyAirQuality)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentAirQuality(
        currentAirQuality: CurrentAirQualityEntity,
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyAirQuality(
        hourlyAirQuality: List<HourlyAirQualityEntity>,
    )

    @Transaction
    @Query("SELECT * FROM air_quality_current WHERE locationId = :locationId")
    suspend fun getAirQualityForLocation(
        locationId: String,
    ): AirQualityWithRelations?

    @Query("DELETE FROM air_quality_hourly WHERE locationId = :id")
    suspend fun deleteHourlyAirQualityForLocation(
        id: String,
    )

    @Query("DELETE FROM weather_locations WHERE id = :id")
    suspend fun deleteCurrentAirQuality(
        id: String,
    )
}
