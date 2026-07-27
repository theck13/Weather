package com.heckofanapp.weather.core.network.sources.weather.nws

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.weather.WeatherResult
import com.heckofanapp.weather.core.model.weather.WeatherResultType
import com.heckofanapp.weather.core.network.sources.weather.nws.json.NwsCurrentForecastJson
import com.heckofanapp.weather.core.network.sources.weather.nws.json.NwsStationsListJson
import com.heckofanapp.weather.core.network.sources.weather.nws.json.bundle.NwsWeatherJsonBundle
import com.heckofanapp.weather.core.network.sources.weather.openmeteo.OpenMeteoApi
import com.heckofanapp.weather.core.utils.weather.cache.isWeatherCacheSafe
import com.heckofanapp.weather.core.utils.weather.cache.shouldReturnWeatherCache
import com.heckofanapp.weather.data.local.dao.location.LocationsDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherDao
import com.heckofanapp.weather.data.local.dao.weather.nws.NwsDao
import com.heckofanapp.weather.data.local.mapper.weather.sources.nws.toDomain
import com.heckofanapp.weather.data.local.mapper.weather.sources.nws.toEntity
import com.heckofanapp.weather.data.local.mapper.weather.sources.nws.toNwsSupplemental
import com.heckofanapp.weather.data.local.mapper.weather.toCurrentWeatherEntity
import com.heckofanapp.weather.data.local.mapper.weather.toDailyWeatherEntity
import com.heckofanapp.weather.data.local.mapper.weather.toDomain
import com.heckofanapp.weather.data.local.mapper.weather.toHourlyWeatherEntity
import com.heckofanapp.weather.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

class NwsRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val nwsDao: NwsDao,
    val api: NwsApi,
    val openMeteoApi: OpenMeteoApi,
) : WeatherRepository {
    override suspend fun getWeather(
        isForceRefresh: Boolean,
        isManualRefresh: Boolean,
        location: Location,
    ): WeatherResult = withContext(Dispatchers.IO) {
        val cache = dao.getWeatherDataForLocation(location.id)
        val cachedGridPointsData = nwsDao.getGridPointsForLocation(location.id)
        val shouldReturnCache = shouldReturnWeatherCache(cache, isManualRefresh, isForceRefresh)

        when (shouldReturnCache) {
            WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable(cache!!.toDomain())
            WeatherResultType.SUCCESS -> return@withContext (WeatherResult.Success(cache!!.toDomain()))
            else -> {}
        }

        /**
         * NWS has everything as separate endpoints.  Makes it annoying to get data.
         * Grid points and station discovery is an inherent dependency chain and is
         * cached, so it only runs on first fetch.  Everything after it only needs
         * grid coordinates (and station id).  Calls are fanned out concurrently
         * instead of awaited one at a time.
         */
        return@withContext try {
            coroutineScope {
                // NWS provides no ultraviolet and no daily/hourly pressure.  Backfill from
                // Open Meteo best-effort.  Failure here must not fail whole NWS result.
                // Depends only on the location, so start it immediately, overlapping the
                // grid points / station discovery below.
                val supplementalDeferred = async {
                    runCatching {
                        openMeteoApi.fetchWeather(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            timezone = location.timezone,
                        ).body()
                    }.getOrNull()?.toNwsSupplemental(location.timezone)
                }

                var currentObservation: NwsCurrentForecastJson? = null
                val nwsStationsDomain = if (cachedGridPointsData != null) {
                    cachedGridPointsData.toDomain()
                } else {
                    val gridPointsResponse = api.fetchGridPoints(
                        location.latitude,
                        location.longitude
                    )
                    val gridPointsBody = gridPointsResponse.body()
                        ?: return@coroutineScope WeatherResult.Error(exception = UnknownHostException())

                    val gridPointsDomain =
                        gridPointsBody.toDomain(location, stationIdentifier = null)

                    val nwsStationsResponse = api.fetchStations(
                        gridPointsDomain.officeId,
                        gridPointsDomain.gridX,
                        gridPointsDomain.gridY
                    )

                    val nwsStationsBody = nwsStationsResponse.body()
                        ?: return@coroutineScope WeatherResult.Error(exception = UnknownHostException())

                    // Get all the stations
                    val stations = nwsStationsBody.features
                    val station = getValidObservationAndStation(stations, api)
                    // New domain with stationIdentifier
                    val domain = gridPointsDomain.copy(
                        stationIdentifier = station?.first,
                    )

                    if (domain.stationIdentifier == null) {
                        return@coroutineScope WeatherResult.Error(
                            exception = UnknownHostException(),
                        )
                    }

                    currentObservation = station?.second
                    domain
                }

                // These only need grid coordinates (and station id), so fetch them
                // concurrently rather than awaiting each in turn.
                val forecastDeferred = async {
                    api.fetchForecast(
                        nwsStationsDomain.officeId,
                        nwsStationsDomain.gridX,
                        nwsStationsDomain.gridY
                    )
                }
                val hourlyDeferred = async {
                    api.fetchHourlyForecast(
                        nwsStationsDomain.officeId,
                        nwsStationsDomain.gridX,
                        nwsStationsDomain.gridY
                    )
                }
                // USING FOR QuantitativePrecipitation and Snowfall
                val gridPointDataDeferred = async {
                    api.fetchGridPointData(
                        nwsStationsDomain.officeId,
                        nwsStationsDomain.gridX,
                        nwsStationsDomain.gridY
                    )
                }
                val currentDeferred = async {
                    currentObservation
                        ?: api.fetchCurrentForecast(nwsStationsDomain.stationIdentifier!!).body()
                }

                // GET DAILY
                val nwsForecastBody = forecastDeferred.await().body()
                    ?: return@coroutineScope WeatherResult.Error(exception = UnknownHostException())
                // GET CURRENT
                val nwsCurrentForecastBody = currentDeferred.await()
                    ?: return@coroutineScope WeatherResult.Error(exception = UnknownHostException())
                // GET HOURLY
                val nwsHourlyForecastBody = hourlyDeferred.await().body()
                    ?: return@coroutineScope WeatherResult.Error(exception = UnknownHostException())
                val nwsGridPointDataBody = gridPointDataDeferred.await().body()
                    ?: return@coroutineScope WeatherResult.Error(exception = UnknownHostException())

                // PUT EVERYTHING TOGETHER IN A BUNDLE
                val final = NwsWeatherJsonBundle(
                    current = nwsCurrentForecastBody,
                    forecast = nwsForecastBody,
                    hourly = nwsHourlyForecastBody,
                    gridPointsData = nwsGridPointDataBody,
                )

                val supplemental = supplementalDeferred.await()

                val domain = final.toDomain(location, supplemental)

                nwsDao.insertLocationGridPoints(nwsStationsDomain.toEntity(location))
                weatherDao.insertWeather(
                    currentWeather = domain.current.toCurrentWeatherEntity(location.id),
                    dailyWeather = domain.daily.toDailyWeatherEntity(location.id),
                    hourlyWeather = domain.hourly.toHourlyWeatherEntity(location.id),
                    id = location.id
                )

                WeatherResult.Success(domain)
            }
        } catch (e: Exception) {
            WeatherResult.Error(
                cacheWeather = if (isWeatherCacheSafe(cache)) cache?.toDomain() else null,
                exception = e,
            )
        }
    }
}

/**
 * Find a working station
 * Sometimes NWS returns empty pages or "Not Found" error
 */
private suspend fun getValidObservationAndStation(
    stations: List<NwsStationsListJson>,
    api: NwsApi,
): Pair<String, NwsCurrentForecastJson?>? {
    for (feature in stations) {
        val stationId = feature.properties.stationIdentifier

        try {
            val response = api.fetchCurrentForecast(stationId)

            if (response.isSuccessful && response.body() != null) {
                return Pair(stationId, response.body())
            }
        } catch (_: Exception) {
        }
    }

    return null
}
