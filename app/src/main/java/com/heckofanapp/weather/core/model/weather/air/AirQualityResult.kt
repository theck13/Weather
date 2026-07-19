package com.heckofanapp.weather.core.model.weather.air

import com.heckofanapp.weather.core.model.domain.airquality.AirQuality

sealed class AirQualityResult {
    data class Success(val airquality: AirQuality) : AirQualityResult()
    data class Error(val exception: Exception, val cacheAirQuality: AirQuality? = null) :
        AirQualityResult()
}

enum class AirQualityResultType {
    RETURN_CACHE,
    ERROR
}
