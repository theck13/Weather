package com.pranshulgg.weather_master_app.data.local.entity.airquality

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity

@Entity(
    tableName = "air_quality_hourly", foreignKeys = [
        ForeignKey(
            childColumns = ["locationId"],
            entity = WeatherLocationEntity::class,
            parentColumns = ["id"],
            onDelete = ForeignKey.Companion.CASCADE,
        )
    ],
    indices = [Index("locationId")],
    primaryKeys = ["locationId", "time"],
)
data class HourlyAirQualityEntity(
    val carbonMonoxide: Double?,
    val locationId: String,
    val nitrogenDioxide: Double?,
    val ozone: Double?,
    val pm10: Double?,
    val pm25: Double?,
    val sulphurDioxide: Double?,
    val time: Long,
    val usAqi: Int?,
)
