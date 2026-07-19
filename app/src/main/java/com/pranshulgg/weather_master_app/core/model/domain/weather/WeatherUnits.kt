package com.pranshulgg.weather_master_app.core.model.domain.weather

import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindUnit

data class WeatherUnits(
    val distance: DistanceUnit,
    val precipitation: PrecipitationUnit,
    val pressure: PressureUnit,
    val speed: WindUnit,
    val temperature: TemperatureUnit,
) {
    companion object {
        fun getDefault() = WeatherUnits(
            distance = DistanceUnit.MI,
            precipitation = PrecipitationUnit.IN,
            pressure = PressureUnit.INHG,
            speed = WindUnit.MPH,
            temperature = TemperatureUnit.FAHRENHEIT,
        )
    }
}
