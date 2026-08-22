package com.heckofanapp.weather.core.network.sources.weather.google

import android.util.Log
import com.heckofanapp.weather.core.model.domain.AppException
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.weather.WeatherResult
import com.heckofanapp.weather.core.model.weather.WeatherResultType
import com.heckofanapp.weather.core.network.sources.weather.google.json.GoogleHourlyWeatherItemJson
import com.heckofanapp.weather.core.network.sources.weather.google.json.bundle.GoogleWeatherBundle
import com.heckofanapp.weather.core.utils.weather.cache.isWeatherCacheSafe
import com.heckofanapp.weather.core.utils.weather.cache.shouldReturnWeatherCache
import com.heckofanapp.weather.data.local.dao.location.LocationsDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherDao
import com.heckofanapp.weather.data.local.mapper.weather.sources.google.toDomain
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

class GoogleRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: GoogleApi,
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
                    // Every endpoint is keyed by plain coordinates, so nothing has to be
                    // resolved first.  Fan all three out concurrently.
                    val currentDeferred = async {
                        api.fetchCurrent(
                            latitude = location.latitude,
                            longitude = location.longitude,
                        )
                    }
                    val hourlyDeferred = async {
                        fetchHourlyPages(location)
                    }
                    val dailyDeferred = async {
                        api.fetchDaily(
                            latitude = location.latitude,
                            longitude = location.longitude,
                        )
                    }

                    val responseCurrent = currentDeferred.await()
                    val bodyCurrent = responseCurrent.body()
                        ?: run {
                            Log.e(
                                TAG,
                                "Current failed: ${responseCurrent.code()} ${responseCurrent.errorBody()?.string()}",
                            )

                            return@coroutineScope WeatherResult.Error(
                                exception = AppException.Unknown(),
                            )
                        }
                    val bodyHourly = hourlyDeferred.await()
                        ?: run {
                            Log.e(TAG, "Hourly failed: no hours returned")

                            return@coroutineScope WeatherResult.Error(
                                exception = AppException.Unknown(),
                            )
                        }
                    val responseDaily = dailyDeferred.await()
                    val bodyDaily = responseDaily.body()
                        ?: run {
                            Log.e(
                                TAG,
                                "Daily failed: ${responseDaily.code()} ${responseDaily.errorBody()?.string()}",
                            )

                            return@coroutineScope WeatherResult.Error(
                                exception = AppException.Unknown(),
                            )
                        }
                    val final = GoogleWeatherBundle(
                        current = bodyCurrent,
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
                Log.e(TAG, "Refresh failed for location ${location.id}", e)

                val isCacheSafe = isWeatherCacheSafe(cache)

                WeatherResult.Error(
                    cacheWeather = if (isCacheSafe) cache?.toDomain() else null,
                    exception = e,
                )
            }
        }

    /**
     * Hourly caps page at 24 hours and each token is only known from previous response.
     * So, pages are walked in order until hour count is met.  Pages that fail part way
     * keep what came before and later days just lose their hourly breakdown instead of
     * failing whole refresh.
     */
    private suspend fun fetchHourlyPages(
        location: Location,
    ): List<GoogleHourlyWeatherItemJson>? {
        val hours = mutableListOf<GoogleHourlyWeatherItemJson>()
        var pageToken: String? = null

        while (hours.size < GoogleApi.HOURLY_HOURS) {
            val response = api.fetchHourly(
                latitude = location.latitude,
                longitude = location.longitude,
                pageToken = pageToken,
            )
            val body = response.body()

            if (body == null) {
                Log.e(
                    TAG,
                    "Hourly page failed at ${hours.size} hours: ${response.code()} ${response.errorBody()?.string()}",
                )

                break
            }

            if (body.forecastHours.isEmpty()) break

            hours.addAll(body.forecastHours)
            pageToken = body.nextPageToken ?: break
        }

        return hours.ifEmpty { null }
    }

    companion object {
        private const val TAG = "GoogleRepository"
    }
}
