package com.heckofanapp.weather.data.local.dao.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heckofanapp.weather.core.model.weather.DistanceUnit
import com.heckofanapp.weather.core.model.weather.PrecipitationUnit
import com.heckofanapp.weather.core.model.weather.PressureUnit
import com.heckofanapp.weather.core.model.weather.TemperatureUnit
import com.heckofanapp.weather.core.model.weather.WindUnit
import com.heckofanapp.weather.data.local.entity.weather.units.AppWeatherUnitsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherUnitsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherUnits: AppWeatherUnitsEntity)

    @Query("SELECT * FROM weather_units")
    fun getWeatherUnits(): Flow<AppWeatherUnitsEntity?>

    @Query("SELECT * FROM weather_units")
    suspend fun getWeatherUnitsOnce(): AppWeatherUnitsEntity?

    @Query("SELECT * FROM weather_units LIMIT 1")
    suspend fun getOnce(): AppWeatherUnitsEntity?

    @Query("UPDATE weather_units SET distance = :distanceUnit WHERE id = 1")
    suspend fun updateDistanceUnit(distanceUnit: DistanceUnit)

    @Query("UPDATE weather_units SET precipitation = :precipitationUnit WHERE id = 1")
    suspend fun updatePrecipitationUnit(precipitationUnit: PrecipitationUnit)

    @Query("UPDATE weather_units SET pressure = :pressureUnit WHERE id = 1")
    suspend fun updatePressureUnit(pressureUnit: PressureUnit)

    @Query("UPDATE weather_units SET temperature = :temperatureUnit WHERE id = 1")
    suspend fun updateTemperatureUnit(temperatureUnit: TemperatureUnit)

    @Query("UPDATE weather_units SET speed = :windUnit WHERE id = 1")
    suspend fun updateWindSpeedUnit(windUnit: WindUnit)
}
