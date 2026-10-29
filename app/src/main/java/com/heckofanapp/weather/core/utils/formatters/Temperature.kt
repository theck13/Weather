package com.heckofanapp.weather.core.utils.formatters

/**
 * Shown in place of temperature when it is unknown.
 */
const val TEMPERATURE_UNKNOWN = "—"

/**
 * Temperature with degree sign, or [TEMPERATURE_UNKNOWN] with degree sign when it is null.
 */
fun Int?.toTemperatureString(): String = "${this ?: TEMPERATURE_UNKNOWN}°"
