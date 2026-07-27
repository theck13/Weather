package com.heckofanapp.weather.core.network.sources.weather.dwd

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.weather.WeatherResult
import com.heckofanapp.weather.core.model.weather.WeatherResultType
import com.heckofanapp.weather.core.network.sources.weather.dwd.json.bundle.DwdWeatherJsonBundle
import com.heckofanapp.weather.core.utils.formatters.safeZoneId
import com.heckofanapp.weather.core.utils.weather.cache.isWeatherCacheSafe
import com.heckofanapp.weather.core.utils.weather.cache.shouldReturnWeatherCache
import com.heckofanapp.weather.data.local.dao.location.LocationsDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherDao
import com.heckofanapp.weather.data.local.mapper.weather.sources.dwd.toDomain
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
import java.time.LocalDate
import javax.inject.Inject

class DwdRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: DwdApi,
) : WeatherRepository {
    override suspend fun getWeather(
        isForceRefresh: Boolean,
        isManualRefresh: Boolean,
        location: Location,
    ): WeatherResult = withContext(Dispatchers.IO) {
        val cache = dao.getWeatherDataForLocation(location.id)
        val shouldReturnCache = shouldReturnWeatherCache(cache, isManualRefresh, isForceRefresh)

        when (shouldReturnCache) {
            WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable(cache!!.toDomain())
            WeatherResultType.SUCCESS -> return@withContext (WeatherResult.Success(cache!!.toDomain()))
            else -> {}
        }

        return@withContext try {
            coroutineScope {
                // Current and forecast only need coordinates and are independent of
                // each other.  Fan them out concurrently instead of awaiting ech in turn.
                val dates = getStartEndDate(location)
                val currentDeferred = async {
                    api.fetchCurrentWeather(location.latitude, location.longitude)
                }
                val forecastDeferred = async {
                    api.fetchWeatherForecast(
                        location.latitude, location.longitude, dates.first, dates.second
                    )
                }

                val body = currentDeferred.await().body()
                    ?: return@coroutineScope WeatherResult.Error(exception = UnknownHostException())
                val forecastBody = forecastDeferred.await().body()
                    ?: return@coroutineScope WeatherResult.Error(exception = UnknownHostException())
                val final = DwdWeatherJsonBundle(
                    current = body,
                    forecastJson = forecastBody,
                )
                val domain = final.toDomain(location)

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

private fun getStartEndDate(location: Location): Pair<String, String> {
    val zoneId = safeZoneId(
        id = location.timezone,
    )
    val start = LocalDate.now(zoneId)
    val end = start.plusDays(5)
    return Pair(start.toString(), end.toString())
}
