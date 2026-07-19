package com.heckofanapp.weather.data.local.entity.weather

import androidx.room.Embedded
import androidx.room.Relation
import com.heckofanapp.weather.data.local.entity.location.WeatherLocationEntity

data class WeatherWithRelations(
    @Embedded val location: WeatherLocationEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val current: CurrentWeatherEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val hourly: List<HourlyWeatherEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val daily: List<DailyWeatherEntity>
)