package com.heckofanapp.weather.data.local.mapper.weather

import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.data.local.entity.weather.units.AppWeatherUnitsEntity

fun AppWeatherUnitsEntity.toDomain(): WeatherUnits =
    WeatherUnits(
        distance = distance,
        precipitation = precipitation,
        pressure = pressure,
        speed = speed,
        temperature = temperature,
    )

fun WeatherUnits.toEntity(): AppWeatherUnitsEntity =
    AppWeatherUnitsEntity(
        distance = distance,
        precipitation = precipitation,
        pressure = pressure,
        speed = speed,
        temperature = temperature,
    )
