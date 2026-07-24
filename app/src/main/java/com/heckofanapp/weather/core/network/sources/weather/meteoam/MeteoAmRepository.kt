package com.heckofanapp.weather.core.network.sources.weather.meteoam

import com.heckofanapp.weather.core.model.domain.AppException
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.weather.WeatherResult
import com.heckofanapp.weather.core.model.weather.WeatherResultType
import com.heckofanapp.weather.core.network.sources.weather.meteoam.json.bundle.MeteoAmWeatherBundle
import com.heckofanapp.weather.core.utils.weather.cache.shouldReturnWeatherCache
import com.heckofanapp.weather.data.local.dao.location.LocationsDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherDao
import com.heckofanapp.weather.data.local.mapper.weather.sources.meteoam.toDomain
import com.heckofanapp.weather.data.local.mapper.weather.toCurrentWeatherEntity
import com.heckofanapp.weather.data.local.mapper.weather.toDailyWeatherEntity
import com.heckofanapp.weather.data.local.mapper.weather.toDomain
import com.heckofanapp.weather.data.local.mapper.weather.toHourlyWeatherEntity
import com.heckofanapp.weather.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MeteoAmRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: MeteoAmApi,
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
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable()
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
                else -> {}
            }

            val current = api.fetchCurrent(location.latitude, location.longitude)
            val bodyCurrent = current.body()
                ?: return@withContext WeatherResult.Error(
                    exception = AppException.Unknown(),
                )
            val forecast = api.fetchForecast(location.latitude, location.longitude)
            val bodyForecast = forecast.body()
                ?: return@withContext WeatherResult.Error(
                    exception = AppException.Unknown(),
                )
            val final = MeteoAmWeatherBundle(
                current = bodyCurrent,
                forecast = bodyForecast,
            )

            val domain = final.toDomain(location)

            weatherDao.insertWeather(
                domain.current.toCurrentWeatherEntity(location.id),
                domain.hourly.toHourlyWeatherEntity(location.id),
                domain.daily.toDailyWeatherEntity(location.id),
                location.id,
            )
            WeatherResult.Success(domain)
        }
}
