package com.pranshulgg.weather_master_app.data.local.entity.weather.units

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindUnit

@Entity("weather_units")
data class AppWeatherUnitsEntity(
    @PrimaryKey val id: Int = 1,
    val distance: DistanceUnit = DistanceUnit.MI,
    val precipitation: PrecipitationUnit = PrecipitationUnit.IN,
    val pressure: PressureUnit = PressureUnit.INHG,
    val speed: WindUnit = WindUnit.MPH,
    val temperature: TemperatureUnit = TemperatureUnit.FAHRENHEIT,
)
