package com.pranshulgg.weather_master_app.core.network.sources.weather.dwd

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json.bundle.DwdWeatherJsonBundle
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.dwd.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
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
            WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable(cache?.toDomain())
            WeatherResultType.SUCCESS -> return@withContext (WeatherResult.Success(cache!!.toDomain()))
            else -> {}
        }

        return@withContext try {
            val response = api.fetchCurrentWeather(location.latitude, location.longitude)
            val dates = getStartEndDate(location)
            val forecastResponse = api.fetchWeatherForecast(
                location.latitude, location.longitude, dates.first, dates.second
            )
            val body = response.body()
                ?: return@withContext WeatherResult.Error(exception = UnknownHostException())
            val forecastBody = forecastResponse.body()
                ?: return@withContext WeatherResult.Error(exception = UnknownHostException())
            val final = DwdWeatherJsonBundle(
                current = body,
                forecastJson = forecastBody,
            )
            val domain = final.toDomain(location)

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

private fun getStartEndDate(location: Location): Pair<String, String> {
    val zoneId = safeZoneId(
        id = location.timezone,
    )
    val start = LocalDate.now(zoneId)
    val end = start.plusDays(5)
    return Pair(start.toString(), end.toString())
}
