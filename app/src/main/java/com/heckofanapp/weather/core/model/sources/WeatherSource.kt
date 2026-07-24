package com.heckofanapp.weather.core.model.sources

enum class WeatherSource(
    val displayName: String,
    val hourlyAggregationLimitHours: Int = 24,
    val displayLink: String,
    val fullName: String,
) {
    ACCU(
        displayLink = "https://www.accuweather.com/",
        displayName = "AccuWeather (Global)",
        fullName = "AccuWeather"
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
    METEOAM(
        displayLink = "https://www.meteoam.it/",
        displayName = "Meteo AM (Italy)",
        fullName = "Meteorologia Aeronautica Militare"
    ),
    OPEN(
        displayLink = "https://open-meteo.com/",
        displayName = "Open Meteo (Global)",
        fullName = "Open Meteo"
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
private val weatherSourcesByCountry = buildMap {
    put("CA", listOf(WeatherSource.ECCC))
    put("CN", listOf(WeatherSource.CNEMC))
    put("DE", listOf(WeatherSource.DWD))
    put("FI", listOf(WeatherSource.FMI))
    put("ID", listOf(WeatherSource.BMKG))
    put("SE", listOf(WeatherSource.SMHI))
    put("US", listOf(WeatherSource.NWS))
    listOf(
        "IT",
        "VA",
    ).forEach {
        put(it, listOf(WeatherSource.METEOAM))
    }
}

fun getWeatherSourcesForCountry(
    countryCode: String?,
): List<WeatherSource> {
    return weatherSourcesByCountry[countryCode] ?: emptyList()
}

// Global Source
private val weatherSourcesGlobal = listOf(
    WeatherSource.ACCU,
    WeatherSource.MET,
    WeatherSource.METEO,
    WeatherSource.OPEN,
)

fun getWeatherSourcesGlobal(): List<WeatherSource> {
    return weatherSourcesGlobal
}
