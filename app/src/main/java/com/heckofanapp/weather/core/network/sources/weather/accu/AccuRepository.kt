package com.heckofanapp.weather.core.network.sources.weather.accu

import com.heckofanapp.weather.core.model.domain.AppException
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.weather.WeatherResult
import com.heckofanapp.weather.core.model.weather.WeatherResultType
import com.heckofanapp.weather.core.network.sources.weather.accu.json.bundle.AccuWeatherBundle
import com.heckofanapp.weather.core.utils.weather.cache.isWeatherCacheSafe
import com.heckofanapp.weather.core.utils.weather.cache.shouldReturnWeatherCache
import com.heckofanapp.weather.data.local.dao.location.LocationsDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherDao
import com.heckofanapp.weather.data.local.mapper.weather.sources.accu.toDomain
import com.heckofanapp.weather.data.local.mapper.weather.toCurrentWeatherEntity
import com.heckofanapp.weather.data.local.mapper.weather.toDailyWeatherEntity
import com.heckofanapp.weather.data.local.mapper.weather.toDomain
import com.heckofanapp.weather.data.local.mapper.weather.toHourlyWeatherEntity
import com.heckofanapp.weather.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccuRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: AccuApi,
) : WeatherRepository {
    override suspend fun getWeather(
        isForceRefresh: Boolean,
        isManualRefresh: Boolean,
        location: Location,
    ): WeatherResult =
        withContext(
            Dispatchers.IO
        ) {
            val cache = dao.getWeatherDataForLocation(location.id)
            val shouldReturnCache = shouldReturnWeatherCache(cache, isManualRefresh, isForceRefresh)

            when (shouldReturnCache) {
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable(cache!!.toDomain())
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
                else -> {}
            }

            return@withContext try {
                coroutineScope {
                    // Location key must be resolved first.  The current/hourly/daily
                    // calls all depend on it, but are independent of each other.  So,
                    // fan them out concurrently instead of awaiting each in turn.
                    val locationKey = api.getLocationKey("${location.latitude},${location.longitude}")
                    val bodyLocation = locationKey.body()
                        ?: return@coroutineScope WeatherResult.Error(exception = AppException.Unknown())

                    val currentDeferred = async { api.fetchCurrent(bodyLocation.key) }
                    val hourlyDeferred = async { api.fetchHourly(bodyLocation.key) }
                    val dailyDeferred = async { api.fetchDaily(bodyLocation.key) }

                    val bodyCurrent = currentDeferred.await().body()
                        ?: return@coroutineScope WeatherResult.Error(exception = AppException.Unknown())
                    val bodyHourly = hourlyDeferred.await().body()
                        ?: return@coroutineScope WeatherResult.Error(exception = AppException.Unknown())
                    val bodyDaily = dailyDeferred.await().body()
                        ?: return@coroutineScope WeatherResult.Error(exception = AppException.Unknown())
                    val final = AccuWeatherBundle(
                        current = bodyCurrent[0],
                        daily = bodyDaily,
                        hourly = bodyHourly,
                    )

                    val domain = final.toDomain(location)

                    weatherDao.insertWeather(
                        currentWeather = domain.current.toCurrentWeatherEntity(location.id),
                        dailyWeather = domain.daily.toDailyWeatherEntity(location.id),
                        hourlyWeather = domain.hourly.toHourlyWeatherEntity(location.id),
                        id = location.id,
                    )
                    WeatherResult.Success(domain)
                }
            } catch (e: Exception) {
                val isCacheSafe = isWeatherCacheSafe(cache)

                WeatherResult.Error(
                    cacheWeather = if (isCacheSafe) cache?.toDomain() else null,
                    exception = e,
                )
            }
        }
}
