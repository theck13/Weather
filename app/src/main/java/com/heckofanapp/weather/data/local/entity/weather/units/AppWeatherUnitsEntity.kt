package com.heckofanapp.weather.data.local.entity.weather.units

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.heckofanapp.weather.core.model.weather.DistanceUnit
import com.heckofanapp.weather.core.model.weather.PrecipitationUnit
import com.heckofanapp.weather.core.model.weather.PressureUnit
import com.heckofanapp.weather.core.model.weather.TemperatureUnit
import com.heckofanapp.weather.core.model.weather.WindUnit

@Entity("weather_units")
data class AppWeatherUnitsEntity(
    @PrimaryKey val id: Int = 1,
    val distance: DistanceUnit = DistanceUnit.MI,
    val precipitation: PrecipitationUnit = PrecipitationUnit.IN,
    val pressure: PressureUnit = PressureUnit.INHG,
    val speed: WindUnit = WindUnit.MPH,
    val temperature: TemperatureUnit = TemperatureUnit.FAHRENHEIT,
)
