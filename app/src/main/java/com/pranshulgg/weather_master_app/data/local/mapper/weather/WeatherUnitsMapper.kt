package com.pranshulgg.weather_master_app.data.local.mapper.weather

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.data.local.entity.weather.units.AppWeatherUnitsEntity

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
