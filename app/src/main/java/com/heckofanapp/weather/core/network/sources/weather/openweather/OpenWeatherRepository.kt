package com.heckofanapp.weather.core.network.sources.weather.openweather

import android.util.Log
import com.heckofanapp.weather.core.model.domain.AppException
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.weather.WeatherResult
import com.heckofanapp.weather.core.model.weather.WeatherResultType
import com.heckofanapp.weather.core.network.sources.weather.openmeteo.OpenMeteoApi
import com.heckofanapp.weather.core.utils.weather.cache.isWeatherCacheSafe
import com.heckofanapp.weather.core.utils.weather.cache.shouldReturnWeatherCache
import com.heckofanapp.weather.data.local.dao.location.LocationsDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherDao
import com.heckofanapp.weather.data.local.mapper.weather.sources.openweather.toDomain
import com.heckofanapp.weather.data.local.mapper.weather.sources.openweather.toOpenWeatherSupplemental
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

class OpenWeatherRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: OpenWeatherApi,
    val openMeteoApi: OpenMeteoApi,
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
                    // OpenWeather provides no daily visibility.  Backfill from Open Meteo
                    // best-effort.  Failure here must not fail the whole OpenWeather result.
                    // Depends only on the location, so start it immediately, overlapping the
                    // call below.
                    val supplementalDeferred = async {
                        runCatching {
                            openMeteoApi.fetchWeather(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                timezone = location.timezone,
                            ).body()
                        }.getOrNull()?.toOpenWeatherSupplemental(location.timezone)
                    }

                    // Current, hourly, and daily all arrive in single response.
                    val response = api.fetchWeather(
                        latitude = location.latitude,
                        longitude = location.longitude,
                    )
                    val body = response.body()
                        ?: run {
                            Log.e(
                                TAG,
                                "Refresh failed: ${response.code()} ${response.errorBody()?.string()}",
                            )

                            return@coroutineScope WeatherResult.Error(
                                exception = AppException.Unknown(),
                            )
                        }

                    val supplemental = supplementalDeferred.await()
                    val domain = body.toDomain(location, supplemental)

                    weatherDao.insertWeather(
                        currentWeather = domain.current.toCurrentWeatherEntity(location.id),
                        dailyWeather = domain.daily.toDailyWeatherEntity(location.id),
                        hourlyWeather = domain.hourly.toHourlyWeatherEntity(location.id),
                        id = location.id,
                    )
                    WeatherResult.Success(domain)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Refresh failed for location ${location.id}", e)

                val isCacheSafe = isWeatherCacheSafe(cache)

                WeatherResult.Error(
                    cacheWeather = if (isCacheSafe) cache?.toDomain() else null,
                    exception = e,
                )
            }
        }

    companion object {
        private const val TAG = "OpenWeatherRepository"
    }
}
