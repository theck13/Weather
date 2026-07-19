package com.heckofanapp.weather.data.repository

import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.core.model.weather.DistanceUnit
import com.heckofanapp.weather.core.model.weather.PrecipitationUnit
import com.heckofanapp.weather.core.model.weather.PressureUnit
import com.heckofanapp.weather.core.model.weather.TemperatureUnit
import com.heckofanapp.weather.core.model.weather.WindUnit
import com.heckofanapp.weather.data.local.dao.weather.WeatherUnitsDao
import com.heckofanapp.weather.data.local.mapper.weather.toDomain
import com.heckofanapp.weather.data.local.mapper.weather.toEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class WeatherUnitsRepository @Inject constructor(
    private val dao: WeatherUnitsDao,
) {
    suspend fun ensureDefaultExists() {
        if (dao.getOnce() == null) {
            dao.insert(WeatherUnits.getDefault().toEntity())
        }
    }

    fun getUnits(): Flow<WeatherUnits> = flow {
        ensureDefaultExists()
        emitAll(
            dao.getWeatherUnits().map { it!!.toDomain() }
        )
    }

    suspend fun getUnitsOnce(): WeatherUnits? {
        return dao.getWeatherUnitsOnce()?.toDomain()
    }

    suspend fun updateDistanceUnit(
        distanceUnit: DistanceUnit,
    ) {
        dao.updateDistanceUnit(
            distanceUnit = distanceUnit,
        )
    }

    suspend fun updatePrecipitationUnit(
        precipitationUnit: PrecipitationUnit,
    ) {
        dao.updatePrecipitationUnit(
            precipitationUnit = precipitationUnit,
        )
    }

    suspend fun updatePressureUnit(
        pressureUnit: PressureUnit,
    ) {
        dao.updatePressureUnit(
            pressureUnit = pressureUnit,
        )
    }

    suspend fun updateTemperatureUnit(
        temperatureUnit: TemperatureUnit,
    ) {
        dao.updateTemperatureUnit(
            temperatureUnit = temperatureUnit,
        )
    }

    suspend fun updateWindUnit(
        windUnit: WindUnit,
    ) {
        dao.updateWindSpeedUnit(
            windUnit = windUnit,
        )
    }
}
