package com.pranshulgg.weather_master_app.core.model.sources

enum class WeatherSource(
    val displayName: String,
    val hourlyAggregationLimitHours: Int = 24,
    val displayLink: String,
    val fullName: String,
) {
    OPEN(
        displayLink = "https://open-meteo.com/",
        displayName = "Open Meteo (Global)",
        fullName = "Open Meteo"
    ),
    BMKG(
        displayLink = "https://www.bmkg.go.id/",
        displayName = "BMKG (Indonesia)",
        fullName = "Badan Meteorologi, Klimatologi, dan Geofisika"
    ),
    CNEMC(
        displayLink = "https://www.cnemc.cn/",
        displayName = "CNEMC (China)",
        fullName = "China National Environmental Monitoring Centre"
    ),
    DWD(
        displayLink = "https://brightsky.dev",
        displayName = "DWD (Germany)",
        fullName = "Bright Sky DWD"
    ),
    ECCC(
        displayLink = "https://app.weather.gc.ca/",
        displayName = "ECCC (Canada)",
        fullName = "Environment and Climate Change Canada",
    ),
    FMI(
        displayLink = "https://en.ilmatieteenlaitos.fi/",
        displayName = "FMI (Finland)",
        fullName = "Finnish Meteorological Institute"
    ),
    NWS(
        displayLink = "https://www.weather.gov/documentation/services-web-api",
        displayName = "NWS (United States)",
        fullName = "National Weather Service"
    ),
    MET(
        displayLink = "https://api.met.no/",
        displayName = "Met (Norway)",
        fullName = "Met Norway"
    ),
    METEO(
        displayLink = "https://meteofrance.com/",
        displayName = "Météo (France)",
        fullName = "Météo-France"
    ),
    SMHI(
        displayLink = "https://opendata.smhi.se",
        displayName = "SMHI (Sweden)",
        fullName = "Swedish Meteorological and Hydrological Institute"
    );

    // Sources that provide snow/rain as precipitation.
    fun providesSnowFall(): Boolean {
        return when (this) {
            BMKG,
            CNEMC,
            DWD,
            MET -> false
            else -> true
        }
    }
}

// Map every weather source here as they are added.
private val weatherSourcesByCountry = mapOf(
    "CA" to listOf(WeatherSource.ECCC),
    "CN" to listOf(WeatherSource.CNEMC),
    "DE" to listOf(WeatherSource.DWD),
    "FI" to listOf(WeatherSource.FMI),
    "ID" to listOf(WeatherSource.BMKG),
    "SE" to listOf(WeatherSource.SMHI),
    "US" to listOf(WeatherSource.NWS),
)

fun getWeatherSourcesForCountry(
    countryCode: String?,
): List<WeatherSource> {
    return weatherSourcesByCountry[countryCode] ?: emptyList()
}

// Global Source
private val weatherSourcesGlobal = listOf(
    WeatherSource.MET,
    WeatherSource.METEO,
    WeatherSource.OPEN,
)

fun getWeatherSourcesGlobal(): List<WeatherSource> {
    return weatherSourcesGlobal
}
