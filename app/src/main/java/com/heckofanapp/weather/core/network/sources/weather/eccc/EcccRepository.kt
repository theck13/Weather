package com.heckofanapp.weather.core.network.sources.weather.eccc

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.weather.WeatherResult
import com.heckofanapp.weather.core.model.weather.WeatherResultType
import com.heckofanapp.weather.core.utils.weather.cache.isWeatherCacheSafe
import com.heckofanapp.weather.core.utils.weather.cache.shouldReturnWeatherCache
import com.heckofanapp.weather.data.local.dao.location.LocationsDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherDao
import com.heckofanapp.weather.data.local.mapper.weather.sources.eccc.toDomain
import com.heckofanapp.weather.data.local.mapper.weather.toCurrentWeatherEntity
import com.heckofanapp.weather.data.local.mapper.weather.toDailyWeatherEntity
import com.heckofanapp.weather.data.local.mapper.weather.toDomain
import com.heckofanapp.weather.data.local.mapper.weather.toHourlyWeatherEntity
import com.heckofanapp.weather.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

class EcccRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: EcccApi,
) : WeatherRepository {
    override suspend fun getWeather(
        isForceRefresh: Boolean,
        isManualRefresh: Boolean,
        location: Location,
    ): WeatherResult = withContext(Dispatchers.IO) {
        val cache = dao.getWeatherDataForLocation(location.id)
        val shouldReturnCache = shouldReturnWeatherCache(cache, isManualRefresh, isForceRefresh)

        when (shouldReturnCache) {
            WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable(cache?.toDomain())
            WeatherResultType.SUCCESS -> return@withContext (WeatherResult.Success(cache!!.toDomain()))
            else -> {}
        }

        return@withContext try {
            val response = api.fetchWeather(location.latitude, location.longitude)
            val body =
                response.body()?.firstOrNull()
                    ?: return@withContext WeatherResult.Error(exception = UnknownHostException())
            val domain = body.toDomain(location)

            weatherDao.insertWeather(
                domain.current.toCurrentWeatherEntity(location.id),
                domain.hourly.toHourlyWeatherEntity(location.id),
                domain.daily.toDailyWeatherEntity(location.id),
                location.id
            )
            WeatherResult.Success(domain)

        } catch (e: Exception) {
            WeatherResult.Error(
                cacheWeather = if (isWeatherCacheSafe(cache)) cache?.toDomain() else null,
                exception = e,
            )
        }
    }
}
