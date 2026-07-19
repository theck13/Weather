package com.heckofanapp.weather.core.model.weather.air

/**
 * Which air-quality index a location uses.
 * US reports EPA AirNow AQI; everywhere else uses European-style index.
 */
enum class AirQualityIndexStandard {
    EUROPEAN,
    US;

    companion object {
        fun forCountryCode(
            countryCode: String?,
        ): AirQualityIndexStandard =
            if (countryCode.equals(
                    ignoreCase = true,
                    other = "US",
                )
            ) {
                US
            } else {
                EUROPEAN
            }
    }
}
