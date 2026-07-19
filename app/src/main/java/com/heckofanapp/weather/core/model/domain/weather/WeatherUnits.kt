package com.heckofanapp.weather.core.model.domain.weather

import com.heckofanapp.weather.core.model.weather.DistanceUnit
import com.heckofanapp.weather.core.model.weather.PrecipitationUnit
import com.heckofanapp.weather.core.model.weather.PressureUnit
import com.heckofanapp.weather.core.model.weather.TemperatureUnit
import com.heckofanapp.weather.core.model.weather.WindUnit

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
